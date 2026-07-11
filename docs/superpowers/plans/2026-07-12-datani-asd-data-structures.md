# Integrasi 5 Struktur Data ASD Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Mengimplementasikan kelima struktur data kustom (Queue, Stack, Doubly LinkedList, Binary Search Tree, dan Algoritma Quick Sort) ke dalam aplikasi DATANI untuk memenuhi persyaratan Tugas Besar Algoritma & Struktur Data (ASD) tanpa merusak fungsionalitas UI JavaFX yang ada.

**Architecture:** 
- Membuat package baru `com.datani.datastructure` untuk menampung kelas-kelas struktur data *from scratch* (menggunakan kelas `Node` buatan sendiri tanpa `java.util.*` collections).
- Mengintegrasikan setiap struktur data ke komponen yang tepat (misal: Stack di `NavigationManager`, BST di `DataService`, dsb) agar berfungsi nyata dalam aplikasi.

**Tech Stack:** Java SE 9, Object-Oriented Data Structures (No built-in Java Collections for the core logic).

## Global Constraints
- Tidak boleh menggunakan `java.util.LinkedList`, `java.util.Stack`, `java.util.Queue`, atau `Collections.sort` untuk implementasi internal logika struktur data. Harus murni berbasis *Node* dan manipulasi *pointer* array/referensi.
- Harus kompatibel dengan Java SE 9.

---

### Task 1: Buat Package & Kelas Struktur Data Dasar
**Files:**
- Create: `src/main/java/com/datani/datastructure/AntreanLaporan.java`
- Create: `src/main/java/com/datani/datastructure/TumpukanNavigasi.java`
- Create: `src/main/java/com/datani/datastructure/RiwayatPengajuanList.java`
- Create: `src/main/java/com/datani/datastructure/PohonPetani.java`
- Create: `src/main/java/com/datani/datastructure/PengurutanData.java`

- [ ] **Step 1: Tulis 5 kelas struktur data dari nol (from scratch)**
    Implementasikan kelas-kelas di atas dengan Inner Class node masing-masing:
    - `AntreanLaporan` -> inner class `NodeLaporan`.
    - `TumpukanNavigasi` -> inner class `NodeString`.
    - `RiwayatPengajuanList` -> inner class `NodeRiwayat` (prev, next).
    - `PohonPetani` -> inner class `NodePetani` (left, right).
    - `PengurutanData` -> algoritma Quick Sort manual untuk list (menggunakan indeks array).

---

### Task 2: Integrasi BST ke DataService
**Files:**
- Modify: `src/main/java/com/datani/service/DataService.java`

- [ ] **Step 1: Inisialisasi dan gunakan PohonPetani (BST) untuk pencarian**
    Ubah logika di dalam `DataService`. Setiap data dimuat (`loadAllData`), sisipkan ke `PohonPetani`. Ubah fungsi `getPetaniByNik` agar memanggil fungsi pencarian pada `PohonPetani` untuk meningkatkan kompleksitas menjadi O(log n).

---

### Task 3: Integrasi Stack ke NavigationManager
**Files:**
- Modify: `src/main/java/com/datani/navigation/NavigationManager.java`

- [ ] **Step 1: Simpan history navigasi dengan TumpukanNavigasi**
    Gunakan `TumpukanNavigasi`. Saat metode `navigateToX` dipanggil, push fxml yang sedang aktif ke dalam stack, lalu buat method `kembaliKeHalamanSebelumnya()`.

---

### Task 4: Integrasi Queue & Quick Sort ke VerifikasiLaporanController
**Files:**
- Modify: `src/main/java/com/datani/controller/petugas/VerifikasiLaporanController.java`

- [ ] **Step 1: Load data ke dalam Antrean dan Sortir Tabel**
    Saat halaman dimuat, masukkan data dari `DataService` yang berstatus menunggu ke dalam `AntreanLaporan` untuk divisualisasikan antrean terdepannya. Gunakan utilitas `PengurutanData` untuk mengurutkan daftar sebelum ditambahkan ke `TableView`.

---

### Task 5: Integrasi DLL ke ProfilPetaniController
**Files:**
- Modify: `src/main/java/com/datani/controller/petani/ProfilPetaniController.java`

- [ ] **Step 1: Load riwayat pengajuan ke Doubly Linked List**
    Saat profil petani dimuat, susun `RiwayatPengajuanList` dari data historis petani tersebut untuk memenuhi tugas.

---
