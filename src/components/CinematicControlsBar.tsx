import React from 'react';
import { CinematicState } from '../types/camera';
import { APERTURE_VALUES, CINEMATIC_PROFILES } from '../constants/cameraData';
import { cameraAudio } from '../utils/audio';

interface Props {
  cinematicState: CinematicState;
  onChange: (updater: (prev: CinematicState) => CinematicState) => void;
}

export const CinematicControlsBar: React.FC<Props> = ({ cinematicState, onChange }) => {
  return (
    <div className="w-full bg-black/90 border-t border-white/10 px-4 py-2 flex flex-col gap-2">
      {/* 1. Aperture Bokeh Selector */}
      <div className="flex items-center justify-between">
        <span className="text-[10px] font-bold tracking-wider text-cyan-400">APERTUR BOKEH</span>
        <span className="text-[11px] font-mono text-white">f/{cinematicState.bokehAperture}</span>
      </div>

      <div className="flex items-center gap-2 overflow-x-auto no-scrollbar py-1">
        {APERTURE_VALUES.map((apt) => {
          const isSelected = cinematicState.bokehAperture === apt;
          return (
            <button
              key={apt}
              onClick={() => {
                cameraAudio.playDialTick();
                onChange((prev) => ({ ...prev, bokehAperture: apt }));
              }}
              className={`px-3 py-1 rounded-full text-[11px] font-mono font-bold transition-all ${
                isSelected
                  ? 'bg-cyan-400 text-black shadow-md scale-105'
                  : 'bg-white/10 text-gray-300 hover:text-white'
              }`}
            >
              f/{apt}
            </button>
          );
        })}
      </div>

      {/* 2. Film Color Profiles */}
      <div className="flex items-center justify-between mt-1">
        <span className="text-[10px] font-bold tracking-wider text-amber-400">PROFIL WARNA FILM</span>
      </div>

      <div className="flex items-center gap-2 overflow-x-auto no-scrollbar py-1">
        {CINEMATIC_PROFILES.map((prof) => {
          const isSelected = cinematicState.profileId === prof.id;
          return (
            <button
              key={prof.id}
              onClick={() => {
                cameraAudio.playDialTick();
                onChange((prev) => ({ ...prev, profileId: prof.id }));
              }}
              className={`flex flex-col items-start px-3 py-1.5 rounded-xl border transition-all text-left min-w-[110px] ${
                isSelected
                  ? 'bg-amber-400/20 border-amber-400 text-amber-400'
                  : 'bg-white/5 border-white/10 text-gray-300 hover:text-white'
              }`}
            >
              <span className="text-[11px] font-bold">{prof.name}</span>
              <span className="text-[8px] opacity-70 font-mono">{prof.subText}</span>
            </button>
          );
        })}
      </div>
    </div>
  );
};
