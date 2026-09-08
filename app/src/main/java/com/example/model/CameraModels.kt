package com.example.model

enum class CameraMode(val title: String, val badge: String) {
  PHOTO("FOTO", "HDR"),
  VIDEO_60FPS("VIDEO 60FPS", "60 FPS"),
  CINEMATIC("SINEMATIK", "21:9 BOKEH"),
  PRO("PRO", "RAW/MAN"),
  BOKEH("PORTRAIT", "DOF f/1.8")
}

enum class OneHandedHand(val label: String) {
  RIGHT("Jempol Kanan"),
  LEFT("Jempol Kiri")
}

enum class FlashMode(val iconName: String) {
  OFF("Off"),
  AUTO("Auto"),
  ON("On"),
  TORCH("Senter")
}

enum class GridType(val label: String) {
  NONE("Mati"),
  RULE_OF_THIRDS("3x3"),
  GOLDEN_RATIO("Golden"),
  SQUARE("Kotak 1:1")
}

enum class CinematicProfile(val displayName: String, val subText: String) {
  NATURAL("Natural Cine", "Warna akurat netral"),
  TEAL_ORANGE("Teal & Orange", "Gaya blockbuster bioskop"),
  KODAK_WARM("Kodak 2383", "Nuansa hangat vintage"),
  NOIR_MONO("Noir Monokrom", "Hitam putih sinematik"),
  MOODY_EMERALD("Emerald SciFi", "Karakter hijau redup"),
  BLEACH_BYPASS("Bleach Bypass", "Kontras tinggi gritty")
}

enum class WhiteBalancePreset(val label: String, val kelvin: Int) {
  AUTO("AWB", 0),
  INCANDESCENT("3000K Lampu", 3000),
  FLUORESCENT("4000K Neon", 4000),
  DAYLIGHT("5500K Siang", 5500),
  CLOUDY("6500K Berawan", 6500),
  SHADE("7500K Teduh", 7500)
}

data class Camera2Info(
  val hardwareLevel: String = "LIMITED (Redmi Note 9 Helio G85)",
  val isCamera2Activated: Boolean = true,
  val isForced60FpsSupported: Boolean = true,
  val isForced60FpsEnabled: Boolean = true,
  val currentMeasuredFps: Float = 60.0f,
  val availableFpsRanges: List<Pair<Int, Int>> = listOf(15 to 30, 30 to 30, 60 to 60),
  val supportsRawSensor: Boolean = true,
  val maxDigitalZoom: Float = 10.0f,
  val deviceModel: String = "Redmi Note 9 (merlin)",
  val activeLensFacing: String = "Belakang (Utama 48MP)"
)

data class ProControlsState(
  val iso: Int = 0, // 0 = Auto, 100, 200, 400, 800, 1600, 3200
  val shutterSpeedFraction: String = "AUTO", // AUTO, 1/4000, 1/2000, 1/1000, 1/500, 1/250, 1/125, 1/60, 1/30, 1/15, 1/8, 1/4, 1/2, 1s
  val evCompensation: Int = 0, // -6 to +6 (each step = 0.5 EV => -3.0 to +3.0)
  val whiteBalance: WhiteBalancePreset = WhiteBalancePreset.AUTO,
  val manualKelvin: Int = 5500,
  val isManualFocus: Boolean = false,
  val focusDistance: Float = 0.5f, // 0.0f = infinity, 1.0f = macro
  val isRawDngEnabled: Boolean = false,
  val isHistogramEnabled: Boolean = true,
  val isFocusPeakingEnabled: Boolean = false,
  val isHorizonLevelEnabled: Boolean = true
)

data class CinematicState(
  val aspectRatio: String = "21:9", // 21:9, 16:9, 4:3
  val bokehAperture: Float = 1.8f, // f/1.4, f/1.8, f/2.8, f/4.0, f/5.6, f/8.0, f/16
  val bokehRadius: Float = 28f,
  val profile: CinematicProfile = CinematicProfile.TEAL_ORANGE,
  val is60FpsCinema: Boolean = true,
  val focusPointX: Float = 0.5f,
  val focusPointY: Float = 0.5f
)

data class CaptureUiState(
  val isCapturing: Boolean = false,
  val isRecordingVideo: Boolean = false,
  val recordingSeconds: Int = 0,
  val lastCapturedUri: String? = null,
  val lastCapturedIsVideo: Boolean = false,
  val lastCapturedIsRaw: Boolean = false,
  val feedbackToast: String? = null
)
