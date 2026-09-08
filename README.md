# Kamera Pro Redmi Note 9 (merlin)

Aplikasi kamera profesional untuk Xiaomi Redmi Note 9 (Helio G85) dengan dukungan Camera2 API 60 FPS, kontrol manual ISO/Shutter/EV/WB/Focus, dukungan RAW DNG 14-bit, serta mode sinematik bokeh anamorfik 21:9.

---

## 📱 Cara Build APK di GitHub Actions

Repositori ini sudah dilengkapi alur kerja otomatis **GitHub Actions** untuk membangun berkas APK Android (`.apk`).

### Langkah-langkah:

1. **Push Proyek ke GitHub**:
   ```bash
   git add .
   git commit -m "feat: setup kamera pro redmi note 9 with github apk build"
   git push origin main
   ```

2. **Buka Tab Actions di GitHub**:
   - Buka halaman repositori GitHub Anda di browser.
   - Klik tab **Actions** di bilah navigasi atas.
   - Pilih alur kerja **"Build Android APK"**.

3. **Mulai Build**:
   - Build akan otomatis dipicu saat Anda melakukan `push` ke branch `main` atau `master`.
   - Anda juga bisa memicunya secara manual kapan saja dengan mengklik tombol **"Run workflow"** &rarr; **Run workflow**.

4. **Unduh Berkas APK**:
   - Tunggu sekitar 2 - 3 menit hingga status workflow berubah menjadi hijau (✅ Selesai).
   - Klik riwayat build yang telah selesai tersebut.
   - Gulir ke bagian bawah pada kotak **Artifacts**.
   - Unduh **`Kamera-Pro-Note-9-Debug-APK`**.
   - Ekstrak berkas zip dan pasang langsung APK di perangkat Redmi Note 9 Anda!

---

## 🛠️ Build APK Secara Lokal (Opsional)

Jika Anda memiliki Android Studio atau Gradle di komputer lokal:

```bash
# Berikan izin eksekusi gradlew
chmod +x gradlew

# Build Debug APK
./gradlew assembleDebug

# Lokasi APK:
# app/build/outputs/apk/debug/app-debug.apk
```
