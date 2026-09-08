import React, { useState } from 'react';
import { CapturedItem } from '../types/camera';
import { X, Download, Trash2, ChevronLeft, ChevronRight, Play, Info } from 'lucide-react';

interface Props {
  items: CapturedItem[];
  onDeleteItem: (id: string) => void;
  onClose: () => void;
}

export const GalleryModal: React.FC<Props> = ({ items, onDeleteItem, onClose }) => {
  const [selectedIndex, setSelectedIndex] = useState<number>(0);

  if (items.length === 0) {
    return (
      <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/80 backdrop-blur-md">
        <div className="w-full max-w-sm bg-[#16161D] border border-white/20 rounded-3xl p-6 text-center flex flex-col items-center gap-4">
          <p className="text-sm text-gray-300 font-medium">Belum ada foto atau video yang diambil.</p>
          <button
            onClick={onClose}
            className="px-6 py-2 rounded-full bg-amber-400 text-black font-bold text-xs"
          >
            Kembali ke Kamera
          </button>
        </div>
      </div>
    );
  }

  const currentItem = items[selectedIndex] || items[0];

  const handleDownload = () => {
    const a = document.createElement('a');
    a.href = currentItem.dataUrl;
    a.download = `REDMI_NOTE9_${currentItem.timestamp}.${currentItem.isVideo ? 'webm' : currentItem.isRaw ? 'dng' : 'jpg'}`;
    a.click();
  };

  return (
    <div className="fixed inset-0 z-50 flex flex-col bg-black/95 backdrop-blur-md animate-fade-in select-none">
      {/* Top Header */}
      <div className="flex items-center justify-between px-4 py-3 bg-black/50 border-b border-white/10">
        <div className="flex items-center gap-2">
          <span className="text-xs font-mono text-amber-400 font-bold">
            {selectedIndex + 1} / {items.length}
          </span>
          {currentItem.isRaw && (
            <span className="text-[10px] font-mono bg-purple-500/30 border border-purple-400 text-purple-300 px-1.5 py-0.5 rounded font-bold">
              RAW DNG
            </span>
          )}
          {currentItem.isVideo && (
            <span className="text-[10px] font-mono bg-red-500/30 border border-red-400 text-red-300 px-1.5 py-0.5 rounded font-bold">
              VIDEO 60FPS
            </span>
          )}
        </div>

        <div className="flex items-center gap-2">
          <button
            onClick={handleDownload}
            className="p-2 rounded-full bg-white/10 hover:bg-white/20 text-white"
            title="Download Media"
          >
            <Download className="w-4 h-4" />
          </button>
          <button
            onClick={() => {
              onDeleteItem(currentItem.id);
              if (selectedIndex > 0) setSelectedIndex(selectedIndex - 1);
            }}
            className="p-2 rounded-full bg-red-500/20 hover:bg-red-500/40 text-red-400"
            title="Hapus"
          >
            <Trash2 className="w-4 h-4" />
          </button>
          <button
            onClick={onClose}
            className="p-2 rounded-full bg-white/10 hover:bg-white/20 text-white"
          >
            <X className="w-4 h-4" />
          </button>
        </div>
      </div>

      {/* Main Media Preview Area */}
      <div className="flex-1 relative flex items-center justify-center p-4">
        {currentItem.isVideo ? (
          <video
            src={currentItem.dataUrl}
            controls
            autoPlay
            className="max-h-full max-w-full rounded-xl object-contain shadow-2xl"
          />
        ) : (
          <img
            src={currentItem.dataUrl}
            alt="Capture preview"
            className="max-h-full max-w-full rounded-xl object-contain shadow-2xl"
          />
        )}

        {/* Navigation Arrows */}
        {selectedIndex > 0 && (
          <button
            onClick={() => setSelectedIndex(selectedIndex - 1)}
            className="absolute left-4 p-2 rounded-full bg-black/60 border border-white/20 text-white hover:bg-black/90"
          >
            <ChevronLeft className="w-5 h-5" />
          </button>
        )}
        {selectedIndex < items.length - 1 && (
          <button
            onClick={() => setSelectedIndex(selectedIndex + 1)}
            className="absolute right-4 p-2 rounded-full bg-black/60 border border-white/20 text-white hover:bg-black/90"
          >
            <ChevronRight className="w-5 h-5" />
          </button>
        )}
      </div>

      {/* Bottom Metadata Bar */}
      <div className="p-4 bg-black/80 border-t border-white/10 flex items-center justify-between text-[11px] font-mono text-gray-400">
        <div>
          <p className="text-white font-bold">Xiaomi Redmi Note 9 • Helio G85</p>
          <p>{new Date(currentItem.timestamp).toLocaleString('id-ID')}</p>
        </div>
        <div className="text-right">
          <p className="text-amber-400">
            {currentItem.mode} • ISO {currentItem.iso || 'AUTO'} • {currentItem.shutter}
          </p>
          <p>{currentItem.resolution}</p>
        </div>
      </div>
    </div>
  );
};
