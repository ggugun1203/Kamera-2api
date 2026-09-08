import React from 'react';
import { CameraMode, CapturedItem, OneHandedHand } from '../types/camera';
import { RefreshCw, SlidersHorizontal, Image as ImageIcon, Video, StopCircle } from 'lucide-react';
import { cameraAudio } from '../utils/audio';

interface Props {
  mode: CameraMode;
  oneHandedHand: OneHandedHand;
  isRecording: boolean;
  recordingSeconds: number;
  lastItem: CapturedItem | null;
  onShutterClick: () => void;
  onFlipCamera: () => void;
  onToggleOneHand: () => void;
  onOpenQuickSettings: () => void;
  onOpenGallery: () => void;
}

export const ErgonomicBottomBar: React.FC<Props> = ({
  mode,
  oneHandedHand,
  isRecording,
  recordingSeconds,
  lastItem,
  onShutterClick,
  onFlipCamera,
  onToggleOneHand,
  onOpenQuickSettings,
  onOpenGallery,
}) => {
  const isRightHanded = oneHandedHand === 'RIGHT';
  const isVideoMode = mode === 'VIDEO_60FPS';

  const formatTime = (secs: number) => {
    const mins = Math.floor(secs / 60);
    const remainder = secs % 60;
    return `${mins.toString().padStart(2, '0')}:${remainder.toString().padStart(2, '0')}`;
  };

  // Primary thumb cluster: Flip, Shutter, Gallery
  const primaryThumbControls = (
    <div className="flex items-center gap-4">
      {/* Flip Camera */}
      <button
        onClick={() => {
          cameraAudio.playDialTick();
          onFlipCamera();
        }}
        className="w-12 h-12 rounded-full bg-white/10 hover:bg-white/20 active:scale-90 flex items-center justify-center transition-all border border-white/20 text-white"
        title="Balik Kamera Depan / Belakang"
      >
        <RefreshCw className="w-5 h-5 text-white" />
      </button>

      {/* Ergonomic Shutter Button */}
      <div className="flex flex-col items-center">
        <button
          onClick={onShutterClick}
          className={`relative w-20 h-20 rounded-full flex items-center justify-center transition-all active:scale-90 border-4 ${
            isRecording
              ? 'border-red-500 shadow-[0_0_20px_rgba(239,68,68,0.6)]'
              : 'border-white hover:border-amber-400'
          }`}
        >
          {isVideoMode || isRecording ? (
            <div
              className={`transition-all ${
                isRecording
                  ? 'w-7 h-7 rounded-lg bg-red-600 animate-pulse'
                  : 'w-14 h-14 rounded-full bg-red-500 hover:bg-red-400'
              }`}
            />
          ) : (
            <div className="w-14 h-14 rounded-full bg-white hover:bg-gray-100 shadow-inner" />
          )}
        </button>

        {isRecording && (
          <div className="mt-1 flex items-center gap-1.5 px-2 py-0.5 rounded-full bg-red-950/80 border border-red-500/50">
            <div className="w-2 h-2 rounded-full bg-red-500 animate-ping" />
            <span className="text-[10px] font-mono font-bold text-white tracking-wider">
              {formatTime(recordingSeconds)}
            </span>
          </div>
        )}
      </div>

      {/* Gallery Thumbnail */}
      <button
        onClick={onOpenGallery}
        className="w-12 h-12 rounded-2xl overflow-hidden bg-white/10 hover:bg-white/20 active:scale-90 border-2 border-white/30 flex items-center justify-center transition-all shadow-md"
        title="Buka Galeri Foto"
      >
        {lastItem ? (
          lastItem.isVideo ? (
            <div className="relative w-full h-full flex items-center justify-center bg-gray-900">
              <Video className="w-5 h-5 text-amber-400" />
            </div>
          ) : (
            <img src={lastItem.dataUrl} alt="Last capture" className="w-full h-full object-cover" />
          )
        ) : (
          <ImageIcon className="w-5 h-5 text-gray-400" />
        )}
      </button>
    </div>
  );

  // Secondary cluster: Hand Switcher & Settings
  const secondaryControls = (
    <div className="flex items-center gap-2.5">
      {/* Hand Switcher Button */}
      <button
        onClick={onToggleOneHand}
        className="flex items-center gap-1 px-3 py-1.5 rounded-full bg-amber-400/20 border border-amber-400 text-amber-400 text-[11px] font-bold active:scale-90 hover:bg-amber-400/30 transition-all"
        title="Ubah Dominasi Tangan"
      >
        <span>{isRightHanded ? '🖐️ Kanan' : '🖐️ Kiri'}</span>
      </button>

      {/* Quick Settings Icon */}
      <button
        onClick={onOpenQuickSettings}
        className="w-10 h-10 rounded-full bg-white/10 hover:bg-white/20 active:scale-90 flex items-center justify-center text-white border border-white/20"
        title="Pengaturan Cepat"
      >
        <SlidersHorizontal className="w-4 h-4 text-white" />
      </button>
    </div>
  );

  return (
    <div className="w-full px-6 py-4 flex items-center justify-between bg-gradient-to-t from-black via-black/80 to-transparent">
      {isRightHanded ? (
        <>
          {secondaryControls}
          {primaryThumbControls}
        </>
      ) : (
        <>
          {primaryThumbControls}
          {secondaryControls}
        </>
      )}
    </div>
  );
};
