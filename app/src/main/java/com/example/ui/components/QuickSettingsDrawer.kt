package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashAuto
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Highlight
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.FlashMode
import com.example.model.GridType
import com.example.ui.theme.CameraDarkBackground
import com.example.ui.theme.CameraProAmber
import com.example.ui.theme.CameraProCyan
import com.example.ui.theme.CameraSurfaceCard

@Composable
fun QuickSettingsDrawer(
  flashMode: FlashMode,
  gridType: GridType,
  isForced60Fps: Boolean,
  isRawEnabled: Boolean,
  isHorizonEnabled: Boolean,
  isHistoEnabled: Boolean,
  onFlashChange: (FlashMode) -> Unit,
  onGridChange: (GridType) -> Unit,
  onToggleForce60Fps: () -> Unit,
  onToggleRaw: () -> Unit,
  onToggleHorizon: () -> Unit,
  onToggleHisto: () -> Unit,
  onDismiss: () -> Unit
) {
  Dialog(onDismissRequest = onDismiss) {
    Card(
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = CameraDarkBackground),
      border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
      modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
        .testTag("quick_settings_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Tune,
              contentDescription = null,
              tint = CameraProAmber,
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Pengaturan Cepat",
              color = Color.White,
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold
            )
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

        // 1. Flash Mode Selector
        Text(text = "LAMPU KILAT (FLASH)", color = CameraProAmber, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          FlashMode.values().forEach { mode ->
            val isSelected = mode == flashMode
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(if (isSelected) CameraProAmber else CameraSurfaceCard)
                .clickable { onFlashChange(mode) }
                .padding(vertical = 8.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = mode.iconName,
                color = if (isSelected) Color.Black else Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 2. Grid Selector
        Text(text = "GARIS BANTU (GRID)", color = CameraProAmber, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          GridType.values().forEach { grid ->
            val isSelected = grid == gridType
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(if (isSelected) CameraProCyan else CameraSurfaceCard)
                .clickable { onGridChange(grid) }
                .padding(vertical = 8.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = grid.label,
                color = if (isSelected) Color.Black else Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 3. Quick Toggles
        QuickToggleRow(
          label = "Paksa 60 FPS (Camera2)",
          description = "Kunci frame rate 60 fps",
          checked = isForced60Fps,
          onCheckedChange = { onToggleForce60Fps() }
        )

        QuickToggleRow(
          label = "Format RAW (DNG)",
          description = "Data sensor mentah tanpa kompresi",
          checked = isRawEnabled,
          onCheckedChange = { onToggleRaw() }
        )

        QuickToggleRow(
          label = "Indikator Waterpass (Horizon)",
          description = "Sensor gyro untuk foto sejajar",
          checked = isHorizonEnabled,
          onCheckedChange = { onToggleHorizon() }
        )

        QuickToggleRow(
          label = "Live Histogram RGB",
          description = "Monitor kurva pencahayaan",
          checked = isHistoEnabled,
          onCheckedChange = { onToggleHisto() }
        )
      }
    }
  }
}

@Composable
private fun QuickToggleRow(
  label: String,
  description: String,
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Column(modifier = Modifier.weight(1f)) {
      Text(text = label, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
      Text(text = description, color = Color.LightGray, fontSize = 10.sp)
    }
    Switch(
      checked = checked,
      onCheckedChange = onCheckedChange,
      colors = SwitchDefaults.colors(
        checkedThumbColor = CameraProAmber,
        checkedTrackColor = CameraProAmber.copy(alpha = 0.5f)
      )
    )
  }
}
