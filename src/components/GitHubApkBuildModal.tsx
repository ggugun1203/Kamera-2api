import React from 'react';
import { X, Github, Download, CheckCircle2, Terminal, HelpCircle, ArrowRight } from 'lucide-react';

interface Props {
  onClose: () => void;
}

export const GitHubApkBuildModal: React.FC<Props> = ({ onClose }) => {
  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/80 backdrop-blur-md animate-fade-in">
      <div className="w-full max-w-md bg-[#16161F] border border-amber-400/40 rounded-3xl p-6 shadow-2xl flex flex-col gap-4 max-h-[90vh] overflow-y-auto no-scrollbar">
        {/* Header */}
        <div className="flex items-center justify-between border-b border-white/10 pb-3">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-2xl bg-amber-400/20 border border-amber-400 flex items-center justify-center text-amber-400">
              <Github className="w-6 h-6" />
            </div>
            <div>
              <h2 className="text-base font-bold text-white leading-tight">Build APK di GitHub</h2>
              <p className="text-[11px] font-mono text-amber-400">GitHub Actions CI/CD Pipeline</p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="w-8 h-8 rounded-full bg-white/10 flex items-center justify-center text-gray-300 hover:text-white"
          >
            <X className="w-4 h-4" />
          </button>
        </div>

        {/* Workflow Info Card */}
        <div className="bg-[#1D1D28] rounded-2xl p-4 border border-white/10 flex flex-col gap-2">
          <div className="flex items-center gap-2 text-emerald-400 text-[11px] font-bold tracking-wider">
            <CheckCircle2 className="w-4 h-4" />
            <span>WORKFLOW TERPASANG OTOMATIS</span>
          </div>
          <p className="text-xs text-gray-300 leading-relaxed">
            Berkas alur kerja telah ditambahkan di:
            <br />
            <code className="bg-black/60 px-2 py-0.5 rounded text-amber-300 font-mono text-[11px]">
              .github/workflows/build-apk.yml
            </code>
          </p>
        </div>

        {/* Steps to build */}
        <div className="flex flex-col gap-3">
          <h3 className="text-xs font-bold text-gray-300 tracking-wider flex items-center gap-1.5">
            <Terminal className="w-4 h-4 text-cyan-400" />
            <span>CARA MEMULAI BUILD & UNDUH APK:</span>
          </h3>

          <div className="space-y-2.5 text-xs text-gray-300">
            <div className="flex items-start gap-2.5 bg-black/40 p-3 rounded-xl border border-white/5">
              <span className="w-5 h-5 rounded-full bg-amber-400/20 text-amber-400 flex items-center justify-center shrink-0 font-mono font-bold text-[10px]">
                1
              </span>
              <div>
                <strong className="text-white">Push ke GitHub:</strong>
                <p className="text-gray-400 text-[11px] mt-0.5">
                  Push repositori ini ke GitHub (cabang <code>main</code> atau <code>master</code>).
                </p>
              </div>
            </div>

            <div className="flex items-start gap-2.5 bg-black/40 p-3 rounded-xl border border-white/5">
              <span className="w-5 h-5 rounded-full bg-amber-400/20 text-amber-400 flex items-center justify-center shrink-0 font-mono font-bold text-[10px]">
                2
              </span>
              <div>
                <strong className="text-white">Masuk ke Tab Actions:</strong>
                <p className="text-gray-400 text-[11px] mt-0.5">
                  Buka repositori di GitHub, pilih menu <strong>Actions</strong> &rarr; pilih alur kerja <strong>"Build Android APK"</strong>.
                </p>
              </div>
            </div>

            <div className="flex items-start gap-2.5 bg-black/40 p-3 rounded-xl border border-white/5">
              <span className="w-5 h-5 rounded-full bg-amber-400/20 text-amber-400 flex items-center justify-center shrink-0 font-mono font-bold text-[10px]">
                3
              </span>
              <div>
                <strong className="text-white">Run Workflow (Manual / Otomatis):</strong>
                <p className="text-gray-400 text-[11px] mt-0.5">
                  Build akan otomatis berjalan saat ada push, atau klik tombol <strong>"Run workflow"</strong> kapan pun Anda inginkan.
                </p>
              </div>
            </div>

            <div className="flex items-start gap-2.5 bg-black/40 p-3 rounded-xl border border-white/5">
              <span className="w-5 h-5 rounded-full bg-emerald-400/20 text-emerald-400 flex items-center justify-center shrink-0 font-mono font-bold text-[10px]">
                4
              </span>
              <div>
                <strong className="text-white">Unduh APK di Artifacts:</strong>
                <p className="text-gray-400 text-[11px] mt-0.5">
                  Setelah build selesai (sekitar 2-3 menit), gulir ke bagian <strong>Artifacts</strong> dan unduh <strong>Kamera-Pro-Note-9-Debug-APK</strong>. Pasang langsung di Redmi Note 9 Anda!
                </p>
              </div>
            </div>
          </div>
        </div>

        {/* Action Button */}
        <button
          onClick={onClose}
          className="w-full py-3 rounded-2xl bg-amber-400 hover:bg-amber-300 active:scale-95 text-black font-bold text-[13px] transition-all shadow-lg flex items-center justify-center gap-2 mt-1"
        >
          <span>Siap, Mengerti</span>
          <ArrowRight className="w-4 h-4" />
        </button>
      </div>
    </div>
  );
};
