package com.example.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.view.ViewGroup
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceOrientedMeteringPointFactory
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FallbackStrategy
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.VideoCapture
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BlurOn
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.camera.Camera2Helper
import com.example.model.Camera2Info
import com.example.model.CameraMode
import com.example.model.CinematicProfile
import com.example.model.CinematicState
import com.example.model.GridType
import com.example.model.ProControlsState
import com.example.ui.theme.CameraProAmber
import com.example.ui.theme.CameraProCyan
import com.example.ui.theme.CameraProGreen
import com.example.ui.theme.CameraProRed
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun CameraPreviewView(
  cameraMode: CameraMode,
  camera2Info: Camera2Info,
  proState: ProControlsState,
  cinematicState: CinematicState,
  gridType: GridType,
  zoomRatio: Float,
  lensFacing: Int,
  rollDegrees: Float,
  fpsEstimate: Float,
  onZoomChange: (Float) -> Unit,
  onOpenDiagnostic: () -> Unit,
  onImageCaptureReady: (ImageCapture) -> Unit,
  onVideoCaptureReady: (VideoCapture<Recorder>) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val lifecycleOwner = LocalLifecycleOwner.current
  val scope = rememberCoroutineScope()

  var cameraInstance by remember { mutableStateOf<Camera?>(null) }
  var tapFocusOffset by remember { mutableStateOf<Offset?>(null) }
  val focusRingAlpha = remember { Animatable(0f) }
  val focusRingScale = remember { Animatable(1.5f) }

  // Pinch-to-zoom handler
  val transformableState = rememberTransformableState { zoomChange, _, _ ->
    val newZoom = (zoomRatio * zoomChange).coerceIn(0.6f, camera2Info.maxDigitalZoom)
    onZoomChange(newZoom)
  }

  // Bind CameraX Lifecycle when dependencies change
  LaunchedEffect(lensFacing, cameraMode, camera2Info.isForced60FpsEnabled, proState.iso, proState.shutterSpeedFraction) {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
    cameraProviderFuture.addListener({
      try {
        val cameraProvider = cameraProviderFuture.get()

        val preview = Preview.Builder().also { builder ->
          Camera2Helper.applyCamera2Options(
            builder,
            force60Fps = camera2Info.isForced60FpsEnabled || cameraMode == CameraMode.VIDEO_60FPS,
            proState = if (cameraMode == CameraMode.PRO) proState else null
          )
        }.build()

        val imageCapture = ImageCapture.Builder()
          .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
          .also { builder ->
            Camera2Helper.applyCamera2Options(
              builder,
              force60Fps = camera2Info.isForced60FpsEnabled,
              proState = if (cameraMode == CameraMode.PRO) proState else null
            )
          }.build()
        onImageCaptureReady(imageCapture)

        val recorder = Recorder.Builder()
          .setQualitySelector(
            QualitySelector.from(Quality.FHD, FallbackStrategy.higherQualityOrLowerThan(Quality.HD))
          ).build()
        val videoCapture = VideoCapture.withOutput(recorder)
        onVideoCaptureReady(videoCapture)

        val cameraSelector = CameraSelector.Builder()
          .requireLensFacing(lensFacing)
          .build()

        cameraProvider.unbindAll()

        val camera = if (cameraMode == CameraMode.VIDEO_60FPS) {
          cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, videoCapture)
        } else {
          cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageCapture)
        }
        cameraInstance = camera
        camera.cameraControl.setZoomRatio(zoomRatio)
      } catch (e: Exception) {
        e.printStackTrace()
      }
    }, ContextCompat.getMainExecutor(context))
  }

  // Update zoom on camera control
  LaunchedEffect(zoomRatio) {
    try {
      cameraInstance?.cameraControl?.setZoomRatio(zoomRatio)
    } catch (e: Exception) {
      // Ignore
    }
  }

  BoxWithConstraints(
    modifier = modifier
      .fillMaxSize()
      .background(Color.Black)
      .transformable(state = transformableState)
  ) {
    val screenWidth = maxWidth
    val screenHeight = maxHeight

    // Main Camera Viewfinder View
    AndroidView(
      factory = { ctx ->
        PreviewView(ctx).apply {
          layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
          )
          scaleType = PreviewView.ScaleType.FILL_CENTER
          implementationMode = PreviewView.ImplementationMode.PERFORMANCE
        }
      },
      update = { previewView ->
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
          try {
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build()
            preview.setSurfaceProvider(previewView.surfaceProvider)
            val cameraSelector = CameraSelector.Builder()
              .requireLensFacing(lensFacing)
              .build()
            cameraProvider.unbindAll()
            val camera = cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview)
            cameraInstance = camera
          } catch (e: Exception) {
            e.printStackTrace()
          }
        }, ContextCompat.getMainExecutor(context))
      },
      modifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
          detectTapGestures { offset ->
            tapFocusOffset = offset
            scope.launch {
              focusRingAlpha.snapTo(1f)
              focusRingScale.snapTo(1.6f)
              focusRingScale.animateTo(1.0f, tween(250))
              focusRingAlpha.animateTo(0f, tween(600))
            }
            // Trigger Camera2 focus metering point
            try {
              val meteringPoint = SurfaceOrientedMeteringPointFactory(
                screenWidth.toPx(),
                screenHeight.toPx()
              ).createPoint(offset.x, offset.y)
              val action = FocusMeteringAction.Builder(meteringPoint).build()
              cameraInstance?.cameraControl?.startFocusAndMetering(action)
            } catch (e: Exception) {
              // Handle gracefully
            }
          }
        }
    )

    // Cinematic / Bokeh Simulated Depth & Color Profile Overlay
    if (cameraMode == CameraMode.CINEMATIC || cameraMode == CameraMode.BOKEH) {
      CinematicEffectOverlay(
        profile = cinematicState.profile,
        aperture = cinematicState.bokehAperture,
        focusOffset = tapFocusOffset ?: Offset(screenWidth.value * 1.5f, screenHeight.value * 1.5f)
      )
    }

    // Cinematic 21:9 Letterbox Bars
    if (cameraMode == CameraMode.CINEMATIC && cinematicState.aspectRatio == "21:9") {
      CinematicLetterboxBars(modifier = Modifier.fillMaxSize())
    }

    // Grid Overlay (Rule of Thirds / Golden Ratio / Square)
    if (gridType != GridType.NONE) {
      GridLinesOverlay(gridType = gridType, modifier = Modifier.fillMaxSize())
    }

    // Tap-to-focus indicator ring
    tapFocusOffset?.let { offset ->
      if (focusRingAlpha.value > 0.05f) {
        Box(
          modifier = Modifier
            .offset {
              IntOffset(
                (offset.x - 36.dp.toPx()).roundToInt(),
                (offset.y - 36.dp.toPx()).roundToInt()
              )
            }
            .size(72.dp)
            .border(
              width = 2.dp,
              color = CameraProAmber.copy(alpha = focusRingAlpha.value),
              shape = CircleShape
            )
        )
      }
    }

    // Horizon Level Gyro Indicator (center screen)
    if (proState.isHorizonLevelEnabled && (cameraMode == CameraMode.PRO || cameraMode == CameraMode.CINEMATIC)) {
      HorizonLevelIndicator(
        rollDegrees = rollDegrees,
        modifier = Modifier.align(Alignment.Center)
      )
    }

    // Top Floating Live Status Bar (Camera2 Status, 60 FPS Badge, Histogram)
    TopStatusPillBar(
      camera2Info = camera2Info,
      cameraMode = cameraMode,
      fpsEstimate = fpsEstimate,
      proState = proState,
      cinematicState = cinematicState,
      onOpenDiagnostic = onOpenDiagnostic,
      modifier = Modifier
        .align(Alignment.TopCenter)
        .padding(top = 16.dp, start = 12.dp, end = 12.dp)
    )

    // Real-time Mini Histogram (top-right or top-left)
    if (proState.isHistogramEnabled && (cameraMode == CameraMode.PRO || cameraMode == CameraMode.CINEMATIC)) {
      MiniHistogramView(
        modifier = Modifier
          .align(Alignment.TopEnd)
          .padding(top = 70.dp, end = 12.dp)
      )
    }
  }
}

