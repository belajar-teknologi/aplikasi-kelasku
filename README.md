# Asisten Guru Pintar (EduTracker) 🎓📱

Aplikasi Asisten Guru Pintar (EduTracker) adalah aplikasi Android Native modern berbasis **Kotlin** dan **Jetpack Compose (Material 3)** yang dirancang untuk membantu para guru di Indonesia dalam mengelola administrasi kelas, rekap nilai siswa, presensi digital, kuis daring interaktif, hingga laporan ke wali murid.

---

## 🌟 Fitur Utama Aplikasi

1. **📊 Dasbor Analisis Kelas Real-Time**
   - Ringkasan kehadiran harian, rata-rata nilai kelas, dan penguasaan per mata pelajaran.
   - Peringkat 5 besar siswa berprestasi & deteksi dini siswa butuh bimbingan (di bawah KKM).

2. **📋 Presensi Kehadiran Digital**
   - Pencatatan cepat status: *Hadir*, *Sakit*, *Izin*, *Alpa*.
   - Tombol satu sentuhan *"Tandai Semua Hadir"*.
   - Integrasi langsung kirim status presensi ke **WhatsApp** orang tua/wali murid.

3. **📝 Buku Nilai & Evaluasi KKM**
   - Pengelolaan nilai Tugas, Kuis, UTS, UAS, dan Praktik.
   - Standar ketuntasan KKM (75).
   - Pengiriman rekapitulasi nilai berkala ke **WhatsApp** & **Email** wali murid.

4. **🎯 Kuis Daring Interaktif & Generator Link**
   - Pembuatan kuis daring dengan materi dan pilihan ganda.
   - Generator link & kode kuis unik (contoh: `KUIS-MAT-5A`) untuk dibagikan ke siswa via WhatsApp.
   - Fitur simulasi ujian interaktif siswa dengan timer, penilaian instan, dan pencatatan nilai otomatis.

5. **📄 Export Laporan PDF Resmi**
   - Pembuatan dan download dokumen PDF resmi rekapitulasi nilai dan kehadiran siswa siap cetak.

6. **📅 Kalender Akademik & Tugas Rumah (PR)**
   - Jadwal terpadu jam pelajaran, PTS, PAS, dan pertemuan wali murid.
   - Sinkronisasi tugas dan *push notification* pengingat.

7. **💬 Chat Interaktif Guru - Wali Murid**
   - Ruang percakapan per siswa dengan template respons cepat.

---

## 💻 Cara Menjalankan Proyek

### Opsi A: Membuka di Android Studio
1. Clone repository ini:
   ```bash
   git clone <URL_REPO_GITHUB_ANDA>
   ```
2. Buka aplikasi **Android Studio**.
3. Pilih **File > Open**, lalu pilih folder proyek ini.
4. Tunggu proses *Gradle Sync* selesai.
5. Jalankan aplikasi di HP Android fisik (via kabel USB) atau Android Emulator dengan menekan tombol **Run (Play Hijau)**.

### Opsi B: Membuka Web Portal di GitHub Pages
- Buka file `index.html` langsung di browser, atau aktifkan **GitHub Pages** pada pengaturan repositori GitHub Anda (*Settings > Pages > Deploy from branch main*).

---

## 🛠️ Spesifikasi Teknologi
- **Bahasa**: Kotlin
- **UI Framework**: Jetpack Compose (Material 3)
- **Arsitektur**: MVVM (Model-View-ViewModel) + StateFlow
- **Web Portal**: HTML5 & Tailwind CSS
