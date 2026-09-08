package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ProControlsState
import com.example.model.WhiteBalancePreset
import com.example.ui.theme.CameraProAmber
import com.example.ui.theme.CameraProCyan
import com.example.ui.theme.CameraSurfaceCard

private enum class ProTab(val title: String) {
  ISO("ISO"),
  SHUTTER("SHUTTER"),
  EV("EV"),
  WB("WB"),
  FOCUS("FOCUS"),
  RAW("RAW")
}

@Composable
fun ProControlsBar(
  proState: ProControlsState,
  onUpdateProState: ((ProControlsState) -> ProControlsState) -> Unit,
  modifier: Modifier = Modifier
) {
  var activeTab by remember { mutableStateOf(ProTab.ISO) }

  val isoList = remember { listOf(0, 100, 200, 400, 800, 1600, 3200) }
  val shutterList = remember {
    listOf(
      "AUTO",
      "1/4000",
      "1/2000",
      "1/1000",
      "1/500",
      "1/250",
      "1/125",
      "1/60",
      "1/30",
      "1/15",
      "1/8",
      "1/4",
      "1/2",
      "1s"
    )
  }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .background(Color.Black.copy(alpha = 0.85f))
      .padding(vertical = 8.dp)
  ) {
    // 1. Pro Parameter Tabs (ISO, SHUTTER, EV, WB, FOCUS, RAW)
    LazyRow(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      items(ProTab.values()) { tab ->
        val isSelected = tab == activeTab
        val valueLabel = when (tab) {
          ProTab.ISO -> if (proState.iso == 0) "AUTO" else "${proState.iso}"
          ProTab.SHUTTER -> proState.shutterSpeedFraction
          ProTab.EV -> if (proState.evCompensation >= 0) "+${proState.evCompensation * 0.5f}" else "${proState.evCompensation * 0.5f}"
          ProTab.WB -> proState.whiteBalance.label.take(4)
          ProTab.FOCUS -> if (proState.isManualFocus) "MF ${(proState.focusDistance * 10).toInt() / 10f}" else "AF"
          ProTab.RAW -> if (proState.isRawDngEnabled) "DNG" else "JPG"
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) CameraProAmber.copy(alpha = 0.25f) else CameraSurfaceCard)
            .border(
              1.dp,
              if (isSelected) CameraProAmber else Color.White.copy(alpha = 0.1f),
              RoundedCornerShape(8.dp)
            )
            .clickable { activeTab = tab }
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .testTag("pro_tab_${tab.name.lowercase()}")
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = tab.title,
              color = if (isSelected) CameraProAmber else Color.LightGray,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = valueLabel,
              color = Color.White,
              fontSize = 11.sp,
              fontFamily = FontFamily.Monospace,
              fontWeight = FontWeight.Medium
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // 2. Tab-specific Sub-controls / Value sliders
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp)
        .height(52.dp),
      contentAlignment = Alignment.Center
    ) {
      when (activeTab) {
        ProTab.ISO -> {
          LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            items(isoList) { iso ->
              val isSelected = iso == proState.iso
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(16.dp))
                  .background(if (isSelected) CameraProAmber else Color.White.copy(alpha = 0.12f))
                  .clickable { onUpdateProState { it.copy(iso = iso) } }
                  .padding(horizontal = 12.dp, vertical = 6.dp)
              ) {
                Text(
                  text = if (iso == 0) "AUTO" else "$iso",
                  color = if (isSelected) Color.Black else Color.White,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  fontFamily = FontFamily.Monospace
                )
              }
            }
          }
        }

        ProTab.SHUTTER -> {
          LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            items(shutterList) { shutter ->
              val isSelected = shutter == proState.shutterSpeedFraction
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(16.dp))
                  .background(if (isSelected) CameraProAmber else Color.White.copy(alpha = 0.12f))
                  .clickable { onUpdateProState { it.copy(shutterSpeedFraction = shutter) } }
                  .padding(horizontal = 10.dp, vertical = 6.dp)
              ) {
                Text(
                  text = shutter,
                  color = if (isSelected) Color.Black else Color.White,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  fontFamily = FontFamily.Monospace
                )
              }
            }
          }
        }

        ProTab.EV -> {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "-3.0 EV",
              color = Color.LightGray,
              fontSize = 10.sp,
              fontFamily = FontFamily.Monospace
            )
            Slider(
              value = proState.evCompensation.toFloat(),
              onValueChange = { newEv ->
                onUpdateProState { it.copy(evCompensation = newEv.toInt()) }
              },
              valueRange = -6f..6f,
              steps = 11,
              colors = SliderDefaults.colors(
                thumbColor = CameraProAmber,
                activeTrackColor = CameraProAmber,
                inactiveTrackColor = Color.DarkGray
              ),
              modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp)
            )
            Text(
              text = "+3.0 EV",
              color = Color.LightGray,
              fontSize = 10.sp,
              fontFamily = FontFamily.Monospace
            )
          }
        }

        ProTab.WB -> {
          LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            items(WhiteBalancePreset.values()) { wb ->
              val isSelected = wb == proState.whiteBalance
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(16.dp))
                  .background(if (isSelected) CameraProAmber else Color.White.copy(alpha = 0.12f))
                  .clickable { onUpdateProState { it.copy(whiteBalance = wb) } }
                  .padding(horizontal = 10.dp, vertical = 6.dp)
              ) {
                Text(
                  text = wb.label,
                  color = if (isSelected) Color.Black else Color.White,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }
        }

        ProTab.FOCUS -> {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            // AF / MF toggle
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(if (proState.isManualFocus) CameraProCyan else Color.White.copy(alpha = 0.15f))
                .clickable {
                  onUpdateProState { it.copy(isManualFocus = !it.isManualFocus) }
                }
                .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
              Text(
                text = if (proState.isManualFocus) "MF (Manual)" else "AF (Auto)",
                color = if (proState.isManualFocus) Color.Black else Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }

            if (proState.isManualFocus) {
              Text(text = "Macro", color = Color.LightGray, fontSize = 10.sp)
              Slider(
                value = proState.focusDistance,
                onValueChange = { dist ->
                  onUpdateProState { it.copy(focusDistance = dist) }
                },
                valueRange = 0.0f..1.0f,
                colors = SliderDefaults.colors(
                  thumbColor = CameraProCyan,
                  activeTrackColor = CameraProCyan,
                  inactiveTrackColor = Color.DarkGray
                ),
                modifier = Modifier.weight(1f)
              )
              Text(text = "Infinity", color = Color.LightGray, fontSize = 10.sp)
            }
          }
        }

        ProTab.RAW -> {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column {
              Text(
                text = if (proState.isRawDngEnabled) "Format RAW Sensor: AKTIF (DNG 14-bit)" else "Format Sensor: Standar JPEG",
                color = if (proState.isRawDngEnabled) CameraProAmber else Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "Menyimpan data mentah sensor Helio G85 tanpa kompresi",
                color = Color.LightGray,
                fontSize = 10.sp
              )
            }
            Switch(
              checked = proState.isRawDngEnabled,
              onCheckedChange = { checked ->
                onUpdateProState { it.copy(isRawDngEnabled = checked) }
              },
              colors = SwitchDefaults.colors(
                checkedThumbColor = CameraProAmber,
                checkedTrackColor = CameraProAmber.copy(alpha = 0.5f)
              )
            )
          }
        }
      }
    }
  }
}