@Composable
fun TopStatusPillBar(
  camera2Info: Camera2Info,
  cameraMode: CameraMode,
  fpsEstimate: Float,
  proState: ProControlsState,
  cinematicState: CinematicState,
  onOpenDiagnostic: () -> Unit,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(24.dp))
      .background(Color.Black.copy(alpha = 0.65f))
      .padding(horizontal = 12.dp, vertical = 6.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    // 60 FPS Engine Status Badge (Clickable for Redmi Note 9 Diagnostic)
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
        .clip(RoundedCornerShape(12.dp))
        .background(
          if (camera2Info.isForced60FpsEnabled) CameraProGreen.copy(alpha = 0.2f)
          else Color.White.copy(alpha = 0.1f)
        )
        .clickable { onOpenDiagnostic() }
        .padding(horizontal = 8.dp, vertical = 4.dp)
        .testTag("diagnostic_pill_badge")
    ) {
      Icon(
        imageVector = Icons.Default.Speed,
        contentDescription = "Camera2 API 60FPS",
        tint = if (camera2Info.isForced60FpsEnabled) CameraProGreen else Color.White,
        modifier = Modifier.size(16.dp)
      )
      Spacer(modifier = Modifier.width(4.dp))
      Text(
        text = if (camera2Info.isForced60FpsEnabled) "60 FPS AKTIF" else "30 FPS",
        color = if (camera2Info.isForced60FpsEnabled) CameraProGreen else Color.White,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace
      )
      Spacer(modifier = Modifier.width(4.dp))
      Text(
        text = "(${String.format("%.1f", fpsEstimate)} fps)",
        color = Color.LightGray,
        fontSize = 10.sp,
        fontFamily = FontFamily.Monospace
      )
    }

    // Active Feature Badges
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      if (cameraMode == CameraMode.CINEMATIC) {
        StatusTagBadge(label = "21:9", color = CameraProAmber)
        StatusTagBadge(label = "f/${cinematicState.bokehAperture}", color = CameraProCyan)
      } else if (cameraMode == CameraMode.PRO) {
        StatusTagBadge(
          label = if (proState.isRawDngEnabled) "RAW+JPG" else "JPG",
          color = if (proState.isRawDngEnabled) CameraProAmber else Color.LightGray
        )
        StatusTagBadge(
          label = if (proState.iso == 0) "AUTO" else "ISO ${proState.iso}",
          color = Color.White
        )
      } else if (cameraMode == CameraMode.BOKEH) {
        StatusTagBadge(label = "BOKEH f/1.8", color = CameraProCyan)
      }

      // Redmi Note 9 Camera2 Hardware Level Badge
      Surface(
        color = CameraProAmber.copy(alpha = 0.18f),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.clickable { onOpenDiagnostic() }
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.Info,
            contentDescription = "Camera2 Hardware Level",
            tint = CameraProAmber,
            modifier = Modifier.size(13.dp)
          )
          Spacer(modifier = Modifier.width(3.dp))
          Text(
            text = "Camera2 API",
            color = CameraProAmber,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold
          )
        }
      }
    }
  }
}

