# Spesifikasi Desain: XML Persistence, Laporan Gagal Panen, & Dashboard DATANI

Dokumen ini merinci rancangan teknis untuk migrasi penyimpanan data aplikasi **DATANI** dari in-memory ke XML (menggunakan library XStream), implementasi modul pelaporan gagal panen (UC04 & UC05), dan dasbor statistik (UC06 & UC07) berbasis Java SE 9.

---

### A. Konfigurasi Dependency (`pom.xml`)
Kita menambahkan library XStream & OpenPDF ke `pom.xml` untuk serialisasi XML dan ekspor PDF:
```xml
<dependency>
    <groupId>com.thoughtworks.xstream</groupId>
    <artifactId>xstream</artifactId>
    <version>1.4.20</version>
</dependency>
<dependency>
    <groupId>com.github.librepdf</groupId>
    <artifactId>openpdf</artifactId>
    <version>1.3.30</version>
</dependency>
```

### B. Manajer Penyimpanan XML (`com.datani.service.DataService.java`)
`DataService` akan bertanggung jawab untuk memuat (*load*) dan menyimpan (*save*) data dari dan ke file XML yang terletak di folder `data/` di root proyek:
*   `data/users.xml`
*   `data/petani.xml`
*   `data/pengajuan.xml`
*   `data/gagal_panen.xml`

Setiap kali ada penambahan, perubahan, atau penghapusan data (registrasi petani baru, pengiriman pengajuan pupuk, persetujuan verifikasi, pelaporan gagal panen), `DataService` akan menserialisasikan daftar objek yang dimodifikasi ke file XML-nya masing-masing.

---

## 2. Model Data Baru (`com.datani.model.LaporanGagalPanen.java`)

Model untuk menampung data pelaporan kerusakan tanaman:
```java
package com.datani.model;

import java.time.LocalDate;

public class LaporanGagalPanen {
    private int id;
    private int petaniId;
    private String petaniNama;
    private String penyebab; // Hama/Penyakit, Kekeringan, Banjir, Bencana Alam
    private double luasTerdampak;
    private int persentaseKerusakan; // 0 - 100
    private LocalDate tanggalKejadian;
    private String fotoBukti;
    private String status; // Menunggu Peninjauan, Terverifikasi, Ditolak
    private String catatanRekomendasi;
    private LocalDate tanggalVerifikasi;
    private String verifikatorUsername;

    public LaporanGagalPanen(int id, int petaniId, String petaniNama, String penyebab, 
                              double luasTerdampak, int persentaseKerusakan, 
                              LocalDate tanggalKejadian, String fotoBukti) {
        this.id = id;
        this.petaniId = petaniId;
        this.petaniNama = petaniNama;
        this.penyebab = penyebab;
        this.luasTerdampak = luasTerdampak;
        this.persentaseKerusakan = persentaseKerusakan;
        this.tanggalKejadian = tanggalKejadian;
        this.fotoBukti = fotoBukti;
        this.status = "Menunggu Peninjauan";
    }

    // Getters and Setters ...
}
```

---

## 3. Desain Dasbor Statistik (Petani & BPP)

### A. Dashboard Petani (`com/datani/fxml/petani/DashboardPetani.fxml`)
Menyediakan ringkasan visual untuk petani:
*   **Kartu Informasi**:
    *   Total Luas Lahan (dari data pengajuan disetujui).
    *   Status Pengajuan Pupuk Terkini.
    *   Laporan Gagal Panen Aktif.
*   **Komponen Visual**:
    *   `PieChart` untuk visualisasi status pengajuan pupuk (Menunggu vs Disetujui vs Ditolak).
    *   `TableView` kuota pupuk historis 3 tahun terakhir (Tahun, Jumlah Kuota Kg, Status Penyaluran).
    *   `TableView` riwayat pengajuan terbaru (5 baris teratas).

### B. Dashboard Petugas BPP (`com/datani/fxml/petugas/DashboardPetugas.fxml`)
Menyediakan ringkasan pemantauan wilayah bagi petugas BP4 Pakem dengan tata letak yang seimbang dan penyaringan periode:
*   **Filter Periode (Baru)**:
    *   HBox berisi dua DatePicker (`dpStart` dan `dpEnd`) serta tombol "Filter" dan "Reset" untuk menyaring data pengajuan dan laporan gagal panen secara rentang waktu.
*   **Kartu Informasi**:
    *   Total Petani Terdaftar.
    *   Total Pengajuan Menunggu Verifikasi (terfilter).
    *   Laporan Gagal Panen yang Belum Ditinjau (terfilter).
*   **Komponen Visual & Tata Letak**:
    *   **Baris Grafik**: Menyejajarkan `PieChart` status pengajuan (kiri) dan `BarChart` distribusi tahunan (kanan) secara berdampingan dengan tinggi yang sama.
    *   **Baris Tabel (Bawah)**: `TableView` historis penyaluran pupuk diletakkan di bawah kedua grafik, membentang lebar penuh.
    *   **Fitur Ekspor Laporan PDF**: Menyediakan tombol "Unduh Laporan PDF" untuk mencetak laporan resmi UPTD BP4 Pakem yang disaring berdasarkan periode tanggal yang dipilih.

---

## 4. Desain Modul Laporan Gagal Panen

### A. Formulir Laporan Petani (`LaporGagalPanen.fxml` & `LaporGagalPanenController.java` - UC04)
*   Petani memilih lahan yang mengalami musibah dari daftar lahan milik mereka yang sudah diverifikasi (mengambil data dari pengajuan yang disetujui).
*   Petani mengunggah foto kondisi tanaman terkini menggunakan `FileChooser`.
*   Validasi input:
    *   Luas terdampak tidak boleh kosong dan tidak boleh melebihi luas lahan asli.
    *   Persentase kerusakan harus bernilai antara 1 s.d. 100.
    *   Unggahan foto wajib dilampirkan.
*   Setelah dikirim, status laporan diset menjadi `"Menunggu Peninjauan"`.

### B. Verifikasi Laporan BPP (`VerifikasiGagalPanen.fxml` & `VerifikasiGagalPanenController.java` - UC05)
*   Petugas BPP melihat daftar antrean laporan gagal panen petani.
*   Detail laporan menampilkan foto kerusakan secara jelas.
*   Petugas wajib menginput *Catatan Rekomendasi/Tindak Lanjut* sebelum menyetujui atau menolak laporan.
*   Status laporan diperbarui menjadi `"Terverifikasi"` atau `"Ditolak"`.

---

## 5. Pembaruan Alur Navigasi (`NavigationManager.java`)

Rute navigasi baru ditambahkan:
*   `navigateToDashboardPetani()`
*   `navigateToDashboardPetugas()`
*   `navigateToLaporGagalPanen()`
*   `navigateToVerifikasiGagalPanen()`

Method `navigateAfterLogin()` akan diarahkan ke dasbor masing-masing aktor setelah login berhasil dilakukan.
