import React, { useRef, useEffect } from 'react';
import { CameraMode } from '../types/camera';
import { CAMERA_MODES } from '../constants/cameraData';
import { cameraAudio } from '../utils/audio';

interface Props {
  currentMode: CameraMode;
  onSelectMode: (mode: CameraMode) => void;
}

export const ModeDialCarousel: React.FC<Props> = ({
  currentMode,
  onSelectMode,
}) => {
  const containerRef = useRef<HTMLDivElement>(null);

  // Auto scroll to center active mode
  useEffect(() => {
    if (!containerRef.current) return;
    const activeEl = containerRef.current.querySelector<HTMLButtonElement>(`[data-mode="${currentMode}"]`);
    if (activeEl) {
      activeEl.scrollIntoView({ behavior: 'smooth', block: 'nearest', inline: 'center' });
    }
  }, [currentMode]);

  return (
    <div className="w-full relative py-2 overflow-hidden">
      {/* Subtle indicator pip in center */}
      <div className="absolute bottom-0.5 left-1/2 -translate-x-1/2 w-1.5 h-1.5 bg-amber-400 rounded-full pointer-events-none" />

      <div
        ref={containerRef}
        className="flex items-center gap-4 overflow-x-auto no-scrollbar px-32 py-1 scroll-smooth"
      >
        {CAMERA_MODES.map((mode) => {
          const isSelected = mode.id === currentMode;
          return (
            <button
              key={mode.id}
              data-mode={mode.id}
              onClick={() => {
                cameraAudio.playDialTick();
                onSelectMode(mode.id);
              }}
              className={`shrink-0 flex items-center gap-1.5 px-3.5 py-1.5 rounded-full transition-all text-[11px] font-bold tracking-wider ${
                isSelected
                  ? 'bg-amber-400/20 border border-amber-400 text-amber-400 scale-105 shadow-sm'
                  : 'text-gray-400 hover:text-white border border-transparent'
              }`}
            >
              <span>{mode.label}</span>
              {mode.badge && (
                <span className={`text-[8px] font-mono px-1 py-0.2 rounded font-black ${
                  isSelected ? 'bg-amber-400 text-black' : 'bg-white/10 text-gray-400'
                }`}>
                  {mode.badge}
                </span>
              )}
            </button>
          );
        })}
      </div>
    </div>
  );
};
