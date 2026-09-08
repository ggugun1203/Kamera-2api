package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.camera.video.Recorder
import androidx.camera.video.VideoCapture
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.CameraMode
import com.example.ui.components.CameraPreviewView
import com.example.ui.components.CinematicControlsBar
import com.example.ui.components.OneHandedControls
import com.example.ui.components.ProControlsBar
import com.example.ui.components.QuickSettingsDrawer
import com.example.ui.components.RedmiCamera2DiagnosticDialog
import com.example.ui.theme.CameraDarkBackground
import com.example.ui.theme.CameraProAmber
import com.example.ui.theme.CameraProCyan
import com.example.ui.theme.CameraProGreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.CameraViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        CameraAppRoot()
      }
    }
  }
}

@Composable
fun CameraAppRoot(viewModel: CameraViewModel = viewModel()) {
  val context = LocalContext.current

  var hasCameraPermission by remember {
    mutableStateOf(
      ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    )
  }

  val permissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestMultiplePermissions()
  ) { permissions ->
    hasCameraPermission = permissions[Manifest.permission.CAMERA] == true
  }

  LaunchedEffect(Unit) {
    if (!hasCameraPermission) {
      permissionLauncher.launch(
        arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
      )
    }
  }

  if (hasCameraPermission) {
    CameraMainScreen(viewModel = viewModel)
  } else {
    PermissionRequestScreen(
      onRequestPermission = {
        permissionLauncher.launch(
          arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
        )
      }
    )
  }
}

