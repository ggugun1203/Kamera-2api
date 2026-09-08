import React, { useRef, useEffect, useState, useCallback } from 'react';
import { CameraMode, CinematicProfile, GridType, ProControlsState } from '../types/camera';
import { CINEMATIC_PROFILES } from '../constants/cameraData';
import { cameraAudio } from '../utils/audio';

interface Props {
  mode: CameraMode;
  proState: ProControlsState;
  cinematicProfileId: string;
  bokehAperture: number;
  gridType: GridType;
  zoomRatio: number;
  lensFacing: 'REAR' | 'FRONT';
  isForced60Fps: boolean;
  onFpsUpdate: (fps: number) => void;
  videoRef: React.RefObject<HTMLVideoElement>;
  isRecording: boolean;
}

export const CameraViewfinder: React.FC<Props> = ({
  mode,
  proState,
  cinematicProfileId,
  bokehAperture,
  gridType,
  zoomRatio,
  lensFacing,
  isForced60Fps,
  onFpsUpdate,
  videoRef,
  isRecording,
}) => {
  const [focusPoint, setFocusPoint] = useState<{ x: number; y: number } | null>(null);
  const [rollDegrees, setRollDegrees] = useState<number>(0);
  const [cameraAccessError, setCameraAccessError] = useState<string | null>(null);
  const [isCameraActive, setIsCameraActive] = useState<boolean>(false);
  const containerRef = useRef<HTMLDivElement>(null);
  const canvasHistoRef = useRef<HTMLCanvasElement>(null);

  // Device orientation / gyro waterpass
  useEffect(() => {
    const handleOrientation = (e: DeviceOrientationEvent) => {
      if (e.gamma !== null) {
        setRollDegrees(e.gamma);
      }
    };
    window.addEventListener('deviceorientation', handleOrientation);
    return () => window.removeEventListener('deviceorientation', handleOrientation);
  }, []);

  // Web camera initialization with 60 FPS constraints
  useEffect(() => {
    let stream: MediaStream | null = null;
    let isCancelled = false;

    const startCamera = async () => {
      try {
        setCameraAccessError(null);
        const targetFacingMode = lensFacing === 'FRONT' ? 'user' : 'environment';

        // Camera2 60 FPS forced constraints: request frameRate 60
        const constraints: MediaStreamConstraints = {
          video: {
            facingMode: targetFacingMode,
            width: { ideal: 1920 },
            height: { ideal: 1080 },
            frameRate: isForced60Fps ? { ideal: 60, min: 30 } : { ideal: 30 },
          },
          audio: mode === 'VIDEO_60FPS',
        };

        const mediaStream = await navigator.mediaDevices.getUserMedia(constraints);
        if (isCancelled) {
          mediaStream.getTracks().forEach((track) => track.stop());
          return;
        }

        stream = mediaStream;
        if (videoRef.current) {
          videoRef.current.srcObject = mediaStream;
          videoRef.current.play().catch(() => {});
          setIsCameraActive(true);
        }
      } catch (err: unknown) {
        console.warn('Camera device not available or permission denied:', err);
        setCameraAccessError('Simulasi Sensor Aktif (Helio G85 ISP)');
        setIsCameraActive(false);
      }
    };

    startCamera();

    return () => {
      isCancelled = true;
      if (stream) {
        stream.getTracks().forEach((track) => track.stop());
      }
    };
  }, [lensFacing, isForced60Fps, mode, videoRef]);

  // Real-time FPS computation loop
  useEffect(() => {
    let animationFrameId: number;
    let lastTime = performance.now();
    let frameCount = 0;
    let targetBaselineFps = isForced60Fps ? 60.0 : 30.0;

    const fpsLoop = () => {
      const now = performance.now();
      frameCount++;
      const elapsed = now - lastTime;

      if (elapsed >= 500) {
        const measuredFps = (frameCount / elapsed) * 1000;
        // Jitter simulation around realistic target if virtual or real
        const jitter = (Math.random() - 0.5) * 1.8;
        const currentFps = isCameraActive
          ? Math.min(60, Math.max(24, measuredFps))
          : Math.min(60, Math.max(29.8, targetBaselineFps + jitter));

        onFpsUpdate(Math.round(currentFps * 10) / 10);
        frameCount = 0;
        lastTime = now;
      }
      animationFrameId = requestAnimationFrame(fpsLoop);
    };

    animationFrameId = requestAnimationFrame(fpsLoop);
    return () => cancelAnimationFrame(animationFrameId);
  }, [isForced60Fps, isCameraActive, onFpsUpdate]);

  // Handle Tap-to-Focus
  const handleTapToFocus = (e: React.MouseEvent<HTMLDivElement>) => {
    if (!containerRef.current) return;
    const rect = containerRef.current.getBoundingClientRect();
    const x = e.clientX - rect.left;
    const y = e.clientY - rect.top;

    setFocusPoint({ x, y });
    cameraAudio.playFocusBeep();

    setTimeout(() => {
      setFocusPoint(null);
    }, 2200);
  };

  // Determine active CSS filter for Pro & Cinematic profiles
  const activeProfile = CINEMATIC_PROFILES.find((p) => p.id === cinematicProfileId);
  let computedFilter = activeProfile ? activeProfile.cssFilter : 'none';

  // Apply Pro manual WB / ISO adjustments to preview
  if (mode === 'PRO') {
    let wbFilter = '';
    switch (proState.whiteBalance) {
      case 'TUNGSTEN':
        wbFilter = 'hue-rotate(-20deg) saturate(1.1)';
        break;
      case 'FLUORESCENT':
        wbFilter = 'hue-rotate(-10deg) brightness(1.05)';
        break;
      case 'DAYLIGHT':
        wbFilter = 'brightness(1.02)';
        break;
      case 'CLOUDY':
        wbFilter = 'sepia(0.15) saturate(1.1)';
        break;
      case 'SHADE':
        wbFilter = 'sepia(0.25) saturate(1.15)';
        break;
      default:
        wbFilter = '';
    }

    const evFactor = 1 + proState.evCompensation * 0.1;
    const isoFactor = proState.iso > 0 ? 1 + (proState.iso - 100) / 3000 * 0.3 : 1;
    computedFilter = `${wbFilter} brightness(${evFactor * isoFactor})`.trim();
  }

  // Bokeh blur calculation
  const bokehBlurPx = mode === 'BOKEH' || mode === 'CINEMATIC' ? Math.max(0, (2.8 - bokehAperture) * 3.5) : 0;

  return (
    <div
      ref={containerRef}
      onClick={handleTapToFocus}
      className="relative w-full h-full bg-black overflow-hidden flex items-center justify-center cursor-crosshair"
    >
      {/* 1. Camera Video Stream */}
      <video
        ref={videoRef}
        autoPlay
        playsInline
        muted
        style={{
          transform: `scale(${zoomRatio}) ${lensFacing === 'FRONT' ? 'scaleX(-1)' : ''}`,
          filter: computedFilter,
          transition: 'transform 0.15s ease-out',
        }}
        className={`w-full h-full object-cover ${isCameraActive ? 'opacity-100' : 'opacity-0'}`}
      />

      {/* 2. Realistic Simulated ISP Viewfinder when physical hardware webcam is not streamed */}
      {!isCameraActive && (
        <div
          style={{
            transform: `scale(${zoomRatio})`,
            filter: computedFilter,
            transition: 'transform 0.15s ease-out',
          }}
          className="absolute inset-0 flex items-center justify-center bg-gradient-to-b from-[#141824] via-[#1c2236] to-[#0d101a]"
        >
          {/* Simulated architectural photogenic subject */}
          <div className="relative w-full h-full flex flex-col items-center justify-center p-6 text-center">
            {/* Horizon sunset gradient simulation */}
            <div className="absolute inset-0 bg-gradient-to-t from-amber-500/10 via-transparent to-cyan-500/10 pointer-events-none" />

            {/* Depth grid perspective planes */}
            <div className="relative z-0 w-64 h-64 border border-white/10 rounded-3xl flex items-center justify-center backdrop-blur-xs shadow-2xl">
              <div className="w-48 h-48 border border-amber-400/20 rounded-2xl flex items-center justify-center animate-pulse">
                <div className="w-32 h-32 border border-cyan-400/30 rounded-xl flex items-center justify-center">
                  <div className="w-16 h-16 rounded-full bg-gradient-to-br from-amber-400/30 to-cyan-400/20 flex items-center justify-center">
                    <span className="text-[10px] font-mono tracking-widest text-amber-300/80">48MP</span>
                  </div>
                </div>
              </div>
            </div>

            {/* Simulated live sensor info badge */}
            <div className="mt-8 z-10 px-4 py-1.5 rounded-full bg-black/60 border border-white/15 backdrop-blur-md">
              <p className="text-[11px] font-mono text-gray-300">
                MediaTek Helio G85 ISP • {lensFacing === 'REAR' ? 'Samsung GM1 48MP' : 'OmniVision 13MP'}
              </p>
            </div>
            {cameraAccessError && (
              <p className="mt-2 text-[10px] text-amber-400/80 tracking-wide font-mono">
                {cameraAccessError}
              </p>
            )}
          </div>
        </div>
      )}

      {/* 3. Bokeh Depth Blur Layer (when active) */}
      {bokehBlurPx > 0 && (
        <div
          style={{
            backdropFilter: `blur(${bokehBlurPx}px)`,
            maskImage: 'radial-gradient(circle at center, transparent 35%, black 85%)',
            WebkitMaskImage: 'radial-gradient(circle at center, transparent 35%, black 85%)',
          }}
          className="absolute inset-0 pointer-events-none transition-all duration-300"
        />
      )}

      {/* 4. Color Grading Gradient Layer */}
      {activeProfile && activeProfile.colorGrade !== 'none' && (
        <div
          style={{ background: activeProfile.colorGrade }}
          className="absolute inset-0 pointer-events-none mix-blend-overlay"
        />
      )}

      {/* 5. 21:9 Cinematic Cinemascope Aspect Ratio Letterbox Bars */}
      {(mode === 'CINEMATIC' || mode === 'BOKEH') && (
        <>
          <div className="absolute top-0 left-0 right-0 h-[11%] bg-black z-20 pointer-events-none transition-all duration-300 flex items-end justify-between px-6 pb-2">
            <span className="text-[9px] font-mono text-amber-400/70 tracking-widest">CINEMASCOPE 2.39:1</span>
            <span className="text-[9px] font-mono text-white/50">ANAMORPHIC PROFILE</span>
          </div>
          <div className="absolute bottom-0 left-0 right-0 h-[11%] bg-black z-20 pointer-events-none transition-all duration-300 flex items-start justify-between px-6 pt-2">
            <span className="text-[9px] font-mono text-white/40">LUT: {activeProfile?.name || 'Standard'}</span>
            <span className="text-[9px] font-mono text-cyan-400/80">f/{bokehAperture} BOKEH</span>
          </div>
        </>
      )}

      {/* 6. Grid Overlays (Rule of Thirds / Golden Ratio / Crosshair) */}
      {gridType === 'RULE_OF_THIRDS' && (
        <div className="absolute inset-0 grid grid-cols-3 grid-rows-3 pointer-events-none z-10">
          <div className="border-r border-b border-white/25" />
          <div className="border-r border-b border-white/25" />
          <div className="border-b border-white/25" />
          <div className="border-r border-b border-white/25" />
          <div className="border-r border-b border-white/25" />
          <div className="border-b border-white/25" />
          <div className="border-r border-white/25" />
          <div className="border-r border-white/25" />
          <div />
        </div>
      )}

      {gridType === 'GOLDEN_RATIO' && (
        <div className="absolute inset-0 pointer-events-none z-10 flex flex-col justify-between">
          <div className="h-[38.2%] border-b border-amber-400/30 w-full" />
          <div className="h-[38.2%] border-t border-amber-400/30 w-full" />
          <div className="absolute inset-0 flex justify-between">
            <div className="w-[38.2%] border-r border-amber-400/30 h-full" />
            <div className="w-[38.2%] border-l border-amber-400/30 h-full" />
          </div>
        </div>
      )}

      {gridType === 'CROSSHAIR' && (
        <div className="absolute inset-0 pointer-events-none z-10 flex items-center justify-center">
          <div className="w-12 h-12 border border-white/35 rounded-full flex items-center justify-center">
            <div className="w-2 h-2 bg-amber-400 rounded-full" />
          </div>
          <div className="absolute w-24 h-[1px] bg-white/25" />
          <div className="absolute h-24 w-[1px] bg-white/25" />
        </div>
      )}

      {/* 7. Horizon Waterpass Gyro Level */}
      {proState.isHorizonLevelEnabled && (
        <div className="absolute inset-x-0 top-1/2 -translate-y-1/2 flex items-center justify-center pointer-events-none z-15">
          <div
            style={{ transform: `rotate(${-rollDegrees}deg)` }}
            className={`w-52 h-[2px] transition-transform duration-75 flex items-center justify-between ${
              Math.abs(rollDegrees) <= 1.5 ? 'bg-emerald-400 shadow-[0_0_12px_#00E676]' : 'bg-white/40'
            }`}
          >
            <div className="w-3 h-3 -ml-1 rounded-full border-2 border-inherit" />
            <div className="px-2 py-0.5 rounded bg-black/60 text-[10px] font-mono font-bold tracking-wider text-inherit">
              {Math.abs(rollDegrees) <= 1.5 ? '0.0° LEVEL' : `${rollDegrees.toFixed(1)}°`}
            </div>
            <div className="w-3 h-3 -mr-1 rounded-full border-2 border-inherit" />
          </div>
        </div>
      )}

      {/* 8. Interactive Tap-to-Focus Ring */}
      {focusPoint && (
        <div
          style={{ left: `${focusPoint.x}px`, top: `${focusPoint.y}px` }}
          className="absolute -translate-x-1/2 -translate-y-1/2 pointer-events-none z-30 animate-scale-in"
        >
          <div className="w-20 h-20 border-2 border-amber-400 rounded-full flex items-center justify-center shadow-[0_0_15px_rgba(255,179,0,0.6)]">
            <div className="w-2 h-2 bg-amber-400 rounded-full" />
            <div className="absolute -top-3 text-[10px] font-mono font-bold text-amber-400 bg-black/60 px-1.5 rounded">
              AF LOCK
            </div>
          </div>
        </div>
      )}

      {/* 9. Live RGB Exposure Histogram (Floating bottom-right preview) */}
      {proState.isHistogramEnabled && (
        <div className="absolute bottom-24 right-4 z-25 bg-black/70 border border-white/20 p-2 rounded-xl backdrop-blur-md shadow-lg pointer-events-none">
          <p className="text-[9px] font-mono text-gray-400 mb-1">HISTOGRAM RGB</p>
          <div className="w-24 h-10 flex items-end gap-[1px]">
            {Array.from({ length: 18 }).map((_, i) => {
              const heightR = 15 + Math.sin(i * 0.4) * 12 + (i > 10 ? 8 : 0);
              const heightG = 20 + Math.sin(i * 0.35 + 0.5) * 15;
              const heightB = 10 + Math.cos(i * 0.5) * 14;
              return (
                <div key={i} className="flex-1 flex flex-col justify-end gap-[1px]">
                  <div style={{ height: `${heightR}%` }} className="bg-red-500/70 w-full" />
                  <div style={{ height: `${heightG}%` }} className="bg-green-500/70 w-full" />
                  <div style={{ height: `${heightB}%` }} className="bg-blue-500/70 w-full" />
                </div>
              );
            })}
          </div>
        </div>
      )}
    </div>
  );
};
