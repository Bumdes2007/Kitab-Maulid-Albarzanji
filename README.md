# Kitab Maulid Al-Barzanji — Aplikasi Android Gratis

Project Android Studio untuk membaca **Kitab Maulid Al-Barzanji** secara offline. PDF sumber disimpan langsung di dalam aplikasi sehingga tidak membutuhkan server atau koneksi internet untuk membaca.

## Cara paling mudah membuat APK gratis

### Opsi A — GitHub Actions (tanpa memasang Android Studio)
1. Buat akun GitHub gratis di https://github.com/ jika belum punya.
2. Buat repository baru, misalnya `KitabMaulidAlBarzanji`.
3. Upload seluruh isi folder project ini ke repository.
4. Buka tab **Actions**.
5. Pilih workflow **Build APK Gratis**.
6. Klik **Run workflow**.
7. Setelah selesai, buka hasil workflow dan download artifact **KitabMaulidAlBarzanji-debug**.
8. Di dalam ZIP artifact terdapat `app-debug.apk` yang dapat dipasang di Android.

Workflow otomatis memasang Java, Android SDK, Build Tools, dan Gradle. Tidak perlu membeli hosting, server, atau software berbayar.

### Opsi B — Android Studio
1. Buka folder project ini di Android Studio.
2. Tunggu Gradle selesai melakukan sync.
3. Pilih **Build → Build APK(s)**.
4. APK debug akan berada di:
   `app/build/outputs/apk/debug/app-debug.apk`

## Isi aplikasi
- Pembaca PDF offline.
- Tombol halaman sebelumnya/berikutnya.
- Geser kiri/kanan untuk berpindah halaman.
- Zoom hingga 4×.
- Daftar halaman.
- Tidak membutuhkan izin internet.

## Catatan
APK yang dihasilkan oleh workflow adalah **debug APK**, cocok untuk dipasang langsung pada HP Android dan untuk penggunaan pribadi. Untuk publikasi ke Google Play, diperlukan proses signing/release dan akun Google Play Developer.
