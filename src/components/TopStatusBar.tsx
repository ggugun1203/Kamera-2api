import React from 'react';
import { Camera2Info, FlashMode } from '../types/camera';
import { Zap, ZapOff, Sliders, Cpu, Activity } from 'lucide-react';

interface Props {
  fps: number;
  camera2Info: Camera2Info;
  flashMode: FlashMode;
  isRawEnabled: boolean;
  onFlashToggle: () => void;
  onOpenQuickSettings: () => void;
  onOpenDiagnostics: () => void;
}

export const TopStatusBar: React.FC<Props> = ({
  fps,
  camera2Info,
  flashMode,
  isRawEnabled,
  onFlashToggle,
  onOpenQuickSettings,
  onOpenDiagnostics,
}) => {
  return (
    <div className="absolute top-0 inset-x-0 z-30 flex items-center justify-between px-4 pt-3 pb-2 bg-gradient-to-b from-black/80 via-black/40 to-transparent">
      {/* 1. Device and Camera2 60 FPS Badge */}
      <button
        onClick={onOpenDiagnostics}
        className="flex items-center gap-1.5 px-3 py-1.5 rounded-full bg-black/60 border border-amber-400/40 backdrop-blur-md hover:border-amber-400 transition-all active:scale-95"
      >
        <Cpu className="w-3.5 h-3.5 text-amber-400" />
        <span className="text-[11px] font-bold text-white tracking-wide">Redmi Note 9</span>
        <div className="w-1.5 h-1.5 rounded-full bg-emerald-400 animate-ping" />
        <span className="text-[10px] font-mono font-bold text-emerald-400">
          {fps >= 55 ? '60 FPS' : `${fps.toFixed(0)} FPS`}
        </span>
      </button>

      {/* 2. Top Controls: Flash, RAW Tag, Quick Settings */}
      <div className="flex items-center gap-2">
        {/* RAW DNG status badge */}
        {isRawEnabled && (
          <span className="px-2 py-0.5 rounded-md bg-amber-500/25 border border-amber-400 text-amber-300 text-[10px] font-mono font-bold tracking-wider">
            RAW DNG
          </span>
        )}

        {/* Flash button */}
        <button
          onClick={onFlashToggle}
          className="w-8 h-8 rounded-full bg-black/50 border border-white/20 flex items-center justify-center text-white active:scale-90 hover:bg-black/70 transition-all"
          title="Flash Mode"
        >
          {flashMode === 'OFF' ? (
            <ZapOff className="w-4 h-4 text-gray-400" />
          ) : (
            <Zap className={`w-4 h-4 ${flashMode === 'ON' || flashMode === 'TORCH' ? 'text-amber-400 fill-amber-400' : 'text-cyan-400'}`} />
          )}
        </button>

        {/* Quick Settings button */}
        <button
          onClick={onOpenQuickSettings}
          className="w-8 h-8 rounded-full bg-black/50 border border-white/20 flex items-center justify-center text-white active:scale-90 hover:bg-black/70 transition-all"
          title="Pengaturan Cepat"
        >
          <Sliders className="w-4 h-4 text-white" />
        </button>
      </div>
    </div>
  );
};
