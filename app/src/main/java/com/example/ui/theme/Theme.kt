package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CameraColorScheme =
  darkColorScheme(
    primary = CameraProAmber,
    onPrimary = Color.Black,
    primaryContainer = CameraSurfaceHighlight,
    onPrimaryContainer = CameraProAmber,
    secondary = CameraProCyan,
    onSecondary = Color.Black,
    tertiary = CameraProRed,
    background = CameraDarkBackground,
    onBackground = CameraTextPrimary,
    surface = CameraSurfaceDark,
    onSurface = CameraTextPrimary,
    surfaceVariant = CameraSurfaceCard,
    onSurfaceVariant = CameraTextSecondary,
    outline = CameraTextDisabled
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = CameraColorScheme,
    typography = Typography,
    content = content
  )
}

