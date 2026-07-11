# DATANI - Sistem Informasi Data Petani

Aplikasi desktop JavaFX (Java 17 + FXML + Maven, arsitektur MVC) untuk
pengelolaan pengajuan pupuk subsidi oleh Petani dan verifikasi oleh
Petugas BPP. Seluruh antarmuka menggunakan Bahasa Indonesia.

## Peran

- **Petani**: mendaftar akun, mengajukan pupuk subsidi, memantau status
  pengajuan, mengelola profil.
- **Petugas BPP**: memverifikasi (menyetujui/menolak) pengajuan, melihat
  riwayat hasil verifikasi, mengelola profil.

Aplikasi ini **tidak memiliki halaman Dashboard**.

## Menjalankan aplikasi

```bash
mvn clean javafx:run
```

## Login

Halaman Login menampilkan dua Tab:

- **Tab Petani** - login menggunakan **NIK** + Kata Sandi.
- **Tab Petugas BPP** - login menggunakan **Username** + Kata Sandi
  (BUKAN NIK, karena Petugas BPP tidak memiliki NIK yang tercatat di
  sistem ini).

## Akun contoh (data awal / dummy)

| Peran        | Login Menggunakan | Nilai              | Kata Sandi |
|--------------|--------------------|--------------------|------------|
| Petugas BPP  | Username           | petugas             | petugas123 |
| Petani       | NIK                | 3201012501900002    | petani123  |
| Petani       | NIK                | 3309022803880003    | petani123  |
| Petani       | NIK                | 3215017004950004    | petani123  |

Petani baru dapat mendaftar sendiri melalui tautan "Daftar" pada Tab
Petani di halaman Login.

## Struktur proyek

```
src/main/java/com/datani/
  Main.java
  model/            Role, User, Petani, Pengajuan, StatusPengajuan
  controller/        LoginController, RegisterController
  controller/common/  HeaderBarController (header bersama Petani & Petugas)
  controller/petani/  PetaniSidebarController, PengajuanPupukController,
                       StatusPengajuanController, ProfilPetaniController
  controller/petugas/ PetugasSidebarController, VerifikasiPengajuanController,
                       HasilVerifikasiController, ProfilPetugasController
  navigation/        NavigationManager
  service/           DataService (sumber data in-memory / dummy)
  session/           UserSession

src/main/resources/com/datani/
  fxml/login/    Login.fxml (TabPane: Petani / Petugas BPP), Register.fxml
  fxml/common/   HeaderBar.fxml
  fxml/petani/   PetaniSidebar.fxml, PengajuanPupuk.fxml,
                 StatusPengajuan.fxml, ProfilPetani.fxml
  fxml/petugas/  PetugasSidebar.fxml, VerifikasiPengajuan.fxml (master-detail),
                 HasilVerifikasi.fxml, ProfilPetugas.fxml
  css/styles.css
```

## Catatan implementasi

- `DataService` adalah sumber data in-memory yang menggantikan basis data
  sungguhan; gantilah isi kelas ini dengan implementasi JDBC/REST bila
  diperlukan persistensi nyata.
- Login Petani menggunakan NIK + Kata Sandi; Login Petugas BPP
  menggunakan Username + Kata Sandi. Kedua jenis akun disimpan pada
  entitas `User` yang sama, dibedakan lewat field `nik` (Petani) dan
  `username` (Petugas BPP).
- Halaman "Verifikasi Pengajuan" menggunakan layout master-detail
  (`SplitPane`): daftar pengajuan di kiri, detail lengkap + aksi
  Setujui/Tolak di kanan.
