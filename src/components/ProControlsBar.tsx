import React, { useState } from 'react';
import { ProControlsState, WhiteBalancePreset } from '../types/camera';
import { ISO_VALUES, SHUTTER_SPEEDS, WHITE_BALANCE_OPTIONS } from '../constants/cameraData';
import { cameraAudio } from '../utils/audio';

interface Props {
  proState: ProControlsState;
  onChange: (updater: (prev: ProControlsState) => ProControlsState) => void;
}

type TabType = 'ISO' | 'SHUTTER' | 'EV' | 'WB' | 'FOCUS' | 'RAW';

export const ProControlsBar: React.FC<Props> = ({ proState, onChange }) => {
  const [activeTab, setActiveTab] = useState<TabType>('ISO');

  const tabs: { id: TabType; label: string; value: string }[] = [
    { id: 'ISO', label: 'ISO', value: proState.iso === 0 ? 'AUTO' : `${proState.iso}` },
    { id: 'SHUTTER', label: 'SHUTTER', value: proState.shutterSpeedFraction },
    { id: 'EV', label: 'EV', value: `${proState.evCompensation >= 0 ? '+' : ''}${(proState.evCompensation * 0.5).toFixed(1)}` },
    { id: 'WB', label: 'WB', value: proState.whiteBalance },
    { id: 'FOCUS', label: 'FOCUS', value: proState.isManualFocus ? `MF ${(proState.focusDistance * 10).toFixed(0)}` : 'AF' },
    { id: 'RAW', label: 'RAW', value: proState.isRawDngEnabled ? 'DNG' : 'JPG' },
  ];

  return (
    <div className="w-full bg-black/90 border-t border-white/10 px-4 py-2 flex flex-col gap-2">
      {/* 1. Header Parameter Selector Tabs */}
      <div className="flex items-center gap-2 overflow-x-auto no-scrollbar py-1">
        {tabs.map((tab) => {
          const isSelected = activeTab === tab.id;
          return (
            <button
              key={tab.id}
              onClick={() => {
                cameraAudio.playDialTick();
                setActiveTab(tab.id);
              }}
              className={`flex flex-col items-center px-3 py-1 rounded-lg border transition-all min-w-[54px] ${
                isSelected
                  ? 'bg-amber-400/20 border-amber-400 text-amber-400'
                  : 'bg-white/5 border-white/10 text-gray-400 hover:text-white'
              }`}
            >
              <span className="text-[9px] font-bold tracking-wider">{tab.label}</span>
              <span className="text-[11px] font-mono font-bold text-white">{tab.value}</span>
            </button>
          );
        })}
      </div>

      {/* 2. Sub-control values based on active tab */}
      <div className="h-11 flex items-center justify-center">
        {activeTab === 'ISO' && (
          <div className="flex items-center gap-2 overflow-x-auto no-scrollbar w-full justify-start px-2">
            {ISO_VALUES.map((iso) => (
              <button
                key={iso}
                onClick={() => {
                  cameraAudio.playDialTick();
                  onChange((prev) => ({ ...prev, iso }));
                }}
                className={`px-3 py-1 rounded-full text-[11px] font-mono font-bold ${
                  proState.iso === iso ? 'bg-amber-400 text-black' : 'bg-white/10 text-gray-300'
                }`}
              >
                {iso === 0 ? 'AUTO' : iso}
              </button>
            ))}
          </div>
        )}

        {activeTab === 'SHUTTER' && (
          <div className="flex items-center gap-2 overflow-x-auto no-scrollbar w-full justify-start px-2">
            {SHUTTER_SPEEDS.map((shutter) => (
              <button
                key={shutter}
                onClick={() => {
                  cameraAudio.playDialTick();
                  onChange((prev) => ({ ...prev, shutterSpeedFraction: shutter }));
                }}
                className={`px-3 py-1 rounded-full text-[11px] font-mono font-bold whitespace-nowrap ${
                  proState.shutterSpeedFraction === shutter ? 'bg-amber-400 text-black' : 'bg-white/10 text-gray-300'
                }`}
              >
                {shutter}
              </button>
            ))}
          </div>
        )}

        {activeTab === 'EV' && (
          <div className="w-full flex items-center gap-3 px-4">
            <span className="text-[11px] font-mono text-gray-400">-3.0 EV</span>
            <input
              type="range"
              min="-6"
              max="6"
              step="1"
              value={proState.evCompensation}
              onChange={(e) => onChange((prev) => ({ ...prev, evCompensation: parseInt(e.target.value) }))}
              className="flex-1"
            />
            <span className="text-[11px] font-mono text-gray-400">+3.0 EV</span>
          </div>
        )}

        {activeTab === 'WB' && (
          <div className="flex items-center gap-2 overflow-x-auto no-scrollbar w-full justify-start px-2">
            {WHITE_BALANCE_OPTIONS.map((wb) => (
              <button
                key={wb.id}
                onClick={() => {
                  cameraAudio.playDialTick();
                  onChange((prev) => ({ ...prev, whiteBalance: wb.id }));
                }}
                className={`flex flex-col items-center px-3 py-1 rounded-lg text-[11px] font-bold ${
                  proState.whiteBalance === wb.id ? 'bg-amber-400 text-black' : 'bg-white/10 text-gray-300'
                }`}
              >
                <span>{wb.label}</span>
                <span className="text-[8px] font-mono opacity-80">{wb.tempKelvin}</span>
              </button>
            ))}
          </div>
        )}

        {activeTab === 'FOCUS' && (
          <div className="w-full flex items-center gap-3 px-2">
            <button
              onClick={() => onChange((prev) => ({ ...prev, isManualFocus: !prev.isManualFocus }))}
              className={`px-3 py-1 rounded-lg text-[11px] font-bold ${
                proState.isManualFocus ? 'bg-cyan-400 text-black' : 'bg-white/20 text-white'
              }`}
            >
              {proState.isManualFocus ? 'MF (Manual)' : 'AF (Auto)'}
            </button>
            {proState.isManualFocus && (
              <div className="flex-1 flex items-center gap-2">
                <span className="text-[10px] text-gray-400 font-mono">Macro</span>
                <input
                  type="range"
                  min="0"
                  max="1"
                  step="0.05"
                  value={proState.focusDistance}
                  onChange={(e) => onChange((prev) => ({ ...prev, focusDistance: parseFloat(e.target.value) }))}
                  className="flex-1"
                />
                <span className="text-[10px] text-gray-400 font-mono">Infinity</span>
              </div>
            )}
          </div>
        )}

        {activeTab === 'RAW' && (
          <div className="w-full flex items-center justify-between px-4">
            <div>
              <p className="text-[11px] font-bold text-amber-400">Sensor RAW DNG (14-bit)</p>
              <p className="text-[9px] text-gray-400">Data mentah tanpa kompresi sensor Redmi Note 9</p>
            </div>
            <button
              onClick={() => onChange((prev) => ({ ...prev, isRawDngEnabled: !prev.isRawDngEnabled }))}
              className={`px-4 py-1.5 rounded-full text-[11px] font-bold font-mono transition-all ${
                proState.isRawDngEnabled
                  ? 'bg-amber-400 text-black font-extrabold shadow-md'
                  : 'bg-white/20 text-gray-300'
              }`}
            >
              {proState.isRawDngEnabled ? 'AKTIF (DNG)' : 'NONAKTIF (JPG)'}
            </button>
          </div>
        )}
      </div>
    </div>
  );
};
