import React, { useState, useRef, useEffect } from 'react';
import {
  CameraMode,
  FlashMode,
  GridType,
  OneHandedHand,
  ProControlsState,
  CinematicState,
  CapturedItem,
  Camera2Info,
} from './types/camera';
import { CameraViewfinder } from './components/CameraViewfinder';
import { TopStatusBar } from './components/TopStatusBar';
import { ThumbZoomSelector } from './components/ThumbZoomSelector';
import { ModeDialCarousel } from './components/ModeDialCarousel';
import { ProControlsBar } from './components/ProControlsBar';
import { CinematicControlsBar } from './components/CinematicControlsBar';
import { ErgonomicBottomBar } from './components/ErgonomicBottomBar';
import { RedmiCamera2DiagnosticModal } from './components/RedmiCamera2DiagnosticModal';
import { QuickSettingsDrawer } from './components/QuickSettingsDrawer';
import { GalleryModal } from './components/GalleryModal';
import { cameraAudio } from './utils/audio';

export const App: React.FC = () => {
  // 1. Camera States
  const [mode, setMode] = useState<CameraMode>('PHOTO');
  const [oneHandedHand, setOneHandedHand] = useState<OneHandedHand>('RIGHT');
  const [zoomRatio, setZoomRatio] = useState<number>(1.0);
  const [lensFacing, setLensFacing] = useState<'REAR' | 'FRONT'>('REAR');
  const [flashMode, setFlashMode] = useState<FlashMode>('OFF');
  const [gridType, setGridType] = useState<GridType>('RULE_OF_THIRDS');
  const [fps, setFps] = useState<number>(60.0);

  // 2. Pro & Cinematic states
  const [proState, setProState] = useState<ProControlsState>({
    iso: 0,
    shutterSpeedFraction: 'AUTO',
    evCompensation: 0,
    whiteBalance: 'AUTO',
    isManualFocus: false,
    focusDistance: 0.5,
    isRawDngEnabled: false,
    isHorizonLevelEnabled: true,
    isHistogramEnabled: true,
  });

  const [cinematicState, setCinematicState] = useState<CinematicState>({
    bokehAperture: 1.8,
    profileId: 'blockbuster',
    isCinemascope21by9: true,
  });

  // 3. Camera2 Specs for Redmi Note 9
  const [camera2Info, setCamera2Info] = useState<Camera2Info>({
    deviceModel: 'Xiaomi Redmi Note 9 (merlin)',
    hardwareLevel: 'LEVEL_3 / FULL',
    activeLensFacing: 'REAR',
    supportsRawSensor: true,
    isForced60FpsEnabled: true,
    fpsRange: [60, 60],
    sensorFrameDurationMs: 16.6,
  });

  // 4. Capture & Video Recording States
  const [capturedItems, setCapturedItems] = useState<CapturedItem[]>([]);
  const [isRecording, setIsRecording] = useState<boolean>(false);
  const [recordingSeconds, setRecordingSeconds] = useState<number>(0);
  const [isShutterFlash, setIsShutterFlash] = useState<boolean>(false);

  // 5. Modal Sheets
  const [showDiagnostics, setShowDiagnostics] = useState<boolean>(false);
  const [showQuickSettings, setShowQuickSettings] = useState<boolean>(false);
  const [showGallery, setShowGallery] = useState<boolean>(false);

  // Video Ref & MediaRecorder
  const videoRef = useRef<HTMLVideoElement>(null);
  const mediaRecorderRef = useRef<MediaRecorder | null>(null);
  const recordedChunksRef = useRef<Blob[]>([]);
  const timerIntervalRef = useRef<number | null>(null);

  // Flash toggle cycle
  const handleFlashCycle = () => {
    cameraAudio.playDialTick();
    const modes: FlashMode[] = ['OFF', 'AUTO', 'ON', 'TORCH'];
    const nextIdx = (modes.indexOf(flashMode) + 1) % modes.length;
    setFlashMode(modes[nextIdx]);
  };

  // Flip Front / Rear lens
  const handleFlipLens = () => {
    setLensFacing((prev) => (prev === 'REAR' ? 'FRONT' : 'REAR'));
    setCamera2Info((prev) => ({
      ...prev,
      activeLensFacing: prev.activeLensFacing === 'REAR' ? 'FRONT' : 'REAR',
    }));
  };

  // Capture Photo
  const handleTakePhoto = () => {
    cameraAudio.playShutterSound();
    setIsShutterFlash(true);
    setTimeout(() => setIsShutterFlash(false), 120);

    let dataUrl = '';
    const video = videoRef.current;

    if (video && video.videoWidth > 0) {
      const canvas = document.createElement('canvas');
      canvas.width = video.videoWidth;
      canvas.height = video.videoHeight;
      const ctx = canvas.getContext('2d');
      if (ctx) {
        if (lensFacing === 'FRONT') {
          ctx.translate(canvas.width, 0);
          ctx.scale(-1, 1);
        }
        ctx.drawImage(video, 0, 0, canvas.width, canvas.height);
        dataUrl = canvas.toDataURL('image/jpeg', 0.95);
      }
    } else {
      // Generate photogenic realistic snapshot
      const canvas = document.createElement('canvas');
      canvas.width = 1920;
      canvas.height = 1080;
      const ctx = canvas.getContext('2d');
      if (ctx) {
        const grad = ctx.createLinearGradient(0, 0, 1920, 1080);
        grad.addColorStop(0, '#101726');
        grad.addColorStop(0.5, '#1e293b');
        grad.addColorStop(1, '#0f172a');
        ctx.fillStyle = grad;
        ctx.fillRect(0, 0, 1920, 1080);

        // Watermark
        ctx.fillStyle = '#FFB300';
        ctx.font = 'bold 36px monospace';
        ctx.fillText('REDMI NOTE 9 48MP AI CAMERA', 80, 1000);
        ctx.fillStyle = '#FFFFFF';
        ctx.font = '24px sans-serif';
        ctx.fillText(
          `ISO ${proState.iso || 100} • ${proState.shutterSpeedFraction} • 60 FPS • ${new Date().toLocaleTimeString()}`,
          80,
          1040
        );
        dataUrl = canvas.toDataURL('image/jpeg', 0.95);
      }
    }

    const newItem: CapturedItem = {
      id: Math.random().toString(36).substring(2, 9),
      dataUrl,
      timestamp: Date.now(),
      isVideo: false,
      mode,
      isRaw: proState.isRawDngEnabled,
      iso: proState.iso,
      shutter: proState.shutterSpeedFraction,
      aperture: `f/${cinematicState.bokehAperture}`,
      resolution: proState.isRawDngEnabled ? '8000x6000 (48MP RAW DNG)' : '4000x3000 (12MP JPG)',
    };

    setCapturedItems((prev) => [newItem, ...prev]);
  };

  // Video 60 FPS Recording Toggle
  const handleToggleVideoRecording = () => {
    if (isRecording) {
      // Stop Recording
      cameraAudio.playDialTick();
      if (mediaRecorderRef.current && mediaRecorderRef.current.state !== 'inactive') {
        mediaRecorderRef.current.stop();
      }
      setIsRecording(false);
      if (timerIntervalRef.current) clearInterval(timerIntervalRef.current);
    } else {
      // Start Recording
      cameraAudio.playDialTick();
      const video = videoRef.current;
      recordedChunksRef.current = [];

      try {
        let stream: MediaStream | null = null;
        if (video && video.srcObject) {
          stream = video.srcObject as MediaStream;
        }

        if (stream) {
          const recorder = new MediaRecorder(stream, {
            mimeType: MediaRecorder.isTypeSupported('video/webm;codecs=vp9')
              ? 'video/webm;codecs=vp9'
              : 'video/webm',
          });

          recorder.ondataavailable = (e) => {
            if (e.data.size > 0) recordedChunksRef.current.push(e.data);
          };

          recorder.onstop = () => {
            const blob = new Blob(recordedChunksRef.current, { type: 'video/webm' });
            const videoUrl = URL.createObjectURL(blob);
            const newItem: CapturedItem = {
              id: Math.random().toString(36).substring(2, 9),
              dataUrl: videoUrl,
              timestamp: Date.now(),
              isVideo: true,
              mode: 'VIDEO_60FPS',
              isRaw: false,
              iso: proState.iso,
              shutter: '1/60s',
              aperture: 'f/1.8',
              resolution: '1920x1080 @ 60.0 FPS FHD',
            };
            setCapturedItems((prev) => [newItem, ...prev]);
          };

          recorder.start(100);
          mediaRecorderRef.current = recorder;
        }
      } catch (e) {
        console.warn('MediaRecorder error, recording simulation active:', e);
      }

      setIsRecording(true);
      setRecordingSeconds(0);
      timerIntervalRef.current = window.setInterval(() => {
        setRecordingSeconds((s) => s + 1);
      }, 1000);
    }
  };

  // Shutter action distributor
  const handleShutterTrigger = () => {
    if (mode === 'VIDEO_60FPS') {
      handleToggleVideoRecording();
    } else {
      handleTakePhoto();
    }
  };

  return (
    <div className="w-screen h-screen bg-[#070709] flex items-center justify-center overflow-hidden">
      {/* Phone container frame (Full bleed on mobile, framed on desktop) */}
      <div className="relative w-full h-full sm:max-w-[430px] sm:max-h-[920px] sm:rounded-[44px] bg-black sm:border-[8px] sm:border-[#22222B] shadow-2xl overflow-hidden flex flex-col">
        {/* Shutter White Flash Animation Overlay */}
        <div
          className={`absolute inset-0 bg-white pointer-events-none z-50 transition-opacity duration-100 ${
            isShutterFlash ? 'opacity-90' : 'opacity-0'
          }`}
        />

        {/* 1. Camera Viewfinder (Background Live Preview) */}
        <div className="relative flex-1 w-full h-full overflow-hidden">
          <CameraViewfinder
            mode={mode}
            proState={proState}
            cinematicProfileId={cinematicState.profileId}
            bokehAperture={cinematicState.bokehAperture}
            gridType={gridType}
            zoomRatio={zoomRatio}
            lensFacing={lensFacing}
            isForced60Fps={camera2Info.isForced60FpsEnabled}
            onFpsUpdate={setFps}
            videoRef={videoRef}
            isRecording={isRecording}
          />

          {/* Top Status Bar with Redmi 60 FPS status */}
          <TopStatusBar
            fps={fps}
            camera2Info={camera2Info}
            flashMode={flashMode}
            isRawEnabled={proState.isRawDngEnabled}
            onFlashToggle={handleFlashCycle}
            onOpenQuickSettings={() => setShowQuickSettings(true)}
            onOpenDiagnostics={() => setShowDiagnostics(true)}
          />

          {/* Lower Thumb Zone Overlay */}
          <div className="absolute inset-x-0 bottom-0 z-30 flex flex-col">
            {/* Quick 0.6x, 1x, 2x, 5x Thumb Zoom Selector */}
            <ThumbZoomSelector
              zoomRatio={zoomRatio}
              oneHandedHand={oneHandedHand}
              onZoomChange={setZoomRatio}
            />

            {/* Pro Controls Bar (when in PRO mode) */}
            {mode === 'PRO' && (
              <ProControlsBar proState={proState} onChange={setProState} />
            )}

            {/* Cinematic Controls Bar (when in CINEMATIC or BOKEH mode) */}
            {(mode === 'CINEMATIC' || mode === 'BOKEH') && (
              <CinematicControlsBar cinematicState={cinematicState} onChange={setCinematicState} />
            )}

            {/* Mode Dial Carousel */}
            <ModeDialCarousel currentMode={mode} onSelectMode={setMode} />

            {/* Ergonomic Bottom Bar (Shutter, Thumb Hand Switcher, Flip, Gallery) */}
            <ErgonomicBottomBar
              mode={mode}
              oneHandedHand={oneHandedHand}
              isRecording={isRecording}
              recordingSeconds={recordingSeconds}
              lastItem={capturedItems[0] || null}
              onShutterClick={handleShutterTrigger}
              onFlipCamera={handleFlipLens}
              onToggleOneHand={() =>
                setOneHandedHand((prev) => (prev === 'RIGHT' ? 'LEFT' : 'RIGHT'))
              }
              onOpenQuickSettings={() => setShowQuickSettings(true)}
              onOpenGallery={() => setShowGallery(true)}
            />
          </div>
        </div>
      </div>

      {/* Diagnostic Modal */}
      {showDiagnostics && (
        <RedmiCamera2DiagnosticModal
          camera2Info={camera2Info}
          fps={fps}
          onToggleForce60Fps={() =>
            setCamera2Info((prev) => ({
              ...prev,
              isForced60FpsEnabled: !prev.isForced60FpsEnabled,
            }))
          }
          onClose={() => setShowDiagnostics(false)}
        />
      )}

      {/* Quick Settings Drawer */}
      {showQuickSettings && (
        <QuickSettingsDrawer
          flashMode={flashMode}
          gridType={gridType}
          isHorizonEnabled={proState.isHorizonLevelEnabled}
          isHistogramEnabled={proState.isHistogramEnabled}
          isForced60Fps={camera2Info.isForced60FpsEnabled}
          isRawEnabled={proState.isRawDngEnabled}
          onFlashChange={setFlashMode}
          onGridChange={setGridType}
          onToggleHorizon={() =>
            setProState((prev) => ({ ...prev, isHorizonLevelEnabled: !prev.isHorizonLevelEnabled }))
          }
          onToggleHistogram={() =>
            setProState((prev) => ({ ...prev, isHistogramEnabled: !prev.isHistogramEnabled }))
          }
          onToggleForce60Fps={() =>
            setCamera2Info((prev) => ({
              ...prev,
              isForced60FpsEnabled: !prev.isForced60FpsEnabled,
            }))
          }
          onToggleRaw={() =>
            setProState((prev) => ({ ...prev, isRawDngEnabled: !prev.isRawDngEnabled }))
          }
          onClose={() => setShowQuickSettings(false)}
        />
      )}

      {/* Gallery Modal */}
      {showGallery && (
        <GalleryModal
          items={capturedItems}
          onDeleteItem={(id) => setCapturedItems((prev) => prev.filter((i) => i.id !== id))}
          onClose={() => setShowGallery(false)}
        />
      )}
    </div>
  );
};

export default App;
