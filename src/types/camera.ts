export type CameraMode =
  | 'PHOTO'
  | 'VIDEO_60FPS'
  | 'PORTRAIT'
  | 'PRO'
  | 'CINEMATIC'
  | 'BOKEH'
  | 'NIGHT'
  | 'DOCUMENT';

export interface CameraModeInfo {
  id: CameraMode;
  label: string;
  badge?: string;
  description: string;
}

export type FlashMode = 'OFF' | 'ON' | 'AUTO' | 'TORCH';

export type GridType = 'NONE' | 'RULE_OF_THIRDS' | 'GOLDEN_RATIO' | 'CROSSHAIR';

export type OneHandedHand = 'RIGHT' | 'LEFT';

export type WhiteBalancePreset = 'AUTO' | 'TUNGSTEN' | 'FLUORESCENT' | 'DAYLIGHT' | 'CLOUDY' | 'SHADE';

export interface CinematicProfile {
  id: string;
  name: string;
  subText: string;
  cssFilter: string;
  colorGrade: string;
}

export interface ProControlsState {
  iso: number; // 0 = Auto, 100, 200, 400, 800, 1600, 3200
  shutterSpeedFraction: string; // "AUTO", "1/4000", ...
  evCompensation: number; // -6 to +6 (representing -3.0 to +3.0 EV)
  whiteBalance: WhiteBalancePreset;
  isManualFocus: boolean;
  focusDistance: number; // 0.0 (macro) to 1.0 (infinity)
  isRawDngEnabled: boolean;
  isHorizonLevelEnabled: boolean;
  isHistogramEnabled: boolean;
}

export interface CinematicState {
  bokehAperture: number; // f/1.4, f/1.8, f/2.8, etc.
  profileId: string;
  isCinemascope21by9: boolean;
}

export interface CapturedItem {
  id: string;
  dataUrl: string;
  timestamp: number;
  isVideo: boolean;
  mode: CameraMode;
  isRaw: boolean;
  iso: number;
  shutter: string;
  aperture: string;
  resolution: string;
}

export interface Camera2Info {
  deviceModel: string;
  hardwareLevel: string;
  activeLensFacing: 'REAR' | 'FRONT';
  supportsRawSensor: boolean;
  isForced60FpsEnabled: boolean;
  fpsRange: [number, number];
  sensorFrameDurationMs: number;
}
