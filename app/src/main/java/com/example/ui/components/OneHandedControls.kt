package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.CameraMode
import com.example.model.CaptureUiState
import com.example.model.OneHandedHand
import com.example.ui.theme.CameraProAmber
import com.example.ui.theme.CameraProCyan
import com.example.ui.theme.CameraProRed
import com.example.ui.theme.CameraSurfaceDark
import com.example.ui.theme.ShutterBorder
import com.example.ui.theme.ShutterRed

@Composable
fun OneHandedControls(
  currentMode: CameraMode,
  oneHandedHand: OneHandedHand,
  zoomRatio: Float,
  captureUiState: CaptureUiState,
  onModeSelected: (CameraMode) -> Unit,
  onOneHandedToggled: () -> Unit,
  onZoomSelected: (Float) -> Unit,
  onShutterClicked: () -> Unit,
  onFlipCameraClicked: () -> Unit,
  onOpenQuickSettings: () -> Unit,
  modifier: Modifier = Modifier
) {
  val modesList = remember { CameraMode.values().toList() }
  val listState = rememberLazyListState()

  // Scroll to selected mode
  LaunchedEffect(currentMode) {
    val index = modesList.indexOf(currentMode)
    if (index >= 0) {
      listState.animateScrollToItem(index)
    }
  }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .background(
        Brush.verticalGradient(
          colors = listOf(
            Color.Transparent,
            Color.Black.copy(alpha = 0.75f),
            Color.Black
          )
        )
      )
      .padding(bottom = 24.dp)
  ) {
    // 1. One-Hand Thumb Zoom Arc Selector (0.6x, 1x, 2x, 5x)
    ThumbZoomSelector(
      zoomRatio = zoomRatio,
      oneHandedHand = oneHandedHand,
      onZoomSelected = onZoomSelected,
      modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(8.dp))

    // 2. Horizontal Mode Dial / Carousel (Reachable with thumb swipe)
    LazyRow(
      state = listState,
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 6.dp),
      horizontalArrangement = Arrangement.Center,
      verticalAlignment = Alignment.CenterVertically
    ) {
      items(modesList) { mode ->
        val isSelected = mode == currentMode
        Box(
          modifier = Modifier
            .padding(horizontal = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
              if (isSelected) CameraProAmber.copy(alpha = 0.25f)
              else Color.Transparent
            )
            .border(
              width = if (isSelected) 1.dp else 0.dp,
              color = if (isSelected) CameraProAmber else Color.Transparent,
              shape = RoundedCornerShape(16.dp)
            )
            .clickable { onModeSelected(mode) }
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .testTag("mode_tab_${mode.name.lowercase()}")
        ) {
          Text(
            text = mode.title,
            color = if (isSelected) CameraProAmber else Color.LightGray,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            letterSpacing = 0.5.sp
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // 3. Ergonomic Bottom Bar: Shutter, Lens Switch, Gallery, One-Hand Toggle
    // The arrangement changes based on whether user is using RIGHT or LEFT thumb!
    val isRightHanded = oneHandedHand == OneHandedHand.RIGHT

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = if (isRightHanded) Arrangement.SpaceBetween else Arrangement.SpaceBetween
    ) {
      if (isRightHanded) {
        // LEFT SIDE (Secondary controls for Right-handed mode)
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          // One-Hand Hand switcher button (Kanan / Kiri)
          OneHandToggleButton(
            oneHandedHand = oneHandedHand,
            onClick = onOneHandedToggled
          )

          // Quick Settings Button
          IconButton(
            onClick = onOpenQuickSettings,
            modifier = Modifier
              .size(46.dp)
              .clip(CircleShape)
              .background(Color.White.copy(alpha = 0.12f))
              .testTag("quick_settings_button")
          ) {
            Icon(
              imageVector = Icons.Default.Tune,
              contentDescription = "Pengaturan Cepat",
              tint = Color.White,
              modifier = Modifier.size(22.dp)
            )
          }
        }

        // RIGHT SIDE (Primary controls clustered near Right Thumb!)
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
          // Camera Flip
          IconButton(
            onClick = onFlipCameraClicked,
            modifier = Modifier
              .size(48.dp)
              .clip(CircleShape)
              .background(Color.White.copy(alpha = 0.15f))
              .testTag("flip_camera_button")
          ) {
            Icon(
              imageVector = Icons.Default.Cameraswitch,
              contentDescription = "Balik Kamera Depan/Belakang",
              tint = Color.White,
              modifier = Modifier.size(24.dp)
            )
          }

          // Shutter Button (Comfortably within Right Thumb reach)
          ErgonomicShutterButton(
            currentMode = currentMode,
            captureUiState = captureUiState,
            onClick = onShutterClicked
          )

          // Gallery Thumbnail
          GalleryPreviewThumbnail(
            lastCapturedUri = captureUiState.lastCapturedUri,
            isVideo = captureUiState.lastCapturedIsVideo
          )
        }
      } else {
        // LEFT-HANDED MODE: Primary shutter controls clustered near Left Thumb!
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
          // Gallery Thumbnail
          GalleryPreviewThumbnail(
            lastCapturedUri = captureUiState.lastCapturedUri,
            isVideo = captureUiState.lastCapturedIsVideo
          )

          // Shutter Button (Within Left Thumb reach)
          ErgonomicShutterButton(
            currentMode = currentMode,
            captureUiState = captureUiState,
            onClick = onShutterClicked
          )

          // Camera Flip
          IconButton(
            onClick = onFlipCameraClicked,
            modifier = Modifier
              .size(48.dp)
              .clip(CircleShape)
              .background(Color.White.copy(alpha = 0.15f))
              .testTag("flip_camera_button")
          ) {
            Icon(
              imageVector = Icons.Default.Cameraswitch,
              contentDescription = "Balik Kamera",
              tint = Color.White,
              modifier = Modifier.size(24.dp)
            )
          }
        }

        // RIGHT SIDE (Secondary controls for Left-handed mode)
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          IconButton(
            onClick = onOpenQuickSettings,
            modifier = Modifier
              .size(46.dp)
              .clip(CircleShape)
              .background(Color.White.copy(alpha = 0.12f))
          ) {
            Icon(
              imageVector = Icons.Default.Tune,
              contentDescription = "Pengaturan Cepat",
              tint = Color.White,
              modifier = Modifier.size(22.dp)
            )
          }

          OneHandToggleButton(
            oneHandedHand = oneHandedHand,
            onClick = onOneHandedToggled
          )
        }
      }
    }
  }
}

