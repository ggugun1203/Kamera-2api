import { CameraMode, CameraModeInfo, CinematicProfile, WhiteBalancePreset } from '../types/camera';

export const CAMERA_MODES: CameraModeInfo[] = [
  { id: 'PHOTO', label: 'FOTO', description: 'Foto AI HDR dengan pemrosesan 48MP Quad-Bayer' },
  { id: 'VIDEO_60FPS', label: 'VIDEO 60FPS', badge: '60 FPS', description: 'Rekam video mulus 60 FPS Camera2 API' },
  { id: 'PORTRAIT', label: 'PORTRAIT', description: 'Separasi subjek AI dengan efek studio' },
  { id: 'PRO', label: 'PRO MANUAL', badge: 'PRO', description: 'Kontrol manual penuh ISO, Rana, EV, WB, Focus & RAW' },
  { id: 'CINEMATIC', label: 'SINEMATIK', badge: '21:9', description: 'Rasio 21:9 anamorfik dengan gradasi warna film' },
  { id: 'BOKEH', label: 'BOKEH f/1.4', badge: 'BOKEH', description: 'Apertur virtual dinamis dengan simulasi depth blur' },
  { id: 'NIGHT', label: 'MALAM', description: 'Multi-frame noise reduction untuk cahaya rendah' },
  { id: 'DOCUMENT', label: 'DOKUMEN', description: 'Deteksi tepi dokumen dan koreksi perspektif' },
];

export const ISO_VALUES = [0, 100, 200, 400, 800, 1600, 3200];

export const SHUTTER_SPEEDS = [
  'AUTO',
  '1/4000',
  '1/2000',
  '1/1000',
  '1/500',
  '1/250',
  '1/125',
  '1/60',
  '1/30',
  '1/15',
  '1/8',
  '1/4',
  '1/2',
  '1s'
];

export const APERTURE_VALUES = [1.4, 1.8, 2.8, 4.0, 5.6, 8.0, 16.0];

export const WHITE_BALANCE_OPTIONS: { id: WhiteBalancePreset; label: string; tempKelvin: string }[] = [
  { id: 'AUTO', label: 'AWB', tempKelvin: 'Otomatis' },
  { id: 'TUNGSTEN', label: 'Tungsten', tempKelvin: '3000K' },
  { id: 'FLUORESCENT', label: 'Neon', tempKelvin: '4000K' },
  { id: 'DAYLIGHT', label: 'Matahari', tempKelvin: '5500K' },
  { id: 'CLOUDY', label: 'Berawan', tempKelvin: '6500K' },
  { id: 'SHADE', label: 'Teduh', tempKelvin: '7500K' },
];

export const CINEMATIC_PROFILES: CinematicProfile[] = [
  {
    id: 'blockbuster',
    name: 'Teal & Orange',
    subText: 'Hollywood Grade',
    cssFilter: 'contrast(1.15) saturate(1.25) hue-rotate(-8deg)',
    colorGrade: 'linear-gradient(135deg, rgba(0, 229, 255, 0.12), rgba(255, 110, 0, 0.12))',
  },
  {
    id: 'kodak_2383',
    name: 'Kodak 2383',
    subText: 'Vintage Warm',
    cssFilter: 'sepia(0.2) contrast(1.1) brightness(1.02) saturate(1.15)',
    colorGrade: 'linear-gradient(180deg, rgba(255, 200, 100, 0.08), rgba(180, 100, 50, 0.1))',
  },
  {
    id: 'noir',
    name: 'Classic Noir',
    subText: 'B&W Kontras Tinggi',
    cssFilter: 'grayscale(1) contrast(1.35) brightness(0.95)',
    colorGrade: 'none',
  },
  {
    id: 'emerald',
    name: 'Emerald SciFi',
    subText: 'Matrix Cool',
    cssFilter: 'contrast(1.1) hue-rotate(35deg) saturate(1.2)',
    colorGrade: 'linear-gradient(180deg, rgba(0, 255, 150, 0.08), rgba(0, 50, 100, 0.12))',
  },
  {
    id: 'bleach',
    name: 'Bleach Bypass',
    subText: 'Gritty Desat',
    cssFilter: 'contrast(1.4) saturate(0.6) brightness(1.05)',
    colorGrade: 'none',
  },
];