@Composable
fun CameraMainScreen(viewModel: CameraViewModel) {
  val cameraMode by viewModel.cameraMode.collectAsStateWithLifecycle()
  val oneHandedHand by viewModel.oneHandedHand.collectAsStateWithLifecycle()
  val camera2Info by viewModel.camera2Info.collectAsStateWithLifecycle()
  val proState by viewModel.proState.collectAsStateWithLifecycle()
  val cinematicState by viewModel.cinematicState.collectAsStateWithLifecycle()
  val captureUiState by viewModel.captureUiState.collectAsStateWithLifecycle()
  val flashMode by viewModel.flashMode.collectAsStateWithLifecycle()
  val gridType by viewModel.gridType.collectAsStateWithLifecycle()
  val zoomRatio by viewModel.zoomRatio.collectAsStateWithLifecycle()
  val lensFacing by viewModel.lensFacing.collectAsStateWithLifecycle()
  val showDiagnosticDialog by viewModel.showDiagnosticDialog.collectAsStateWithLifecycle()
  val showQuickSettings by viewModel.showQuickSettings.collectAsStateWithLifecycle()
  val fpsEstimate by viewModel.fpsEstimate.collectAsStateWithLifecycle()
  val rollDegrees by viewModel.rollDegrees.collectAsStateWithLifecycle()

  var imageCaptureInstance by remember { mutableStateOf<ImageCapture?>(null) }
  var videoCaptureInstance by remember { mutableStateOf<VideoCapture<Recorder>?>(null) }

  // Floating feedback banner
  val context = LocalContext.current
  LaunchedEffect(captureUiState.feedbackToast) {
    captureUiState.feedbackToast?.let { msg ->
      Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
      viewModel.clearFeedbackToast()
    }
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(CameraDarkBackground)
  ) {
    // 1. Live Camera Preview Viewfinder with Overlays
    CameraPreviewView(
      cameraMode = cameraMode,
      camera2Info = camera2Info,
      proState = proState,
      cinematicState = cinematicState,
      gridType = gridType,
      zoomRatio = zoomRatio,
      lensFacing = lensFacing,
      rollDegrees = rollDegrees,
      fpsEstimate = fpsEstimate,
      onZoomChange = { viewModel.setZoomRatio(it) },
      onOpenDiagnostic = { viewModel.setDiagnosticDialogVisible(true) },
      onImageCaptureReady = { imageCaptureInstance = it },
      onVideoCaptureReady = { videoCaptureInstance = it },
      modifier = Modifier.fillMaxSize()
    )

    // 2. Lower Ergonomic Thumb Zone (Mode specific Pro/Cine bar + 1-Handed Controls)
    Column(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .fillMaxWidth()
        .navigationBarsPadding()
    ) {
      // Pro Controls Bar (when in PRO mode)
      AnimatedVisibility(
        visible = cameraMode == CameraMode.PRO,
        enter = slideInVertically { it } + fadeIn(),
        exit = slideOutVertically { it } + fadeOut()
      ) {
        ProControlsBar(
          proState = proState,
          onUpdateProState = { viewModel.updateProState(it) }
        )
      }

      // Cinematic Controls Bar (when in CINEMATIC or BOKEH mode)
      AnimatedVisibility(
        visible = cameraMode == CameraMode.CINEMATIC || cameraMode == CameraMode.BOKEH,
        enter = slideInVertically { it } + fadeIn(),
        exit = slideOutVertically { it } + fadeOut()
      ) {
        CinematicControlsBar(
          cinematicState = cinematicState,
          onUpdateCinematicState = { viewModel.updateCinematicState(it) }
        )
      }

      // One-Handed Ergonomic Controls (Mode Dial, Shutter, Zoom, Thumb Switcher)
      OneHandedControls(
        currentMode = cameraMode,
        oneHandedHand = oneHandedHand,
        zoomRatio = zoomRatio,
        captureUiState = captureUiState,
        onModeSelected = { viewModel.setCameraMode(it) },
        onOneHandedToggled = { viewModel.toggleOneHandedHand() },
        onZoomSelected = { viewModel.setZoomRatio(it) },
        onShutterClicked = {
          if (cameraMode == CameraMode.VIDEO_60FPS) {
            viewModel.toggleVideoRecording(videoCaptureInstance)
          } else {
            viewModel.capturePhoto(imageCaptureInstance)
          }
        },
        onFlipCameraClicked = { viewModel.toggleLensFacing() },
        onOpenQuickSettings = { viewModel.setQuickSettingsVisible(true) }
      )
    }

    // 3. Redmi Note 9 Camera2 & 60 FPS Diagnostic Sheet
    if (showDiagnosticDialog) {
      RedmiCamera2DiagnosticDialog(
        camera2Info = camera2Info,
        fpsEstimate = fpsEstimate,
        onToggleForce60Fps = { viewModel.toggleForce60Fps() },
        onDismiss = { viewModel.setDiagnosticDialogVisible(false) }
      )
    }

    // 4. Quick Settings Sheet
    if (showQuickSettings) {
      QuickSettingsDrawer(
        flashMode = flashMode,
        gridType = gridType,
        isForced60Fps = camera2Info.isForced60FpsEnabled,
        isRawEnabled = proState.isRawDngEnabled,
        isHorizonEnabled = proState.isHorizonLevelEnabled,
        isHistoEnabled = proState.isHistogramEnabled,
        onFlashChange = { viewModel.setFlashMode(it) },
        onGridChange = { viewModel.setGridType(it) },
        onToggleForce60Fps = { viewModel.toggleForce60Fps() },
        onToggleRaw = { viewModel.toggleRawDng() },
        onToggleHorizon = {
          viewModel.updateProState { it.copy(isHorizonLevelEnabled = !it.isHorizonLevelEnabled) }
        },
        onToggleHisto = {
          viewModel.updateProState { it.copy(isHistogramEnabled = !it.isHistogramEnabled) }
        },
        onDismiss = { viewModel.setQuickSettingsVisible(false) }
      )
    }
  }
}

@Composable
fun PermissionRequestScreen(onRequestPermission: () -> Unit) {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(CameraDarkBackground)
      .padding(24.dp),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Icon(
        imageVector = Icons.Default.CameraAlt,
        contentDescription = "Izin Kamera",
        tint = CameraProAmber,
        modifier = Modifier.size(64.dp)
      )
      Spacer(modifier = Modifier.height(16.dp))
      Text(
        text = "Kamera Pro Redmi Note 9",
        color = Color.White,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold
      )
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = stringResource(R.string.camera_permission_required),
        color = Color.LightGray,
        fontSize = 13.sp,
        textAlign = TextAlign.Center
      )
      Spacer(modifier = Modifier.height(24.dp))
      Button(
        onClick = onRequestPermission,
        colors = ButtonDefaults.buttonColors(containerColor = CameraProAmber),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.testTag("request_permission_button")
      ) {
        Text(
          text = stringResource(R.string.grant_permission),
          color = Color.Black,
          fontWeight = FontWeight.Bold
        )
      }
    }
  }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  Text(text = "Hello $name!", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  MyApplicationTheme { Greeting("Redmi Note 9") }
}
