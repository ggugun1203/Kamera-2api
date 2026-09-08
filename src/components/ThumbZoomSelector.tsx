import React from 'react';
import { OneHandedHand } from '../types/camera';
import { cameraAudio } from '../utils/audio';

interface Props {
  zoomRatio: number;
  oneHandedHand: OneHandedHand;
  onZoomChange: (ratio: number) => void;
}

export const ThumbZoomSelector: React.FC<Props> = ({
  zoomRatio,
  oneHandedHand,
  onZoomChange,
}) => {
  const zoomLevels = [0.6, 1.0, 2.0, 5.0];

  return (
    <div
      className={`w-full flex px-6 py-2 transition-all ${
        oneHandedHand === 'RIGHT' ? 'justify-end' : 'justify-start'
      }`}
    >
      <div className="flex items-center gap-1 bg-black/60 border border-white/20 px-2 py-1 rounded-full backdrop-blur-md shadow-lg">
        {zoomLevels.map((lvl) => {
          const isSelected = Math.abs(zoomRatio - lvl) < 0.2;
          const label = lvl === 0.6 ? '0.6x' : `${lvl}x`;

          return (
            <button
              key={lvl}
              onClick={() => {
                cameraAudio.playDialTick();
                onZoomChange(lvl);
              }}
              className={`px-2.5 py-1 rounded-full text-[11px] font-mono font-bold transition-all ${
                isSelected
                  ? 'bg-amber-400 text-black shadow-md scale-105'
                  : 'text-white/80 hover:text-white'
              }`}
            >
              {label}
            </button>
          );
        })}
      </div>
    </div>
  );
};