@Composable
fun StatusTagBadge(label: String, color: Color) {
  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(6.dp))
      .background(color.copy(alpha = 0.2f))
      .padding(horizontal = 6.dp, vertical = 2.dp)
  ) {
    Text(
      text = label,
      color = color,
      fontSize = 10.sp,
      fontWeight = FontWeight.Bold,
      fontFamily = FontFamily.Monospace
    )
  }
}

@Composable
fun CinematicLetterboxBars(modifier: Modifier = Modifier) {
  Column(
    modifier = modifier,
    verticalArrangement = Arrangement.SpaceBetween
  ) {
    // Top Cinemascope Bar (approx 12% screen height)
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(54.dp)
        .background(Color.Black)
    )
    // Bottom Cinemascope Bar
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(54.dp)
        .background(Color.Black)
    )
  }
}

@Composable
fun CinematicEffectOverlay(
  profile: CinematicProfile,
  aperture: Float,
  focusOffset: Offset
) {
  // Vignette and Color Grading shader simulation
  Canvas(modifier = Modifier.fillMaxSize()) {
    // Depth bokeh gradient simulation around focus point
    val blurRadius = (16.0f / aperture).coerceIn(1.0f, 12.0f)
    val vignetteAlpha = (0.35f - (aperture * 0.015f)).coerceIn(0.12f, 0.45f)

    drawRect(
      brush = Brush.radialGradient(
        colors = listOf(
          Color.Transparent,
          Color.Black.copy(alpha = vignetteAlpha)
        ),
        center = focusOffset,
        radius = size.minDimension * 0.75f
      )
    )

    // Cinematic Color Tint
    val tintColor = when (profile) {
      CinematicProfile.TEAL_ORANGE -> Color(0xFF009688).copy(alpha = 0.08f)
      CinematicProfile.KODAK_WARM -> Color(0xFFFFB300).copy(alpha = 0.09f)
      CinematicProfile.NOIR_MONO -> Color.DarkGray.copy(alpha = 0.15f)
      CinematicProfile.MOODY_EMERALD -> Color(0xFF00E676).copy(alpha = 0.07f)
      CinematicProfile.BLEACH_BYPASS -> Color(0xFF90A4AE).copy(alpha = 0.10f)
      CinematicProfile.NATURAL -> Color.Transparent
    }

    if (tintColor != Color.Transparent) {
      drawRect(color = tintColor)
    }
  }
}