@Composable
fun ThumbZoomSelector(
  zoomRatio: Float,
  oneHandedHand: OneHandedHand,
  onZoomSelected: (Float) -> Unit,
  modifier: Modifier = Modifier
) {
  val zoomLevels = listOf(0.6f, 1.0f, 2.0f, 5.0f)
  val isRightHanded = oneHandedHand == OneHandedHand.RIGHT

  Row(
    modifier = modifier.padding(horizontal = 24.dp),
    horizontalArrangement = if (isRightHanded) Arrangement.End else Arrangement.Start,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = Color.Black.copy(alpha = 0.6f),
      border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        zoomLevels.forEach { zoom ->
          val isSelected = (zoomRatio - zoom) in -0.2f..0.2f
          val label = when (zoom) {
            0.6f -> "0.6x"
            1.0f -> "1x"
            2.0f -> "2x"
            else -> "5x"
          }

          Box(
            modifier = Modifier
              .clip(CircleShape)
              .background(if (isSelected) CameraProAmber else Color.Transparent)
              .clickable { onZoomSelected(zoom) }
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Text(
              text = label,
              color = if (isSelected) Color.Black else Color.White,
              fontSize = 11.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
              fontFamily = FontFamily.Monospace
            )
          }
        }
      }
    }
  }
}

@Composable
fun ErgonomicShutterButton(
  currentMode: CameraMode,
  captureUiState: CaptureUiState,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val isRecording = captureUiState.isRecordingVideo
  val isVideoMode = currentMode == CameraMode.VIDEO_60FPS
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()

  val scale = remember { Animatable(1f) }
  LaunchedEffect(isPressed) {
    scale.animateTo(if (isPressed) 0.92f else 1f, tween(100))
  }

  // Pulsing animation for video recording
  val pulseColor by animateColorAsState(
    targetValue = if (isRecording) ShutterRed else Color.White,
    animationSpec = tween(300)
  )

  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    // Shutter Trigger Ring
    Box(
      modifier = modifier
        .size(76.dp)
        .scale(scale.value)
        .clip(CircleShape)
        .background(Color.Transparent)
        .border(4.dp, pulseColor, CircleShape)
        .clickable(
          interactionSource = interactionSource,
          indication = null,
          onClick = onClick
        )
        .padding(6.dp)
        .testTag("camera_shutter_button"),
      contentAlignment = Alignment.Center
    ) {
      if (isVideoMode || isRecording) {
        // Red core for video
        Box(
          modifier = Modifier
            .size(if (isRecording) 32.dp else 56.dp)
            .clip(if (isRecording) RoundedCornerShape(8.dp) else CircleShape)
            .background(ShutterRed)
        )
      } else {
        // Solid white core for photo
        Box(
          modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(Color.White)
        )
      }
    }

    // Recording duration timer tag
    if (isRecording) {
      Spacer(modifier = Modifier.height(4.dp))
      val minutes = captureUiState.recordingSeconds / 60
      val seconds = captureUiState.recordingSeconds % 60
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        Icon(
          imageVector = Icons.Default.FiberManualRecord,
          contentDescription = "REC",
          tint = ShutterRed,
          modifier = Modifier.size(12.dp)
        )
        Text(
          text = String.format("%02d:%02d", minutes, seconds),
          color = Color.White,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace
        )
      }
    }
  }
}

@Composable
fun GalleryPreviewThumbnail(
  lastCapturedUri: String?,
  isVideo: Boolean,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .size(48.dp)
      .clip(RoundedCornerShape(12.dp))
      .background(Color.White.copy(alpha = 0.15f))
      .border(1.5.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(12.dp)),
    contentAlignment = Alignment.Center
  ) {
    if (lastCapturedUri != null && !lastCapturedUri.startsWith("simulated://")) {
      AsyncImage(
        model = lastCapturedUri,
        contentDescription = "Galeri Foto",
        modifier = Modifier.fillMaxWidth()
      )
    } else {
      Icon(
        imageVector = Icons.Default.Image,
        contentDescription = "Galeri Foto Terakhir",
        tint = Color.LightGray,
        modifier = Modifier.size(24.dp)
      )
    }
  }
}

@Composable
fun OneHandToggleButton(
  oneHandedHand: OneHandedHand,
  onClick: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(20.dp),
    color = CameraProAmber.copy(alpha = 0.18f),
    border = androidx.compose.foundation.BorderStroke(1.dp, CameraProAmber.copy(alpha = 0.6f)),
    modifier = Modifier
      .clickable { onClick() }
      .testTag("one_handed_toggle_button")
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = if (oneHandedHand == OneHandedHand.RIGHT) "🖐️ Kanan" else "🖐️ Kiri",
        color = CameraProAmber,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold
      )
    }
  }
}
