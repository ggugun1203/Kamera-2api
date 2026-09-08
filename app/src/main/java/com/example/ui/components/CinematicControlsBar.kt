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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CinematicProfile
import com.example.model.CinematicState
import com.example.ui.theme.CameraProAmber
import com.example.ui.theme.CameraProCyan
import com.example.ui.theme.CameraSurfaceCard

@Composable
fun CinematicControlsBar(
  cinematicState: CinematicState,
  onUpdateCinematicState: ((CinematicState) -> CinematicState) -> Unit,
  modifier: Modifier = Modifier
) {
  val apertures = remember { listOf(1.4f, 1.8f, 2.8f, 4.0f, 5.6f, 8.0f, 16.0f) }
  val profiles = remember { CinematicProfile.values().toList() }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .background(Color.Black.copy(alpha = 0.85f))
      .padding(vertical = 8.dp)
  ) {
    // 1. Aperture (Depth of Field / Bokeh) Row
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 2.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text(
        text = "APERTUR BOKEH",
        color = CameraProCyan,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.5.sp
      )
      Text(
        text = "f/${cinematicState.bokehAperture} (Depth Blur)",
        color = Color.White,
        fontSize = 11.sp,
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.SemiBold
      )
    }

    LazyRow(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 4.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      items(apertures) { apt ->
        val isSelected = apt == cinematicState.bokehAperture
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (isSelected) CameraProCyan else CameraSurfaceCard)
            .border(
              1.dp,
              if (isSelected) CameraProCyan else Color.White.copy(alpha = 0.12f),
              RoundedCornerShape(14.dp)
            )
            .clickable { onUpdateCinematicState { it.copy(bokehAperture = apt) } }
            .padding(horizontal = 12.dp, vertical = 5.dp)
            .testTag("aperture_${apt}")
        ) {
          Text(
            text = "f/$apt",
            color = if (isSelected) Color.Black else Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(4.dp))

    // 2. Cinematic Film Color Profiles Row
    Text(
      text = "PROFIL WARNA FILM",
      color = CameraProAmber,
      fontSize = 10.sp,
      fontWeight = FontWeight.Bold,
      letterSpacing = 0.5.sp,
      modifier = Modifier.padding(horizontal = 14.dp, vertical = 2.dp)
    )

    LazyRow(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 4.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      items(profiles) { profile ->
        val isSelected = profile == cinematicState.profile
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) CameraProAmber else Color.White.copy(alpha = 0.1f))
            .clickable { onUpdateCinematicState { it.copy(profile = profile) } }
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .testTag("cinematic_profile_${profile.name.lowercase()}")
        ) {
          Column {
            Text(
              text = profile.displayName,
              color = if (isSelected) Color.Black else Color.White,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = profile.subText,
              color = if (isSelected) Color.DarkGray else Color.LightGray,
              fontSize = 9.sp
            )
          }
        }
      }
    }
  }
}
