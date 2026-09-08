import React from 'react';
import { Camera2Info } from '../types/camera';
import { Smartphone, X, CheckCircle2, ShieldCheck, Zap, Gauge, Info } from 'lucide-react';

interface Props {
  camera2Info: Camera2Info;
  fps: number;
  onToggleForce60Fps: () => void;
  onClose: () => void;
}

export const RedmiCamera2DiagnosticModal: React.FC<Props> = ({
  camera2Info,
  fps,
  onToggleForce60Fps,
  onClose,
}) => {
  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/80 backdrop-blur-sm animate-fade-in">
      <div className="w-full max-w-md bg-[#121216] border border-amber-400/40 rounded-3xl p-6 shadow-2xl flex flex-col gap-4 max-h-[90vh] overflow-y-auto no-scrollbar">
        {/* Header */}
        <div className="flex items-center justify-between border-b border-white/10 pb-3">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-2xl bg-amber-400/20 border border-amber-400 flex items-center justify-center text-amber-400">
              <Smartphone className="w-6 h-6" />
            </div>
            <div>
              <h2 className="text-base font-bold text-white leading-tight">Redmi Note 9 (merlin)</h2>
              <p className="text-[11px] font-mono text-amber-400">Camera2 API & 60 FPS Enforcer</p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="w-8 h-8 rounded-full bg-white/10 flex items-center justify-center text-gray-300 hover:text-white"
          >
            <X className="w-4 h-4" />
          </button>
        </div>

        {/* 1. Hardware Status Section */}
        <div className="bg-[#1A1A22] rounded-2xl p-4 border border-white/10 flex flex-col gap-2.5">
          <div className="flex items-center gap-2 text-cyan-400 text-[11px] font-bold tracking-wider">
            <ShieldCheck className="w-4 h-4" />
            <span>STATUS SENSOR HARDWARE</span>
          </div>
          <div className="flex justify-between text-[11px] border-b border-white/5 pb-1.5">
            <span className="text-gray-400">SoC Chipset</span>
            <span className="font-mono text-white font-medium">MediaTek Helio G85 (Octa-core)</span>
          </div>
          <div className="flex justify-between text-[11px] border-b border-white/5 pb-1.5">
            <span className="text-gray-400">Sensor Utama Belakang</span>
            <span className="font-mono text-white font-medium">Samsung GM1 48MP Quad-Bayer</span>
          </div>
          <div className="flex justify-between text-[11px] border-b border-white/5 pb-1.5">
            <span className="text-gray-400">Tingkat Camera2 API</span>
            <span className="font-mono text-amber-400 font-bold">INFO_SUPPORTED_HARDWARE_LEVEL_FULL</span>
          </div>
          <div className="flex justify-between text-[11px]">
            <span className="text-gray-400">Dukungan RAW (DNG)</span>
            <span className="font-mono text-emerald-400 font-bold flex items-center gap-1">
              <CheckCircle2 className="w-3.5 h-3.5" /> AKTIF (RAW10 / DNG)
            </span>
          </div>
        </div>

        {/* 2. 60 FPS Enforcer Hook */}
        <div className="bg-[#1A1A22] rounded-2xl p-4 border border-white/10 flex flex-col gap-3">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2 text-amber-400 text-[11px] font-bold tracking-wider">
              <Zap className="w-4 h-4" />
              <span>PIPELINE PAKSA 60 FPS</span>
            </div>
            <button
              onClick={onToggleForce60Fps}
              className={`px-3 py-1 rounded-full text-[11px] font-bold font-mono transition-all ${
                camera2Info.isForced60FpsEnabled
                  ? 'bg-emerald-500 text-black shadow-md'
                  : 'bg-white/20 text-gray-400'
              }`}
            >
              {camera2Info.isForced60FpsEnabled ? 'ENFORCED [60,60]' : 'MIUI 30 FPS'}
            </button>
          </div>

          <p className="text-[11px] text-gray-300 leading-relaxed">
            Secara bawaan, aplikasi kamera MIUI membatasi perekaman video Redmi Note 9 pada 30 FPS. Mode ini
            menginjeksi parameter Camera2 <code className="bg-black/40 px-1 py-0.5 rounded text-amber-300">CONTROL_AE_TARGET_FPS_RANGE = [60, 60]</code> dan mengunci interval frame sensor pada 16.6ms.
          </p>

          {/* Real-time FPS meter */}
          <div className="bg-black/60 rounded-xl p-3 border border-white/10 flex items-center justify-between">
            <div className="flex items-center gap-2">
              <Gauge className="w-4 h-4 text-emerald-400" />
              <span className="text-[11px] text-gray-300">FPS Real-Time Viewfinder:</span>
            </div>
            <span className="text-lg font-mono font-bold text-emerald-400">
              {fps.toFixed(1)} FPS
            </span>
          </div>
        </div>

        {/* 3. Helpful Advice Card */}
        <div className="bg-amber-400/10 border border-amber-400/30 rounded-2xl p-3.5 flex items-start gap-2.5">
          <Info className="w-4 h-4 text-amber-400 shrink-0 mt-0.5" />
          <p className="text-[10px] text-gray-300 leading-normal">
            <strong>Tips Perekaman:</strong> Untuk hasil video 60 FPS maksimal tanpa stutter di pencahayaan redup, gunakan Shutter Speed manual 1/60s atau lebih cepat pada menu Pro.
          </p>
        </div>

        {/* Close button */}
        <button
          onClick={onClose}
          className="w-full py-3 rounded-2xl bg-amber-400 hover:bg-amber-300 active:scale-95 text-black font-bold text-[13px] transition-all shadow-lg"
        >
          Tutup & Lanjutkan Memotret
        </button>
      </div>
    </div>
  );
};
