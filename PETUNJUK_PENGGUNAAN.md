# Petunjuk Penggunaan Aplikasi DATANI

Aplikasi DATANI (Sistem Informasi Data Petani) adalah aplikasi desktop berbasis JavaFX yang dirancang dengan sistem multi-peran (Petugas BPP dan Petani).

## 1. Persyaratan Sistem
- **Java Runtime Environment (JRE) / Java Development Kit (JDK)**: Minimal versi 11 (disarankan JDK 11 atau yang lebih baru).
- **Sistem Operasi**: Windows, macOS, atau Linux.

## 2. Cara Menjalankan Aplikasi
Aplikasi telah di-*build* menjadi file JAR mandiri (*Fat JAR*) yang sudah mencakup semua dependensi (termasuk *library* untuk ekspor PDF dan pemrosesan XML). 

File tersebut berada di: `target/datani.jar`

**Melalui Command Prompt (CMD) / Terminal:**
1. Buka CMD atau Terminal.
2. Arahkan direktori (menggunakan perintah `cd`) ke folder `target/` di dalam proyek ini, atau jalankan perintah langsung dari *root* proyek:
   ```bash
   java -jar target/datani.jar
   ```

**Melalui File Explorer (Hanya Windows):**
- Anda juga dapat melakukan klik ganda (*double-click*) pada file `datani.jar` yang ada di dalam folder `target` jika pengaturan default untuk ekstensi `.jar` sudah dikaitkan dengan Java.

## 3. Akun Default untuk Pengujian (Testing)
Data bawaan (dummy data) akan dibuat secara otomatis di dalam folder `data/` saat aplikasi pertama kali dijalankan. Berikut adalah daftar akun yang dapat Anda gunakan untuk login:

### Akun Petugas BPP
- **Username:** `petugas`
- **Password:** `petugas123`

### Akun Petani
Petani melakukan login menggunakan NIK sebagai username. Berikut 3 akun petani yang sudah terdaftar:

1. **Budi Santoso**
   - **NIK (Username):** `3201012501900002`
   - **Password:** `petani123`
2. **Wayan Sudarma**
   - **NIK (Username):** `3309022803880003`
   - **Password:** `petani123`
3. **Siti Aminah**
   - **NIK (Username):** `3215017004950004`
   - **Password:** `petani123`

## 4. Struktur Data yang Diimplementasikan (Tugas ASD)
Aplikasi ini juga menerapkan beberapa struktur data untuk memenuhi kriteria penilaian mata kuliah Algoritma dan Struktur Data:
1. **Binary Search Tree (BST)**: Digunakan untuk fitur pencarian data Petani berdasarkan NIK yang ada di menu Verifikasi Pengajuan dan Laporan Gagal Panen (pada Dashboard Petugas).
2. **Queue (Antrean)**: Digunakan pada antrean laporan gagal panen (First In First Out) di sisi petugas.
3. **Quick Sort**: Digunakan untuk mengurutkan daftar tingkat kerusakan lahan dari yang paling parah ke yang paling ringan.

Semoga berhasil!