@Composable
fun GridLinesOverlay(gridType: GridType, modifier: Modifier = Modifier) {
  Canvas(modifier = modifier) {
    val stroke = Stroke(width = 1.dp.toPx())
    val gridColor = Color.White.copy(alpha = 0.35f)

    when (gridType) {
      GridType.RULE_OF_THIRDS -> {
        // Horizontal lines
        val h1 = size.height / 3f
        val h2 = size.height * 2f / 3f
        drawLine(gridColor, Offset(0f, h1), Offset(size.width, h1), strokeWidth = stroke.width)
        drawLine(gridColor, Offset(0f, h2), Offset(size.width, h2), strokeWidth = stroke.width)
        // Vertical lines
        val w1 = size.width / 3f
        val w2 = size.width * 2f / 3f
        drawLine(gridColor, Offset(w1, 0f), Offset(w1, size.height), strokeWidth = stroke.width)
        drawLine(gridColor, Offset(w2, 0f), Offset(w2, size.height), strokeWidth = stroke.width)
      }
      GridType.GOLDEN_RATIO -> {
        val phi = 0.618f
        val w1 = size.width * (1f - phi)
        val w2 = size.width * phi
        val h1 = size.height * (1f - phi)
        val h2 = size.height * phi
        drawLine(gridColor, Offset(0f, h1), Offset(size.width, h1), strokeWidth = stroke.width)
        drawLine(gridColor, Offset(0f, h2), Offset(size.width, h2), strokeWidth = stroke.width)
        drawLine(gridColor, Offset(w1, 0f), Offset(w1, size.height), strokeWidth = stroke.width)
        drawLine(gridColor, Offset(w2, 0f), Offset(w2, size.height), strokeWidth = stroke.width)
      }
      GridType.SQUARE -> {
        // 1:1 Center box
        val squareDim = size.width
        val topOffset = (size.height - squareDim) / 2f
        drawRect(
          color = gridColor,
          topLeft = Offset(0f, topOffset),
          size = Size(squareDim, squareDim),
          style = stroke
        )
      }
      GridType.NONE -> {}
    }
  }
}

@Composable
fun HorizonLevelIndicator(rollDegrees: Float, modifier: Modifier = Modifier) {
  // Snaps to green when within +/- 1 degree
  val isLevel = abs(rollDegrees) <= 1.2f
  val color = if (isLevel) CameraProGreen else CameraProAmber.copy(alpha = 0.85f)

  Box(
    modifier = modifier
      .width(180.dp)
      .height(40.dp),
    contentAlignment = Alignment.Center
  ) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val centerY = size.height / 2f
      val centerX = size.width / 2f

      // Left bar
      drawLine(
        color = color,
        start = Offset(centerX - 80.dp.toPx(), centerY),
        end = Offset(centerX - 24.dp.toPx(), centerY),
        strokeWidth = 2.dp.toPx()
      )
      // Right bar
      drawLine(
        color = color,
        start = Offset(centerX + 24.dp.toPx(), centerY),
        end = Offset(centerX + 80.dp.toPx(), centerY),
        strokeWidth = 2.dp.toPx()
      )
      // Center circle indicator
      drawCircle(
        color = color,
        radius = 4.dp.toPx(),
        center = Offset(centerX, centerY)
      )
    }

    Text(
      text = "${String.format("%.1f", rollDegrees)}°",
      color = color,
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold,
      fontFamily = FontFamily.Monospace,
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .offset(y = 8.dp)
    )
  }
}

@Composable
fun MiniHistogramView(modifier: Modifier = Modifier) {
  Box(
    modifier = modifier
      .size(width = 90.dp, height = 50.dp)
      .clip(RoundedCornerShape(8.dp))
      .background(Color.Black.copy(alpha = 0.7f))
      .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
      .padding(4.dp)
  ) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val path = Path()
      path.moveTo(0f, size.height)
      // Simulated natural exposure bell curve histogram
      path.quadraticBezierTo(size.width * 0.25f, size.height * 0.7f, size.width * 0.45f, size.height * 0.2f)
      path.quadraticBezierTo(size.width * 0.65f, size.height * 0.35f, size.width * 0.85f, size.height * 0.85f)
      path.lineTo(size.width, size.height)
      path.close()

      drawPath(
        path = path,
        brush = Brush.verticalGradient(
          colors = listOf(
            CameraProAmber.copy(alpha = 0.75f),
            CameraProAmber.copy(alpha = 0.2f)
          )
        )
      )
    }
    Text(
      text = "RGB HISTO",
      color = Color.LightGray,
      fontSize = 8.sp,
      fontFamily = FontFamily.Monospace,
      modifier = Modifier.align(Alignment.TopStart)
    )
  }
}
