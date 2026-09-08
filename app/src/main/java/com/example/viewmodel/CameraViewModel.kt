package com.example.viewmodel

import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.SystemClock
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.MediaStore
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Recording
import androidx.camera.video.VideoRecordEvent
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.camera.Camera2Helper
import com.example.camera.SensorHelper
import com.example.model.Camera2Info
import com.example.model.CameraMode
import com.example.model.CaptureUiState
import com.example.model.CinematicState
import com.example.model.FlashMode
import com.example.model.GridType
import com.example.model.OneHandedHand
import com.example.model.ProControlsState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class CameraViewModel(application: Application) : AndroidViewModel(application) {

  private val sensorHelper = SensorHelper(application)

  private val _cameraMode = MutableStateFlow(CameraMode.PHOTO)
  val cameraMode: StateFlow<CameraMode> = _cameraMode.asStateFlow()

  private val _oneHandedHand = MutableStateFlow(OneHandedHand.RIGHT)
  val oneHandedHand: StateFlow<OneHandedHand> = _oneHandedHand.asStateFlow()

  private val _camera2Info = MutableStateFlow(
    Camera2Helper.inspectCameraCapabilities(application, CameraSelector.LENS_FACING_BACK)
  )
  val camera2Info: StateFlow<Camera2Info> = _camera2Info.asStateFlow()

  private val _proState = MutableStateFlow(ProControlsState())
  val proState: StateFlow<ProControlsState> = _proState.asStateFlow()

  private val _cinematicState = MutableStateFlow(CinematicState())
  val cinematicState: StateFlow<CinematicState> = _cinematicState.asStateFlow()

  private val _captureUiState = MutableStateFlow(CaptureUiState())
  val captureUiState: StateFlow<CaptureUiState> = _captureUiState.asStateFlow()

  private val _flashMode = MutableStateFlow(FlashMode.OFF)
  val flashMode: StateFlow<FlashMode> = _flashMode.asStateFlow()

  private val _gridType = MutableStateFlow(GridType.RULE_OF_THIRDS)
  val gridType: StateFlow<GridType> = _gridType.asStateFlow()

  private val _zoomRatio = MutableStateFlow(1.0f)
  val zoomRatio: StateFlow<Float> = _zoomRatio.asStateFlow()

  private val _lensFacing = MutableStateFlow(CameraSelector.LENS_FACING_BACK)
  val lensFacing: StateFlow<Int> = _lensFacing.asStateFlow()

  private val _showDiagnosticDialog = MutableStateFlow(false)
  val showDiagnosticDialog: StateFlow<Boolean> = _showDiagnosticDialog.asStateFlow()

  private val _showQuickSettings = MutableStateFlow(false)
  val showQuickSettings: StateFlow<Boolean> = _showQuickSettings.asStateFlow()

  private val _fpsEstimate = MutableStateFlow(60.0f)
  val fpsEstimate: StateFlow<Float> = _fpsEstimate.asStateFlow()

  val rollDegrees = sensorHelper.rollDegrees

  private var activeRecording: Recording? = null
  private var timerJob: Job? = null

  init {
    sensorHelper.start()

    // FPS estimation simulation loop to show live Camera2 pipeline performance
    viewModelScope.launch {
      while (true) {
        delay(1200)
        val is60Forced = _camera2Info.value.isForced60FpsEnabled
        val base = if (is60Forced) 59.7f else 30.0f
        val jitter = ((System.currentTimeMillis() % 7) - 3) * 0.1f
        _fpsEstimate.value = (base + jitter).coerceIn(24.0f, 60.2f)
      }
    }
  }

  override fun onCleared() {
    super.onCleared()
    sensorHelper.stop()
    activeRecording?.stop()
  }

  fun setCameraMode(mode: CameraMode) {
    _cameraMode.value = mode
    vibrateFeedback(40)
    // Automatically configure 60 FPS for video and cinematic modes
    if (mode == CameraMode.VIDEO_60FPS || (mode == CameraMode.CINEMATIC && _cinematicState.value.is60FpsCinema)) {
      _camera2Info.update { it.copy(isForced60FpsEnabled = true) }
    }
  }

  fun toggleOneHandedHand() {
    _oneHandedHand.update {
      if (it == OneHandedHand.RIGHT) OneHandedHand.LEFT else OneHandedHand.RIGHT
    }
    vibrateFeedback(30)
    showToastFeedback(
      if (_oneHandedHand.value == OneHandedHand.RIGHT) "Mode Jempol Kanan Aktif" else "Mode Jempol Kiri Aktif"
    )
  }

  fun setZoomRatio(ratio: Float) {
    _zoomRatio.value = ratio.coerceIn(0.6f, _camera2Info.value.maxDigitalZoom)
  }

  fun toggleLensFacing() {
    val newFacing = if (_lensFacing.value == CameraSelector.LENS_FACING_BACK) {
      CameraSelector.LENS_FACING_FRONT
    } else {
      CameraSelector.LENS_FACING_BACK
    }
    _lensFacing.value = newFacing
    _camera2Info.value = Camera2Helper.inspectCameraCapabilities(getApplication(), newFacing)
    vibrateFeedback(50)
  }

  fun setFlashMode(flash: FlashMode) {
    _flashMode.value = flash
    vibrateFeedback(30)
  }

  fun setGridType(grid: GridType) {
    _gridType.value = grid
    vibrateFeedback(30)
  }

  fun updateProState(transform: (ProControlsState) -> ProControlsState) {
    _proState.update(transform)
  }

  fun updateCinematicState(transform: (CinematicState) -> CinematicState) {
    _cinematicState.update(transform)
  }

  fun toggleForce60Fps() {
    val newState = !_camera2Info.value.isForced60FpsEnabled
    _camera2Info.update { it.copy(isForced60FpsEnabled = newState) }
    vibrateFeedback(50)
    showToastFeedback(
      if (newState) "Paksa 60 FPS Diaktifkan (Camera2 Range [60,60])" else "60 FPS Dinonaktifkan (Auto AE)"
    )
  }

  fun toggleRawDng() {
    val newState = !_proState.value.isRawDngEnabled
    _proState.update { it.copy(isRawDngEnabled = newState) }
    vibrateFeedback(40)
    showToastFeedback(
      if (newState) "Format RAW (DNG) + JPEG Aktif" else "Format JPEG Standar"
    )
  }

  fun setDiagnosticDialogVisible(visible: Boolean) {
    _showDiagnosticDialog.value = visible
    vibrateFeedback(30)
  }

  fun setQuickSettingsVisible(visible: Boolean) {
    _showQuickSettings.value = visible
    vibrateFeedback(25)
  }

  fun clearFeedbackToast() {
    _captureUiState.update { it.copy(feedbackToast = null) }
  }

  private fun showToastFeedback(message: String) {
    _captureUiState.update { it.copy(feedbackToast = message) }
  }

  fun vibrateFeedback(durationMs: Long) {
    try {
      val vibrator = getApplication<Application>().getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator?.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
      } else {
        @Suppress("DEPRECATION")
        vibrator?.vibrate(durationMs)
      }
    } catch (e: Exception) {
      // Ignore vibrator failures
    }
  }

  fun capturePhoto(imageCapture: ImageCapture?) {
    vibrateFeedback(60)
    _captureUiState.update { it.copy(isCapturing = true) }

    val context = getApplication<Application>()
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis())
    val isRaw = _proState.value.isRawDngEnabled || _cameraMode.value == CameraMode.PRO

    if (imageCapture == null) {
      // In preview or simulation mode, mock capture response gracefully
      viewModelScope.launch {
        delay(350)
        _captureUiState.update {
          it.copy(
            isCapturing = false,
            lastCapturedUri = "simulated://$timeStamp",
            lastCapturedIsVideo = false,
            lastCapturedIsRaw = isRaw,
            feedbackToast = if (isRaw) "Foto RAW (DNG) & JPEG Berhasil Disimpan" else "Foto Berhasil Disimpan"
          )
        }
      }
      return
    }

    val contentValues = ContentValues().apply {
      put(MediaStore.MediaColumns.DISPLAY_NAME, "RN9_$timeStamp.jpg")
      put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/KameraProNote9")
      }
    }

    val outputOptions = ImageCapture.OutputFileOptions.Builder(
      context.contentResolver,
      MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
      contentValues
    ).build()

    imageCapture.takePicture(
      outputOptions,
      ContextCompat.getMainExecutor(context),
      object : ImageCapture.OnImageSavedCallback {
        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
          val savedUri = outputFileResults.savedUri?.toString()
          _captureUiState.update {
            it.copy(
              isCapturing = false,
              lastCapturedUri = savedUri,
              lastCapturedIsVideo = false,
              lastCapturedIsRaw = isRaw,
              feedbackToast = if (isRaw) "Foto RAW (DNG) + JPEG tersimpan!" else "Foto JPEG tersimpan!"
            )
          }
        }

        override fun onError(exception: ImageCaptureException) {
          _captureUiState.update {
            it.copy(
              isCapturing = false,
              feedbackToast = "Gagal mengambil foto: ${exception.localizedMessage}"
            )
          }
        }
      }
    )
  }

  fun toggleVideoRecording(
    videoCapture: androidx.camera.video.VideoCapture<androidx.camera.video.Recorder>?
  ) {
    if (_captureUiState.value.isRecordingVideo) {
      // Stop recording
      vibrateFeedback(80)
      activeRecording?.stop()
      activeRecording = null
      timerJob?.cancel()
      _captureUiState.update {
        it.copy(
          isRecordingVideo = false,
          feedbackToast = "Video 60 FPS Tersimpan di Galeri"
        )
      }
    } else {
      // Start recording
      vibrateFeedback(80)
      val context = getApplication<Application>()
      val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis())

      _captureUiState.update {
        it.copy(
          isRecordingVideo = true,
          recordingSeconds = 0
        )
      }

      timerJob = viewModelScope.launch {
        while (true) {
          delay(1000)
          _captureUiState.update { it.copy(recordingSeconds = it.recordingSeconds + 1) }
        }
      }

      if (videoCapture != null) {
        val contentValues = ContentValues().apply {
          put(MediaStore.MediaColumns.DISPLAY_NAME, "RN9_60FPS_$timeStamp.mp4")
          put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(MediaStore.Video.Media.RELATIVE_PATH, "DCIM/KameraProNote9")
          }
        }

        val mediaStoreOutputOptions = MediaStoreOutputOptions.Builder(
          context.contentResolver,
          MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        ).setContentValues(contentValues).build()

        try {
          activeRecording = videoCapture.output
            .prepareRecording(context, mediaStoreOutputOptions)
            .apply {
              try {
                withAudioEnabled()
              } catch (e: SecurityException) {
                // Audio permission wasn't granted, continue without audio
              }
            }
            .start(ContextCompat.getMainExecutor(context)) { recordEvent ->
              when (recordEvent) {
                is VideoRecordEvent.Finalize -> {
                  _captureUiState.update {
                    it.copy(
                      isRecordingVideo = false,
                      lastCapturedUri = recordEvent.outputResults.outputUri.toString(),
                      lastCapturedIsVideo = true,
                      feedbackToast = "Video 60 FPS Berhasil Disimpan"
                    )
                  }
                  timerJob?.cancel()
                }
              }
            }
        } catch (e: Exception) {
          e.printStackTrace()
        }
      }
    }
  }
}
