import React from 'react';
import { FlashMode, GridType } from '../types/camera';
import { X, SlidersHorizontal, Grid, Zap, Compass, BarChart2, Video, FileText } from 'lucide-react';
import { cameraAudio } from '../utils/audio';

interface Props {
  flashMode: FlashMode;
  gridType: GridType;
  isHorizonEnabled: boolean;
  isHistogramEnabled: boolean;
  isForced60Fps: boolean;
  isRawEnabled: boolean;
  onFlashChange: (mode: FlashMode) => void;
  onGridChange: (grid: GridType) => void;
  onToggleHorizon: () => void;
  onToggleHistogram: () => void;
  onToggleForce60Fps: () => void;
  onToggleRaw: () => void;
  onClose: () => void;
}

export const QuickSettingsDrawer: React.FC<Props> = ({
  flashMode,
  gridType,
  isHorizonEnabled,
  isHistogramEnabled,
  isForced60Fps,
  isRawEnabled,
  onFlashChange,
  onGridChange,
  onToggleHorizon,
  onToggleHistogram,
  onToggleForce60Fps,
  onToggleRaw,
  onClose,
}) => {
  const flashOptions: { id: FlashMode; label: string }[] = [
    { id: 'OFF', label: 'Mati' },
    { id: 'AUTO', label: 'Otomatis' },
    { id: 'ON', label: 'Aktif' },
    { id: 'TORCH', label: 'Senter' },
  ];

  const gridOptions: { id: GridType; label: string }[] = [
    { id: 'NONE', label: 'Nonaktif' },
    { id: 'RULE_OF_THIRDS', label: '3x3 (Thirds)' },
    { id: 'GOLDEN_RATIO', label: 'Golden Ratio' },
    { id: 'CROSSHAIR', label: 'Titik Tengah' },
  ];

  return (
    <div className="fixed inset-0 z-50 flex items-end sm:items-center justify-center bg-black/70 backdrop-blur-sm animate-fade-in">
      <div className="w-full max-w-md bg-[#16161D] border-t sm:border border-white/20 sm:rounded-3xl rounded-t-3xl p-6 shadow-2xl flex flex-col gap-4 max-h-[85vh] overflow-y-auto no-scrollbar">
        {/* Header */}
        <div className="flex items-center justify-between border-b border-white/10 pb-3">
          <div className="flex items-center gap-2">
            <SlidersHorizontal className="w-5 h-5 text-amber-400" />
            <h3 className="text-base font-bold text-white">Pengaturan Cepat</h3>
          </div>
          <button
            onClick={onClose}
            className="w-8 h-8 rounded-full bg-white/10 flex items-center justify-center text-gray-300 hover:text-white"
          >
            <X className="w-4 h-4" />
          </button>
        </div>

        {/* 1. Flash Mode */}
        <div className="flex flex-col gap-2">
          <div className="flex items-center gap-2 text-amber-400 text-[11px] font-bold tracking-wider">
            <Zap className="w-3.5 h-3.5" />
            <span>LAMPU KILAT (FLASH)</span>
          </div>
          <div className="grid grid-cols-4 gap-2">
            {flashOptions.map((opt) => (
              <button
                key={opt.id}
                onClick={() => {
                  cameraAudio.playDialTick();
                  onFlashChange(opt.id);
                }}
                className={`py-2 rounded-xl text-[11px] font-bold transition-all border ${
                  flashMode === opt.id
                    ? 'bg-amber-400 text-black border-amber-400 shadow-md'
                    : 'bg-white/5 text-gray-300 border-white/10 hover:bg-white/10'
                }`}
              >
                {opt.label}
              </button>
            ))}
          </div>
        </div>

        {/* 2. Grid Type */}
        <div className="flex flex-col gap-2">
          <div className="flex items-center gap-2 text-cyan-400 text-[11px] font-bold tracking-wider">
            <Grid className="w-3.5 h-3.5" />
            <span>GARIS BANTU (GRID & WATERPASS)</span>
          </div>
          <div className="grid grid-cols-2 gap-2">
            {gridOptions.map((opt) => (
              <button
                key={opt.id}
                onClick={() => {
                  cameraAudio.playDialTick();
                  onGridChange(opt.id);
                }}
                className={`py-2 px-3 rounded-xl text-[11px] font-bold transition-all border text-left ${
                  gridType === opt.id
                    ? 'bg-cyan-400 text-black border-cyan-400 shadow-md'
                    : 'bg-white/5 text-gray-300 border-white/10 hover:bg-white/10'
                }`}
              >
                {opt.label}
              </button>
            ))}
          </div>
        </div>

        {/* 3. Toggles */}
        <div className="flex flex-col gap-2.5 pt-2 border-t border-white/10">
          {/* Waterpass */}
          <div className="flex items-center justify-between py-1">
            <div className="flex items-center gap-2">
              <Compass className="w-4 h-4 text-emerald-400" />
              <div>
                <p className="text-[12px] font-bold text-white">Indikator Waterpass (Horizon)</p>
                <p className="text-[10px] text-gray-400">Sensor gyro untuk foto sejajar datar</p>
              </div>
            </div>
            <button
              onClick={onToggleHorizon}
              className={`w-11 h-6 rounded-full transition-colors relative ${
                isHorizonEnabled ? 'bg-amber-400' : 'bg-white/20'
              }`}
            >
              <div
                className={`w-4 h-4 rounded-full bg-black transition-transform absolute top-1 ${
                  isHorizonEnabled ? 'right-1' : 'left-1'
                }`}
              />
            </button>
          </div>

          {/* Histogram */}
          <div className="flex items-center justify-between py-1">
            <div className="flex items-center gap-2">
              <BarChart2 className="w-4 h-4 text-cyan-400" />
              <div>
                <p className="text-[12px] font-bold text-white">Live Histogram RGB</p>
                <p className="text-[10px] text-gray-400">Kurva distribusi pencahayaan real-time</p>
              </div>
            </div>
            <button
              onClick={onToggleHistogram}
              className={`w-11 h-6 rounded-full transition-colors relative ${
                isHistogramEnabled ? 'bg-cyan-400' : 'bg-white/20'
              }`}
            >
              <div
                className={`w-4 h-4 rounded-full bg-black transition-transform absolute top-1 ${
                  isHistogramEnabled ? 'right-1' : 'left-1'
                }`}
              />
            </button>
          </div>

          {/* Forced 60 FPS */}
          <div className="flex items-center justify-between py-1">
            <div className="flex items-center gap-2">
              <Video className="w-4 h-4 text-amber-400" />
              <div>
                <p className="text-[12px] font-bold text-white">Paksa 60 FPS (Camera2)</p>
                <p className="text-[10px] text-gray-400">Bypass batas 30 fps bawaan MIUI</p>
              </div>
            </div>
            <button
              onClick={onToggleForce60Fps}
              className={`w-11 h-6 rounded-full transition-colors relative ${
                isForced60Fps ? 'bg-amber-400' : 'bg-white/20'
              }`}
            >
              <div
                className={`w-4 h-4 rounded-full bg-black transition-transform absolute top-1 ${
                  isForced60Fps ? 'right-1' : 'left-1'
                }`}
              />
            </button>
          </div>

          {/* RAW DNG */}
          <div className="flex items-center justify-between py-1">
            <div className="flex items-center gap-2">
              <FileText className="w-4 h-4 text-purple-400" />
              <div>
                <p className="text-[12px] font-bold text-white">Format RAW Sensor (DNG)</p>
                <p className="text-[10px] text-gray-400">Simpan data mentah 14-bit</p>
              </div>
            </div>
            <button
              onClick={onToggleRaw}
              className={`w-11 h-6 rounded-full transition-colors relative ${
                isRawEnabled ? 'bg-purple-500' : 'bg-white/20'
              }`}
            >
              <div
                className={`w-4 h-4 rounded-full bg-black transition-transform absolute top-1 ${
                  isRawEnabled ? 'right-1' : 'left-1'
                }`}
              />
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};
