package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.Camera2Info
import com.example.ui.theme.CameraDarkBackground
import com.example.ui.theme.CameraProAmber
import com.example.ui.theme.CameraProCyan
import com.example.ui.theme.CameraProGreen
import com.example.ui.theme.CameraSurfaceCard
import com.example.ui.theme.CameraSurfaceHighlight

@Composable
fun RedmiCamera2DiagnosticDialog(
  camera2Info: Camera2Info,
  fpsEstimate: Float,
  onToggleForce60Fps: () -> Unit,
  onDismiss: () -> Unit
) {
  Dialog(onDismissRequest = onDismiss) {
    Card(
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = CameraDarkBackground),
      border = androidx.compose.foundation.BorderStroke(1.dp, CameraProAmber.copy(alpha = 0.5f)),
      modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
        .testTag("redmi_camera2_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
          .verticalScroll(rememberScrollState())
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.PhoneAndroid,
              contentDescription = "Redmi Note 9",
              tint = CameraProAmber,
              modifier = Modifier.size(26.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "Redmi Note 9",
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "Camera2 API & 60 FPS Enforcer",
                color = CameraProAmber,
                fontSize = 11.sp
              )
            }
          }
          IconButton(onClick = onDismiss) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Tutup",
              tint = Color.LightGray
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 1. Hardware Level & Sensor Info
        DiagnosticSection(title = "STATUS HARDWARE CAMERA2") {
          DiagnosticItem(label = "Perangkat", value = camera2Info.deviceModel)
          DiagnosticItem(label = "Lensa Aktif", value = camera2Info.activeLensFacing)
          DiagnosticItem(label = "Level Camera2", value = camera2Info.hardwareLevel, highlight = true)
          DiagnosticItem(label = "Dukungan RAW", value = if (camera2Info.supportsRawSensor) "DIDUKUNG (DNG 14-bit)" else "Tidak")
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 2. Forced 60 FPS Video Pipeline
        DiagnosticSection(title = "FITUR PAKSA 60 FPS (CAMERA2 HOOK)") {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Paksa Rekam 60 FPS",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "Memaksa AE Range [60,60] & Frame 16.6ms untuk bypass batas MIUI 30fps",
                color = Color.LightGray,
                fontSize = 11.sp,
                lineHeight = 14.sp
              )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Switch(
              checked = camera2Info.isForced60FpsEnabled,
              onCheckedChange = { onToggleForce60Fps() },
              colors = SwitchDefaults.colors(
                checkedThumbColor = CameraProGreen,
                checkedTrackColor = CameraProGreen.copy(alpha = 0.5f)
              ),
              modifier = Modifier.testTag("force_60fps_switch")
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Real-time FPS measurement box
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .background(CameraSurfaceHighlight)
              .padding(12.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.Speed,
                  contentDescription = null,
                  tint = if (camera2Info.isForced60FpsEnabled) CameraProGreen else CameraProAmber,
                  modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "FPS Real-Time Viewfinder:",
                  color = Color.White,
                  fontSize = 12.sp
                )
              }
              Text(
                text = "${String.format("%.1f", fpsEstimate)} FPS",
                color = if (camera2Info.isForced60FpsEnabled) CameraProGreen else Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 3. Info Guide for Redmi Note 9
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CameraProAmber.copy(alpha = 0.12f))
            .border(1.dp, CameraProAmber.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
            .padding(12.dp)
        ) {
          Column {
            Text(
              text = "Tips Optimalisasi Redmi Note 9:",
              color = CameraProAmber,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Chipset MediaTek Helio G85 pada Redmi Note 9 mendukung perekaman 60 FPS berkecepatan tinggi melalui Camera2 API interop. Pastikan pencahayaan cukup agar shutter speed tetap di atas 1/60 detik untuk menjaga frame rate 60 fps tetap stabil.",
              color = Color.LightGray,
              fontSize = 11.sp,
              lineHeight = 15.sp
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Close Button
        Button(
          onClick = onDismiss,
          colors = ButtonDefaults.buttonColors(containerColor = CameraProAmber),
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(46.dp)
            .testTag("close_diagnostic_button")
        ) {
          Text(
            text = "Tutup & Mulai Memotret",
            color = Color.Black,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}

@Composable
private fun DiagnosticSection(title: String, content: @Composable () -> Unit) {
  Column(modifier = Modifier.fillMaxWidth()) {
    Text(
      text = title,
      color = CameraProCyan,
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold,
      letterSpacing = 0.5.sp
    )
    Spacer(modifier = Modifier.height(6.dp))
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(12.dp))
        .background(CameraSurfaceCard)
        .padding(12.dp)
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        content()
      }
    }
  }
}

@Composable
private fun DiagnosticItem(label: String, value: String, highlight: Boolean = false) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(text = label, color = Color.LightGray, fontSize = 11.sp)
    Text(
      text = value,
      color = if (highlight) CameraProAmber else Color.White,
      fontSize = 11.sp,
      fontWeight = if (highlight) FontWeight.Bold else FontWeight.Medium,
      fontFamily = FontFamily.Monospace
    )
  }
}
