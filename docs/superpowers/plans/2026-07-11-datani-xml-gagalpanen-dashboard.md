# DATANI XML Persistence, Gagal Panen, & Dashboard Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Mengimplementasikan penyimpanan data berbasis XML (XStream), modul pelaporan gagal panen (UC04 & UC05), dan halaman dashboard (UC06 & UC07) untuk Petani dan Petugas BPP pada aplikasi DATANI di Java SE 9.

**Architecture:** Memanfaatkan `DataService` sebagai lapisan persistensi tunggal yang diserialisasikan ke file XML melalui library XStream. Navigasi dikelola secara terpusat oleh `NavigationManager`.

**Tech Stack:** Java SE 9, JavaFX, FXML, XStream 1.4.20.

## Global Constraints
*   Target rilis dan kompatibilitas: Java SE 9.
*   Pustaka GUI: JavaFX (built-in di JDK 9.0.4).
*   Bahasa antarmuka: Bahasa Indonesia yang baku dan sederhana.
*   Penyimpanan: File XML lokal di folder `data/` di root proyek.

---

### Task 1: Update Maven Configuration & Dependency
**Files:**
*   Modify: `pom.xml`

- [ ] **Step 1: Tambahkan dependensi XStream**
    Tambahkan dependensi berikut di dalam tag `<dependencies>` utama di `pom.xml` agar XStream diunduh saat kompilasi:
    ```xml
    <dependency>
        <groupId>com.thoughtworks.xstream</groupId>
        <artifactId>xstream</artifactId>
        <version>1.4.20</version>
    </dependency>
    ```
- [ ] **Step 2: Jalankan kompilasi untuk memverifikasi dependensi**
    Kompilasi proyek untuk memastikan XStream terunduh tanpa error.
    Perintah: `mvn clean compile`
    Expected: BUILD SUCCESS
- [ ] **Step 3: Commit perubahan**
    Commit perubahan `pom.xml` dengan pesan commit `feat: add xstream dependency`.

---

### Task 2: Implement Model `LaporanGagalPanen`
**Files:**
*   Create: `src/main/java/com/datani/model/LaporanGagalPanen.java`

- [ ] **Step 1: Buat kelas model `LaporanGagalPanen.java`**
    Tulis kode lengkap untuk model `LaporanGagalPanen` di path `src/main/java/com/datani/model/LaporanGagalPanen.java`:
    ```java
    package com.datani.model;

    import java.time.LocalDate;

    public class LaporanGagalPanen {
        private int id;
        private int petaniId;
        private String petaniNama;
        private String penyebab;
        private double luasTerdampak;
        private int persentaseKerusakan;
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

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public int getPetaniId() { return petaniId; }
        public void setPetaniId(int petaniId) { this.petaniId = petaniId; }
        public String getPetaniNama() { return petaniNama; }
        public void setPetaniNama(String petaniNama) { this.petaniNama = petaniNama; }
        public String getPenyebab() { return penyebab; }
        public void setPenyebab(String penyebab) { this.penyebab = penyebab; }
        public double getLuasTerdampak() { return luasTerdampak; }
        public void setLuasTerdampak(double luasTerdampak) { this.luasTerdampak = luasTerdampak; }
        public int getPersentaseKerusakan() { return persentaseKerusakan; }
        public void setPersentaseKerusakan(int persentaseKerusakan) { this.persentaseKerusakan = persentaseKerusakan; }
        public LocalDate getTanggalKejadian() { return tanggalKejadian; }
        public void setTanggalKejadian(LocalDate tanggalKejadian) { this.tanggalKejadian = tanggalKejadian; }
        public String getFotoBukti() { return fotoBukti; }
        public void setFotoBukti(String fotoBukti) { this.fotoBukti = fotoBukti; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getCatatanRekomendasi() { return catatanRekomendasi; }
        public void setCatatanRekomendasi(String catatanRekomendasi) { this.catatanRekomendasi = catatanRekomendasi; }
        public LocalDate getTanggalVerifikasi() { return tanggalVerifikasi; }
        public void setTanggalVerifikasi(LocalDate tanggalVerifikasi) { this.tanggalVerifikasi = tanggalVerifikasi; }
        public String getVerifikatorUsername() { return verifikatorUsername; }
        public void setVerifikatorUsername(String verifikatorUsername) { this.verifikatorUsername = verifikatorUsername; }
    }
    ```
- [ ] **Step 2: Kompilasi dan verifikasi**
    Jalankan: `mvn compile`
    Expected: BUILD SUCCESS
- [ ] **Step 3: Commit**
    Commit dengan pesan `feat: add LaporanGagalPanen model`.

---

### Task 3: Implement XML Persistence in `DataService`
**Files:**
*   Modify: `src/main/java/com/datani/service/DataService.java`

- [ ] **Step 1: Tulis logika load/save XML dengan XStream**
    Ganti bagian inisialisasi static dan tambahkan method untuk menyimpan dan memuat file XML di `DataService.java`:
    ```java
    // Tambahkan import berikut di bagian atas DataService.java
    import com.datani.model.LaporanGagalPanen;
    import com.thoughtworks.xstream.XStream;
    import com.thoughtworks.xstream.security.AnyTypePermission;
    import java.io.File;
    import java.io.PrintWriter;
    import java.nio.file.Files;
    import java.nio.file.Paths;

    // Tambahkan field di DataService
    private static final ObservableList<LaporanGagalPanen> GAGAL_PANEN_LIST = FXCollections.observableArrayList();
    private static final String DATA_DIR = "data";
    private static final XStream xstream = new XStream();

    static {
        xstream.addPermission(AnyTypePermission.ANY);
        // Load data dari file XML saat startup
        loadAllData();
    }

    private static void saveToXml(Object list, String filename) {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
            String xml = xstream.toXML(list);
            try (PrintWriter out = new PrintWriter(new File(DATA_DIR, filename))) {
                out.println(xml);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Object loadFromXml(String filename) {
        File file = new File(DATA_DIR, filename);
        if (!file.exists()) {
            return null;
        }
        try {
            String xml = new String(Files.readAllBytes(file.toPath()));
            return xstream.fromXML(xml);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static void loadAllData() {
        // Load Users
        List<User> loadedUsers = (List<User>) loadFromXml("users.xml");
        if (loadedUsers != null) {
            USERS.clear();
            USERS.addAll(loadedUsers);
            nextUserId = USERS.stream().mapToInt(User::getId).max().orElse(0) + 1;
        } else {
            // Seed default
            USERS.add(User.petugas(1, "petugas", "petugas123", "Petugas BPP", "petugas.bpp@bpp.go.id", "081200000001"));
            nextUserId = 2;
            saveToXml(new ArrayList<>(USERS), "users.xml");
        }

        // Load Petani
        List<Petani> loadedPetani = (List<Petani>) loadFromXml("petani.xml");
        if (loadedPetani != null) {
            PETANI_LIST.clear();
            PETANI_LIST.addAll(loadedPetani);
            nextPetaniId = PETANI_LIST.stream().mapToInt(Petani::getId).max().orElse(0) + 1;
        } else {
            registerPetaniInternal("3201012501900002", "petani123", "Budi Santoso", "3201011234560001", "Desa Sukamaju, Bogor", "081300000002", "Tani Makmur 1");
            registerPetaniInternal("3309022803880003", "petani123", "Wayan Sudarma", "3309021234560002", "Desa Tegal Rejo, Klaten", "081300000003", "Tani Sejahtera");
            registerPetaniInternal("3215017004950004", "petani123", "Siti Aminah", "3215011234560003", "Desa Cikampek, Karawang", "081300000004", "Tani Makmur 2");
            saveToXml(new ArrayList<>(PETANI_LIST), "petani.xml");
            saveToXml(new ArrayList<>(USERS), "users.xml");
        }

        // Load Pengajuan
        List<Pengajuan> loadedPengajuan = (List<Pengajuan>) loadFromXml("pengajuan.xml");
        if (loadedPengajuan != null) {
            PENGAJUAN_LIST.clear();
            PENGAJUAN_LIST.addAll(loadedPengajuan);
            nextPengajuanId = PENGAJUAN_LIST.stream().mapToInt(Pengajuan::getId).max().orElse(0) + 1;
        } else {
            Petani budi = getPetaniByNik("3201012501900002").get();
            Petani wayan = getPetaniByNik("3309022803880003").get();
            Petani aminah = getPetaniByNik("3215017004950004").get();
            nextPengajuanId = 1;
            PENGAJUAN_LIST.add(new Pengajuan(nextPengajuanId++, budi.getId(), 1.5, "Milik Sendiri", "Padi", "sample-photos/lahan_budi_1.jpg", StatusPengajuan.DISETUJUI, null, LocalDate.now().minusDays(10), LocalDate.now().minusDays(8)));
            PENGAJUAN_LIST.add(new Pengajuan(nextPengajuanId++, budi.getId(), 0.8, "Sewa", "Jagung", "sample-photos/lahan_budi_2.jpg", StatusPengajuan.MENUNGGU_VERIFIKASI, null, LocalDate.now().minusDays(3), null));
            PENGAJUAN_LIST.add(new Pengajuan(nextPengajuanId++, wayan.getId(), 2.2, "Milik Sendiri", "Tebu", "sample-photos/lahan_wayan_1.jpg", StatusPengajuan.MENUNGGU_VERIFIKASI, null, LocalDate.now().minusDays(7), null));
            PENGAJUAN_LIST.add(new Pengajuan(nextPengajuanId++, aminah.getId(), 1.0, "Milik Sendiri", "Kedelai", "sample-photos/lahan_aminah_1.jpg", StatusPengajuan.DITOLAK, "Foto bukti tidak jelas.", LocalDate.now().minusDays(1), LocalDate.now()));
            saveToXml(new ArrayList<>(PENGAJUAN_LIST), "pengajuan.xml");
        }

        // Load Gagal Panen
        List<LaporanGagalPanen> loadedGagal = (List<LaporanGagalPanen>) loadFromXml("gagal_panen.xml");
        if (loadedGagal != null) {
            GAGAL_PANEN_LIST.clear();
            GAGAL_PANEN_LIST.addAll(loadedGagal);
        } else {
            // Seed gagal panen dummy
            Petani budi = getPetaniByNik("3201012501900002").get();
            LaporanGagalPanen l = new LaporanGagalPanen(1, budi.getId(), budi.getNamaLengkap(), "Hama/Penyakit", 0.5, 70, LocalDate.now().minusDays(5), "sample-photos/damage_budi_1.jpg");
            l.setStatus("Terverifikasi");
            l.setCatatanRekomendasi("Disetujui untuk alokasi pupuk darurat.");
            l.setTanggalVerifikasi(LocalDate.now().minusDays(4));
            l.setVerifikatorUsername("petugas");
            GAGAL_PANEN_LIST.add(l);
            saveToXml(new ArrayList<>(GAGAL_PANEN_LIST), "gagal_panen.xml");
        }
    }
    ```
- [ ] **Step 2: Tambahkan API CRUD LaporanGagalPanen**
    Tulis method publik berikut di `DataService.java` untuk melayani operasi penambahan dan pembacaan:
    ```java
    public static ObservableList<LaporanGagalPanen> getAllLaporanGagalPanen() {
        return GAGAL_PANEN_LIST;
    }

    public static ObservableList<LaporanGagalPanen> getLaporanGagalPanenByPetani(int petaniId) {
        ObservableList<LaporanGagalPanen> result = FXCollections.observableArrayList();
        for (LaporanGagalPanen l : GAGAL_PANEN_LIST) {
            if (l.getPetaniId() == petaniId) {
                result.add(l);
            }
        }
        return result;
    }

    public static void addLaporanGagalPanen(LaporanGagalPanen laporan) {
        GAGAL_PANEN_LIST.add(laporan);
        saveToXml(new ArrayList<>(GAGAL_PANEN_LIST), "gagal_panen.xml");
    }

    public static void updateLaporanGagalPanen(LaporanGagalPanen laporan) {
        for (int i = 0; i < GAGAL_PANEN_LIST.size(); i++) {
            if (GAGAL_PANEN_LIST.get(i).getId() == laporan.getId()) {
                GAGAL_PANEN_LIST.set(i, laporan);
                break;
            }
        }
        saveToXml(new ArrayList<>(GAGAL_PANEN_LIST), "gagal_panen.xml");
    }

    public static int getNextGagalPanenId() {
        return GAGAL_PANEN_LIST.stream().mapToInt(LaporanGagalPanen::getId).max().orElse(0) + 1;
    }
    ```
- [ ] **Step 3: Modifikasi method edit/save data yang sudah ada agar melakukan auto-save ke XML**
    Pastikan method registrasi petani (`registerPetani`), pengisian pengajuan (`addPengajuan`), dan update status pengajuan memanggil `saveToXml` agar tersimpan permanen di file XML.
- [ ] **Step 4: Kompilasi dan verifikasi**
    Jalankan: `mvn clean compile`
    Expected: BUILD SUCCESS
- [ ] **Step 5: Commit**
    Commit dengan pesan `feat: implement XML persistence and Gagal Panen data queries`.

---

### Task 4: Implement UC04 - Lapor Gagal Panen (Petani)
**Files:**
*   Create: `src/main/resources/com/datani/fxml/petani/LaporGagalPanen.fxml`
*   Create: `src/main/java/com/datani/controller/petani/LaporGagalPanenController.java`

- [ ] **Step 1: Buat FXML layout `LaporGagalPanen.fxml`**
    Tulis kode lengkap untuk UI form laporan di `src/main/resources/com/datani/fxml/petani/LaporGagalPanen.fxml`:
    ```xml
    <?xml version="1.0" encoding="UTF-8"?>

    <?import javafx.geometry.Insets?>
    <?import javafx.scene.control.Button?>
    <?import javafx.scene.control.ComboBox?>
    <?import javafx.scene.control.DatePicker?>
    <?import javafx.scene.control.Label?>
    <?import javafx.scene.control.TextField?>
    <?import javafx.scene.image.ImageView?>
    <?import javafx.scene.layout.BorderPane?>
    <?import javafx.scene.layout.GridPane?>
    <?import javafx.scene.layout.HBox?>
    <?import javafx.scene.layout.VBox?>

    <BorderPane xmlns="http://javafx.com/javafx"
                xmlns:fx="http://javafx.com/fxml"
                fx:controller="com.datani.controller.petani.LaporGagalPanenController"
                styleClass="app-root">
        <left>
            <fx:include source="PetaniSidebar.fxml"/>
        </left>
        <top>
            <fx:include source="../common/HeaderBar.fxml"/>
        </top>
        <center>
            <VBox spacing="16" styleClass="content-area">
                <padding>
                    <Insets top="28" right="32" bottom="28" left="32"/>
                </padding>

                <Label text="Lapor Gagal Panen" styleClass="page-title"/>
                <Label text="Laporkan kerusakan pada lahan Anda untuk alokasi bantuan atau kuota musim berikutnya" styleClass="page-subtitle"/>

                <GridPane hgap="16" vgap="12" styleClass="form-grid">
                    <columnConstraints>
                        <javafx.scene.layout.ColumnConstraints minWidth="150" prefWidth="180"/>
                        <javafx.scene.layout.ColumnConstraints hgrow="ALWAYS"/>
                    </columnConstraints>

                    <Label text="Pilih Lahan Terdaftar:" GridPane.rowIndex="0" GridPane.columnIndex="0" styleClass="form-label"/>
                    <ComboBox fx:id="lahanCombo" promptText="Pilih lahan terdampak" maxWidth="Infinity" GridPane.rowIndex="0" GridPane.columnIndex="1"/>

                    <Label text="Penyebab Gagal Panen:" GridPane.rowIndex="1" GridPane.columnIndex="0" styleClass="form-label"/>
                    <ComboBox fx:id="penyebabCombo" promptText="Pilih penyebab" maxWidth="Infinity" GridPane.rowIndex="1" GridPane.columnIndex="1"/>

                    <Label text="Luas Terdampak (Ha):" GridPane.rowIndex="2" GridPane.columnIndex="0" styleClass="form-label"/>
                    <TextField fx:id="luasField" promptText="Masukkan luas yang terdampak" GridPane.rowIndex="2" GridPane.columnIndex="1"/>

                    <Label text="Persentase Kerusakan (%):" GridPane.rowIndex="3" GridPane.columnIndex="0" styleClass="form-label"/>
                    <TextField fx:id="kerusakanField" promptText="Masukkan perkiraan % kerusakan (1 - 100)" GridPane.rowIndex="3" GridPane.columnIndex="1"/>

                    <Label text="Tanggal Kejadian:" GridPane.rowIndex="4" GridPane.columnIndex="0" styleClass="form-label"/>
                    <DatePicker fx:id="tanggalPicker" maxWidth="Infinity" GridPane.rowIndex="4" GridPane.columnIndex="1"/>

                    <Label text="Foto Bukti Kerusakan:" GridPane.rowIndex="5" GridPane.columnIndex="0" styleClass="form-label"/>
                    <HBox spacing="12" alignment="CENTER_LEFT" GridPane.rowIndex="5" GridPane.columnIndex="1">
                        <Button text="Pilih Foto" onAction="#handleUploadFoto" styleClass="secondary-button"/>
                        <Label fx:id="filePathLabel" text="Belum ada berkas dipilih" styleClass="file-path-label"/>
                    </HBox>

                    <Label text="Preview Foto:" GridPane.rowIndex="6" GridPane.columnIndex="0" styleClass="form-label"/>
                    <ImageView fx:id="previewImage" fitHeight="150" fitWidth="200" preserveRatio="true" visible="false" GridPane.rowIndex="6" GridPane.columnIndex="1"/>
                </GridPane>

                <HBox spacing="12" alignment="CENTER_LEFT">
                    <Button text="Kirim Laporan" onAction="#handleKirimLaporan" styleClass="primary-button"/>
                </HBox>
            </VBox>
        </center>
    </BorderPane>
    ```
- [ ] **Step 2: Buat Controller `LaporGagalPanenController.java`**
    Tulis kode logis formulir pelaporan di `src/main/java/com/datani/controller/petani/LaporGagalPanenController.java`:
    ```java
    package com.datani.controller.petani;

    import com.datani.model.LaporanGagalPanen;
    import com.datani.model.Pengajuan;
    import com.datani.model.Petani;
    import com.datani.model.StatusPengajuan;
    import com.datani.service.DataService;
    import com.datani.session.UserSession;
    import javafx.collections.FXCollections;
    import javafx.collections.ObservableList;
    import javafx.fxml.FXML;
    import javafx.scene.control.*;
    import javafx.scene.image.Image;
    import javafx.scene.image.ImageView;
    import javafx.stage.FileChooser;
    import java.io.File;
    import java.time.LocalDate;

    public class LaporGagalPanenController {

        @FXML private ComboBox<String> lahanCombo;
        @FXML private ComboBox<String> penyebabCombo;
        @FXML private TextField luasField;
        @FXML private TextField kerusakanField;
        @FXML private DatePicker tanggalPicker;
        @FXML private Label filePathLabel;
        @FXML private ImageView previewImage;

        private File fileFotoTerpilih;

        @FXML
        private void initialize() {
            penyebabCombo.setItems(FXCollections.observableArrayList("Hama/Penyakit", "Kekeringan", "Banjir", "Bencana Alam"));
            
            // Ambil data lahan yang terdaftar & disetujui milik petani saat ini
            Petani petani = UserSession.getCurrentPetani();
            if (petani != null) {
                ObservableList<String> lahanItems = FXCollections.observableArrayList();
                for (Pengajuan p : DataService.getPengajuanByPetaniId(petani.getId())) {
                    if (p.getStatus() == StatusPengajuan.DISETUJUI) {
                        lahanItems.add("Lahan ID #" + p.getId() + " - " + p.getJenisTanaman() + " (" + p.getLuasLahan() + " Ha)");
                    }
                }
                lahanCombo.setItems(lahanItems);
            }
        }

        @FXML
        private void handleUploadFoto() {
            FileChooser chooser = new FileChooser();
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Gambar Lahan", "*.jpg", "*.jpeg", "*.png"));
            File file = chooser.showOpenDialog(filePathLabel.getScene().getWindow());
            if (file != null) {
                fileFotoTerpilih = file;
                filePathLabel.setText(file.getName());
                previewImage.setImage(new Image(file.toURI().toString()));
                previewImage.setVisible(true);
            }
        }

        @FXML
        private void handleKirimLaporan() {
            String lahan = lahanCombo.getValue();
            String penyebab = penyebabCombo.getValue();
            String luasStr = luasField.getText();
            String rusakStr = kerusakanField.getText();
            LocalDate tgl = tanggalPicker.getValue();

            if (lahan == null || penyebab == null || luasStr == null || rusakStr == null || tgl == null || fileFotoTerpilih == null) {
                showError("Semua form wajib diisi dan foto wajib dilampirkan.");
                return;
            }

            try {
                double luas = Double.parseDouble(luasStr);
                int rusak = Integer.parseInt(rusakStr);

                if (luas <= 0 || rusak <= 0 || rusak > 100) {
                    showError("Masukkan nilai desimal luas dan persentase kerusakan (1-100) secara valid.");
                    return;
                }

                // Validasi agar tidak melebihi luas lahan asli
                double luasMaks = Double.parseDouble(lahan.substring(lahan.indexOf("(") + 1, lahan.indexOf(" Ha)")));
                if (luas > luasMaks) {
                    showError("Luas terdampak (" + luas + " Ha) tidak boleh melebihi luas lahan asli (" + luasMaks + " Ha).");
                    return;
                }

                Petani petani = UserSession.getCurrentPetani();
                int id = DataService.getNextGagalPanenId();
                LaporanGagalPanen laporan = new LaporanGagalPanen(id, petani.getId(), petani.getNamaLengkap(), penyebab, luas, rusak, tgl, fileFotoTerpilih.getAbsolutePath());
                DataService.addLaporanGagalPanen(laporan);

                Alert info = new Alert(Alert.AlertType.INFORMATION);
                info.setTitle("Laporan Terkirim");
                info.setHeaderText(null);
                info.setContentText("Laporan gagal panen berhasil dikirim dengan status 'Menunggu Peninjauan'.");
                info.showAndWait();

                // Reset form
                lahanCombo.setValue(null);
                penyebabCombo.setValue(null);
                luasField.clear();
                kerusakanField.clear();
                tanggalPicker.setValue(null);
                filePathLabel.setText("Belum ada berkas dipilih");
                previewImage.setVisible(false);

            } catch (NumberFormatException e) {
                showError("Format angka luas atau kerusakan tidak valid.");
            }
        }

        private void showError(String msg) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Validasi Error");
            alert.setHeaderText(null);
            alert.setContentText(msg);
            alert.showAndWait();
        }
    }
    ```
- [ ] **Step 3: Kompilasi dan verifikasi**
    Jalankan: `mvn compile`
    Expected: BUILD SUCCESS
- [ ] **Step 4: Commit**
    Commit dengan pesan `feat: add UC04 Lapor Gagal Panen form and controller`.

---

### Task 5: Implement UC05 - Verifikasi Laporan Gagal Panen (BPP)
**Files:**
*   Create: `src/main/resources/com/datani/fxml/petugas/VerifikasiLaporan.fxml`
*   Create: `src/main/java/com/datani/controller/petugas/VerifikasiLaporanController.java`

- [ ] **Step 1: Buat FXML layout `VerifikasiLaporan.fxml`**
    Tulis kode UI verifikasi laporan di `src/main/resources/com/datani/fxml/petugas/VerifikasiLaporan.fxml`:
    ```xml
    <?xml version="1.0" encoding="UTF-8"?>

    <?import javafx.geometry.Insets?>
    <?import javafx.scene.control.Button?>
    <?import javafx.scene.control.Label?>
    <?import javafx.scene.control.SplitPane?>
    <?import javafx.scene.control.TableColumn?>
    <?import javafx.scene.control.TableView?>
    <?import javafx.scene.control.TextArea?>
    <?import javafx.scene.image.ImageView?>
    <?import javafx.scene.layout.BorderPane?>
    <?import javafx.scene.layout.GridPane?>
    <?import javafx.scene.layout.HBox?>
    <?import javafx.scene.layout.VBox?>

    <BorderPane xmlns="http://javafx.com/javafx"
                xmlns:fx="http://javafx.com/fxml"
                fx:controller="com.datani.controller.petugas.VerifikasiLaporanController"
                styleClass="app-root">
        <left>
            <fx:include source="PetugasSidebar.fxml"/>
        </left>
        <top>
            <fx:include source="../common/HeaderBar.fxml"/>
        </top>
        <center>
            <SplitPane dividerPositions="0.4">
                <items>
                    <!-- Kiri: Antrean Laporan -->
                    <VBox spacing="12">
                        <padding>
                            <Insets top="16" right="16" bottom="16" left="16"/>
                        </padding>
                        <Label text="Antrean Gagal Panen" styleClass="section-title"/>
                        <TableView fx:id="antreanTable" VBox.vgrow="ALWAYS" styleClass="data-table">
                            <columns>
                                <TableColumn fx:id="idColumn" text="ID" prefWidth="50"/>
                                <TableColumn fx:id="namaColumn" text="Petani" prefWidth="120"/>
                                <TableColumn fx:id="penyebabColumn" text="Penyebab" prefWidth="100"/>
                            </columns>
                        </TableView>
                    </VBox>

                    <!-- Kanan: Detail & Verifikasi -->
                    <VBox spacing="16" fx:id="detailContainer" visible="false">
                        <padding>
                            <Insets top="16" right="16" bottom="16" left="16"/>
                        </padding>
                        <Label text="Detail & Keputusan Verifikasi" styleClass="section-title"/>

                        <GridPane hgap="12" vgap="8">
                            <Label text="Nama Petani:" GridPane.rowIndex="0" GridPane.columnIndex="0" styleClass="detail-label-bold"/>
                            <Label fx:id="namaPetaniLabel" GridPane.rowIndex="0" GridPane.columnIndex="1"/>

                            <Label text="Penyebab:" GridPane.rowIndex="1" GridPane.columnIndex="0" styleClass="detail-label-bold"/>
                            <Label fx:id="penyebabLabel" GridPane.rowIndex="1" GridPane.columnIndex="1"/>

                            <Label text="Luas Lahan Terdampak:" GridPane.rowIndex="2" GridPane.columnIndex="0" styleClass="detail-label-bold"/>
                            <Label fx:id="luasLabel" GridPane.rowIndex="2" GridPane.columnIndex="1"/>

                            <Label text="Kerusakan (%):" GridPane.rowIndex="3" GridPane.columnIndex="0" styleClass="detail-label-bold"/>
                            <Label fx:id="kerusakanLabel" GridPane.rowIndex="3" GridPane.columnIndex="1"/>

                            <Label text="Tanggal Kejadian:" GridPane.rowIndex="4" GridPane.columnIndex="0" styleClass="detail-label-bold"/>
                            <Label fx:id="tanggalLabel" GridPane.rowIndex="4" GridPane.columnIndex="1"/>
                        </GridPane>

                        <Label text="Foto Bukti Kerusakan:" styleClass="detail-label-bold"/>
                        <ImageView fx:id="fotoImageView" fitHeight="150" fitWidth="250" preserveRatio="true"/>

                        <Label text="Catatan Rekomendasi / Tindak Lanjut (Wajib):" styleClass="detail-label-bold"/>
                        <TextArea fx:id="rekomendasiArea" prefRowCount="4" promptText="Masukkan rekomendasi bantuan atau penyesuaian kuota"/>

                        <HBox spacing="12">
                            <Button text="Setujui Laporan" onAction="#handleSetujui" styleClass="primary-button"/>
                            <Button text="Tolak Laporan" onAction="#handleTolak" styleClass="danger-button"/>
                        </HBox>
                    </VBox>
                </items>
            </SplitPane>
        </center>
    </BorderPane>
    ```
- [ ] **Step 2: Buat Controller `VerifikasiLaporanController.java`**
    Tulis logika verifikasi laporan di `src/main/java/com/datani/controller/petugas/VerifikasiLaporanController.java`:
    ```java
    package com.datani.controller.petugas;

    import com.datani.model.LaporanGagalPanen;
    import com.datani.service.DataService;
    import com.datani.session.UserSession;
    import javafx.collections.FXCollections;
    import javafx.collections.ObservableList;
    import javafx.fxml.FXML;
    import javafx.scene.control.*;
    import javafx.scene.image.Image;
    import javafx.scene.image.ImageView;
    import javafx.scene.layout.VBox;
    import java.io.File;
    import java.time.LocalDate;
    import java.util.Optional;

    public class VerifikasiLaporanController {

        @FXML private TableView<LaporanGagalPanen> antreanTable;
        @FXML private TableColumn<LaporanGagalPanen, Integer> idColumn;
        @FXML private TableColumn<LaporanGagalPanen, String> namaColumn;
        @FXML private TableColumn<LaporanGagalPanen, String> penyebabColumn;

        @FXML private VBox detailContainer;
        @FXML private Label namaPetaniLabel;
        @FXML private Label penyebabLabel;
        @FXML private Label luasLabel;
        @FXML private Label kerusakanLabel;
        @FXML private Label tanggalLabel;
        @FXML private ImageView fotoImageView;
        @FXML private TextArea rekomendasiArea;

        private LaporanGagalPanen laporanTerpilih;

        @FXML
        private void initialize() {
            idColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId()).asObject());
            namaColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getPetaniNama()));
            penyebabColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getPenyebab()));

            refreshTable();

            antreanTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    tampilkanDetail(newVal);
                } else {
                    detailContainer.setVisible(false);
                }
            });
        }

        private void refreshTable() {
            ObservableList<LaporanGagalPanen> antrean = FXCollections.observableArrayList();
            for (LaporanGagalPanen l : DataService.getAllLaporanGagalPanen()) {
                if (l.getStatus().equals("Menunggu Peninjauan")) {
                    antrean.add(l);
                }
            }
            antreanTable.setItems(antrean);
            detailContainer.setVisible(false);
            laporanTerpilih = null;
        }

        private void tampilkanDetail(LaporanGagalPanen laporan) {
            laporanTerpilih = laporan;
            namaPetaniLabel.setText(laporan.getPetaniNama());
            penyebabLabel.setText(laporan.getPenyebab());
            luasLabel.setText(laporan.getLuasTerdampak() + " Ha");
            kerusakanLabel.setText(laporan.getPersentaseKerusakan() + " %");
            tanggalLabel.setText(laporan.getTanggalKejadian().toString());

            String path = laporan.getFotoBukti();
            if (path != null && new File(path).exists()) {
                fotoImageView.setImage(new Image(new File(path).toURI().toString()));
            } else {
                fotoImageView.setImage(null);
            }

            rekomendasiArea.clear();
            detailContainer.setVisible(true);
        }

        @FXML
        private void handleSetujui() {
            prosesVerifikasi("Terverifikasi");
        }

        @FXML
        private void handleTolak() {
            prosesVerifikasi("Ditolak");
        }

        private void prosesVerifikasi(String statusDestinasi) {
            if (laporanTerpilih == null) return;
            String rekomendasi = rekomendasiArea.getText();
            if (rekomendasi == null || rekomendasi.trim().isEmpty()) {
                Alert err = new Alert(Alert.AlertType.ERROR);
                err.setTitle("Validasi Gagal");
                err.setHeaderText(null);
                err.setContentText("Catatan rekomendasi/tindak lanjut wajib diisi sebelum menyimpan keputusan.");
                err.showAndWait();
                return;
            }

            laporanTerpilih.setStatus(statusDestinasi);
            laporanTerpilih.setCatatanRekomendasi(rekomendasi);
            laporanTerpilih.setTanggalVerifikasi(LocalDate.now());
            laporanTerpilih.setVerifikatorUsername(UserSession.getCurrentUser().getUsername());

            DataService.updateLaporanGagalPanen(laporanTerpilih);

            Alert info = new Alert(Alert.AlertType.INFORMATION);
            info.setTitle("Verifikasi Berhasil");
            info.setHeaderText(null);
            info.setContentText("Laporan gagal panen berhasil diverifikasi dengan status: " + statusDestinasi);
            info.showAndWait();

            refreshTable();
        }
    }
    ```
- [ ] **Step 3: Kompilasi dan verifikasi**
    Jalankan: `mvn compile`
    Expected: BUILD SUCCESS
- [ ] **Step 4: Commit**
    Commit dengan pesan `feat: add UC05 Verifikasi Laporan Gagal Panen for BPP`.

---

### Task 6: Implement UC06 - Dashboard Petani
**Files:**
*   Create: `src/main/resources/com/datani/fxml/petani/DashboardPetani.fxml`
*   Create: `src/main/java/com/datani/controller/petani/DashboardPetaniController.java`

- [ ] **Step 1: Buat FXML layout `DashboardPetani.fxml`**
    Tulis layout lengkap di `src/main/resources/com/datani/fxml/petani/DashboardPetani.fxml`:
    ```xml
    <?xml version="1.0" encoding="UTF-8"?>

    <?import javafx.geometry.Insets?>
    <?import javafx.scene.chart.PieChart?>
    <?import javafx.scene.control.Label?>
    <?import javafx.scene.control.TableColumn?>
    <?import javafx.scene.control.TableView?>
    <?import javafx.scene.layout.BorderPane?>
    <?import javafx.scene.layout.HBox?>
    <?import javafx.scene.layout.VBox?>

    <BorderPane xmlns="http://javafx.com/javafx"
                xmlns:fx="http://javafx.com/fxml"
                fx:controller="com.datani.controller.petani.DashboardPetaniController"
                styleClass="app-root">
        <left>
            <fx:include source="PetaniSidebar.fxml"/>
        </left>
        <top>
            <fx:include source="../common/HeaderBar.fxml"/>
        </top>
        <center>
            <VBox spacing="20" styleClass="content-area">
                <padding>
                    <Insets top="28" right="32" bottom="28" left="32"/>
                </padding>

                <Label text="Dasbor Beranda Petani" styleClass="page-title"/>
                <Label fx:id="welcomeLabel" text="Selamat datang kembali" styleClass="page-subtitle"/>

                <!-- Row 1: Summary Cards -->
                <HBox spacing="16">
                    <VBox spacing="6" styleClass="stat-card" HBox.hgrow="ALWAYS">
                        <Label text="Total Luas Lahan Terdaftar" styleClass="stat-card-title"/>
                        <Label fx:id="totalLahanLabel" text="0.0 Ha" styleClass="stat-card-value"/>
                    </VBox>
                    <VBox spacing="6" styleClass="stat-card" HBox.hgrow="ALWAYS">
                        <Label text="Pengajuan Pupuk Disetujui" styleClass="stat-card-title"/>
                        <Label fx:id="pengajuanSetujuLabel" text="0" styleClass="stat-card-value"/>
                    </VBox>
                    <VBox spacing="6" styleClass="stat-card" HBox.hgrow="ALWAYS">
                        <Label text="Laporan Gagal Panen Aktif" styleClass="stat-card-title"/>
                        <Label fx:id="gagalPanenLabel" text="0" styleClass="stat-card-value"/>
                    </VBox>
                </HBox>

                <!-- Row 2: Charts & Historis -->
                <HBox spacing="20" VBox.vgrow="ALWAYS">
                    <!-- Left: Pie Chart -->
                    <VBox spacing="12" HBox.hgrow="ALWAYS" styleClass="dashboard-panel">
                        <Label text="Persentase Status Pengajuan" styleClass="section-title"/>
                        <PieChart fx:id="statusPieChart" labelsVisible="true" legendSide="BOTTOM" VBox.vgrow="ALWAYS"/>
                    </VBox>

                    <!-- Right: Alokasi Pupuk 3 Tahun Terakhir -->
                    <VBox spacing="12" HBox.hgrow="ALWAYS" styleClass="dashboard-panel">
                        <Label text="Alokasi Pupuk 3 Tahun Terakhir" styleClass="section-title"/>
                        <TableView fx:id="historisTable" VBox.vgrow="ALWAYS">
                            <columns>
                                <TableColumn fx:id="tahunColumn" text="Tahun" prefWidth="80"/>
                                <TableColumn fx:id="kuotaColumn" text="Alokasi Kuota (Kg)" prefWidth="150"/>
                                <TableColumn fx:id="statusPenyaluranColumn" text="Status Penyaluran" prefWidth="130"/>
                            </columns>
                        </TableView>
                    </VBox>
                </HBox>

                <!-- Row 3: Riwayat Pengajuan Terbaru -->
                <VBox spacing="12" styleClass="dashboard-panel">
                    <Label text="Riwayat Pengajuan Terbaru" styleClass="section-title"/>
                    <TableView fx:id="recentTable" prefHeight="150">
                        <columns>
                            <TableColumn fx:id="idColumn" text="ID" prefWidth="50"/>
                            <TableColumn fx:id="luasLahanColumn" text="Luas Lahan" prefWidth="100"/>
                            <TableColumn fx:id="jenisTanamanColumn" text="Tanaman" prefWidth="120"/>
                            <TableColumn fx:id="statusColumn" text="Status" prefWidth="150"/>
                        </columns>
                    </TableView>
                </VBox>
            </VBox>
        </center>
    </BorderPane>
    ```
- [ ] **Step 2: Buat Controller `DashboardPetaniController.java`**
    Tulis logika dashboard petani di `src/main/java/com/datani/controller/petani/DashboardPetaniController.java`:
    ```java
    package com.datani.controller.petani;

    import com.datani.model.LaporanGagalPanen;
    import com.datani.model.Pengajuan;
    import com.datani.model.Petani;
    import com.datani.model.StatusPengajuan;
    import com.datani.service.DataService;
    import com.datani.session.UserSession;
    import javafx.beans.property.SimpleIntegerProperty;
    import javafx.beans.property.SimpleStringProperty;
    import javafx.collections.FXCollections;
    import javafx.collections.ObservableList;
    import javafx.fxml.FXML;
    import javafx.scene.chart.PieChart;
    import javafx.scene.control.Label;
    import javafx.scene.control.TableColumn;
    import javafx.scene.control.TableView;
    import java.time.LocalDate;

    public class DashboardPetaniController {

        @FXML private Label welcomeLabel;
        @FXML private Label totalLahanLabel;
        @FXML private Label pengajuanSetujuLabel;
        @FXML private Label gagalPanenLabel;

        @FXML private PieChart statusPieChart;

        @FXML private TableView<HistorisPupuk> historisTable;
        @FXML private TableColumn<HistorisPupuk, Integer> tahunColumn;
        @FXML private TableColumn<HistorisPupuk, String> kuotaColumn;
        @FXML private TableColumn<HistorisPupuk, String> statusPenyaluranColumn;

        @FXML private TableView<Pengajuan> recentTable;
        @FXML private TableColumn<Pengajuan, Integer> idColumn;
        @FXML private TableColumn<Pengajuan, String> luasLahanColumn;
        @FXML private TableColumn<Pengajuan, String> jenisTanamanColumn;
        @FXML private TableColumn<Pengajuan, String> statusColumn;

        @FXML
        private void initialize() {
            Petani petani = UserSession.getCurrentPetani();
            if (petani == null) return;

            welcomeLabel.setText("Selamat datang kembali, " + petani.getNamaLengkap());

            // 1. Hitung Statistik & Pengajuan
            double totalLuas = 0.0;
            int setujuCount = 0;
            int tungguCount = 0;
            int tolakCount = 0;

            ObservableList<Pengajuan> allPengajuan = DataService.getPengajuanByPetaniId(petani.getId());
            for (Pengajuan p : allPengajuan) {
                if (p.getStatus() == StatusPengajuan.DISETUJUI) {
                    totalLuas += p.getLuasLahan();
                    setujuCount++;
                } else if (p.getStatus() == StatusPengajuan.MENUNGGU_VERIFIKASI) {
                    tungguCount++;
                } else if (p.getStatus() == StatusPengajuan.DITOLAK) {
                    tolakCount++;
                }
            }

            totalLahanLabel.setText(String.format("%.1f Ha", totalLuas));
            pengajuanSetujuLabel.setText(String.valueOf(setujuCount));

            // Hitung gagal panen aktif (berstatus Menunggu Peninjauan / Terverifikasi)
            int gagalCount = 0;
            for (LaporanGagalPanen l : DataService.getLaporanGagalPanenByPetani(petani.getId())) {
                if (!l.getStatus().equals("Ditolak")) {
                    gagalCount++;
                }
            }
            gagalPanenLabel.setText(String.valueOf(gagalCount));

            // 2. Load Pie Chart
            ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList(
                    new PieChart.Data("Disetujui (" + setujuCount + ")", setujuCount),
                    new PieChart.Data("Menunggu (" + tungguCount + ")", tungguCount),
                    new PieChart.Data("Ditolak (" + tolakCount + ")", tolakCount)
            );
            statusPieChart.setData(pieData);

            // 3. Load Historis Table (3 Tahun Terakhir)
            tahunColumn.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().tahun).asObject());
            kuotaColumn.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().kuota));
            statusPenyaluranColumn.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().status));

            ObservableList<HistorisPupuk> histList = FXCollections.observableArrayList(
                    new HistorisPupuk(2024, "350 Kg", "Telah Diambil"),
                    new HistorisPupuk(2025, "400 Kg", "Telah Diambil"),
                    new HistorisPupuk(2026, String.format("%.0f Kg", totalLuas * 150), setujuCount > 0 ? "Siap Diambil" : "Belum Tersedia")
            );
            historisTable.setItems(histList);

            // 4. Load Recent Table
            idColumn.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getId()).asObject());
            luasLahanColumn.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getLuasLahan() + " Ha"));
            jenisTanamanColumn.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getJenisTanaman()));
            statusColumn.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStatus().getLabel()));

            recentTable.setItems(allPengajuan);
        }

        // Inner class helper untuk data historis
        public static class HistorisPupuk {
            int tahun;
            String kuota;
            String status;

            public HistorisPupuk(int tahun, String kuota, String status) {
                this.tahun = tahun;
                this.kuota = kuota;
                this.status = status;
            }
            public int getTahun() { return tahun; }
            public String getKuota() { return kuota; }
            public String getStatus() { return status; }
        }
    }
    ```
- [ ] **Step 3: Kompilasi dan verifikasi**
    Jalankan: `mvn compile`
    Expected: BUILD SUCCESS
- [ ] **Step 4: Commit**
    Commit dengan pesan `feat: add UC06 Dashboard Petani layout and controller`.

---

### Task 7: Implement UC07 - Dashboard Petugas BPP
**Files:**
*   Create: `src/main/resources/com/datani/fxml/petugas/DashboardPetugas.fxml`
*   Create: `src/main/java/com/datani/controller/petugas/DashboardPetugasController.java`

- [ ] **Step 1: Buat FXML layout `DashboardPetugas.fxml`**
    Tulis layout lengkap di `src/main/resources/com/datani/fxml/petugas/DashboardPetugas.fxml`:
    ```xml
    <?xml version="1.0" encoding="UTF-8"?>

    <?import javafx.geometry.Insets?>
    <?import javafx.scene.chart.BarChart?>
    <?import javafx.scene.chart.CategoryAxis?>
    <?import javafx.scene.chart.NumberAxis?>
    <?import javafx.scene.chart.PieChart?>
    <?import javafx.scene.control.Label?>
    <?import javafx.scene.layout.BorderPane?>
    <?import javafx.scene.layout.HBox?>
    <?import javafx.scene.layout.VBox?>

    <BorderPane xmlns="http://javafx.com/javafx"
                xmlns:fx="http://javafx.com/fxml"
                fx:controller="com.datani.controller.petugas.DashboardPetugasController"
                styleClass="app-root">
        <left>
            <fx:include source="PetugasSidebar.fxml"/>
        </left>
        <top>
            <fx:include source="../common/HeaderBar.fxml"/>
        </top>
        <center>
            <VBox spacing="20" styleClass="content-area">
                <padding>
                    <Insets top="28" right="32" bottom="28" left="32"/>
                </padding>

                <Label text="Dasbor Monitoring Petugas BPP" styleClass="page-title"/>
                <Label text="Ringkasan penyaluran pupuk subsidi dan peninjauan wilayah UPTD BP4 Pakem" styleClass="page-subtitle"/>

                <!-- Row 1: Summary Cards -->
                <HBox spacing="16">
                    <VBox spacing="6" styleClass="stat-card" HBox.hgrow="ALWAYS">
                        <Label text="Total Petani Aktif" styleClass="stat-card-title"/>
                        <Label fx:id="totalPetaniLabel" text="0" styleClass="stat-card-value"/>
                    </VBox>
                    <VBox spacing="6" styleClass="stat-card" HBox.hgrow="ALWAYS">
                        <Label text="Pengajuan Pupuk Menunggu" styleClass="stat-card-title"/>
                        <Label fx:id="pengajuanMenungguLabel" text="0" styleClass="stat-card-value"/>
                    </VBox>
                    <VBox spacing="6" styleClass="stat-card" HBox.hgrow="ALWAYS">
                        <Label text="Laporan Gagal Panen Tertunda" styleClass="stat-card-title"/>
                        <Label fx:id="gagalPanenMenungguLabel" text="0" styleClass="stat-card-value"/>
                    </VBox>
                </HBox>

                <!-- Row 2: Charts -->
                <HBox spacing="20" VBox.vgrow="ALWAYS">
                    <!-- Left: Pie Chart Pengajuan -->
                    <VBox spacing="12" HBox.hgrow="ALWAYS" styleClass="dashboard-panel">
                        <Label text="Akumulasi Status Pengajuan Wilayah" styleClass="section-title"/>
                        <PieChart fx:id="statusPieChart" labelsVisible="true" legendSide="BOTTOM" VBox.vgrow="ALWAYS"/>
                    </VBox>

                    <!-- Right: Bar Chart Distribusi -->
                    <VBox spacing="12" HBox.hgrow="ALWAYS" styleClass="dashboard-panel">
                        <Label text="Distribusi Pupuk Tahunan (Kg)" styleClass="section-title"/>
                        <BarChart fx:id="distribusiBarChart" VBox.vgrow="ALWAYS">
                            <xAxis>
                                <CategoryAxis label="Tahun"/>
                            </xAxis>
                            <yAxis>
                                <NumberAxis label="Jumlah Pupuk (Kg)"/>
                            </yAxis>
                        </BarChart>
                    </VBox>
                </HBox>
            </VBox>
        </center>
    </BorderPane>
    ```
- [ ] **Step 2: Buat Controller `DashboardPetugasController.java`**
    Tulis logika dashboard petugas di `src/main/java/com/datani/controller/petugas/DashboardPetugasController.java`:
    ```java
    package com.datani.controller.petugas;

    import com.datani.model.LaporanGagalPanen;
    import com.datani.model.Pengajuan;
    import com.datani.model.StatusPengajuan;
    import com.datani.service.DataService;
    import javafx.collections.FXCollections;
    import javafx.collections.ObservableList;
    import javafx.fxml.FXML;
    import javafx.scene.chart.BarChart;
    import javafx.scene.chart.PieChart;
    import javafx.scene.chart.XYChart;
    import javafx.scene.control.Label;

    public class DashboardPetugasController {

        @FXML private Label totalPetaniLabel;
        @FXML private Label pengajuanMenungguLabel;
        @FXML private Label gagalPanenMenungguLabel;

        @FXML private PieChart statusPieChart;
        @FXML private BarChart<String, Number> distribusiBarChart;

        @FXML
        private void initialize() {
            // 1. Set Ringkasan Kinerja BPP
            totalPetaniLabel.setText(String.valueOf(DataService.getAllPetani().size()));

            int setujuCount = 0;
            int tungguCount = 0;
            int tolakCount = 0;

            for (Pengajuan p : DataService.getAllPengajuan()) {
                if (p.getStatus() == StatusPengajuan.DISETUJUI) {
                    setujuCount++;
                } else if (p.getStatus() == StatusPengajuan.MENUNGGU_VERIFIKASI) {
                    tungguCount++;
                } else if (p.getStatus() == StatusPengajuan.DITOLAK) {
                    tolakCount++;
                }
            }

            pengajuanMenungguLabel.setText(String.valueOf(tungguCount));

            int gagalTungguCount = 0;
            for (LaporanGagalPanen l : DataService.getAllLaporanGagalPanen()) {
                if (l.getStatus().equals("Menunggu Peninjauan")) {
                    gagalTungguCount++;
                }
            }
            gagalPanenMenungguLabel.setText(String.valueOf(gagalTungguCount));

            // 2. Load Pie Chart status pengajuan wilayah
            ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList(
                    new PieChart.Data("Disetujui (" + setujuCount + ")", setujuCount),
                    new PieChart.Data("Menunggu (" + tungguCount + ")", tungguCount),
                    new PieChart.Data("Ditolak (" + tolakCount + ")", tolakCount)
            );
            statusPieChart.setData(pieData);

            // 3. Load Bar Chart distribusi pupuk (historis)
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Penyaluran Pupuk");
            series.getData().add(new XYChart.Data<>("2024", 12500));
            series.getData().add(new XYChart.Data<>("2025", 14300));
            series.getData().add(new XYChart.Data<>("2026", 16800));

            distribusiBarChart.getData().clear();
            distribusiBarChart.getData().add(series);
        }
    }
    ```
- [ ] **Step 3: Kompilasi dan verifikasi**
    Jalankan: `mvn compile`
    Expected: BUILD SUCCESS
- [ ] **Step 4: Commit**
    Commit dengan pesan `feat: add UC07 Dashboard Petugas layout and controller`.

---

### Task 8: Update Sidebars for Navigation
**Files:**
*   Modify: `src/main/resources/com/datani/fxml/petani/PetaniSidebar.fxml`
*   Modify: `src/main/java/com/datani/controller/petani/PetaniSidebarController.java`
*   Modify: `src/main/resources/com/datani/fxml/petugas/PetugasSidebar.fxml`
*   Modify: `src/main/java/com/datani/controller/petugas/PetugasSidebarController.java`

- [ ] **Step 1: Update `PetaniSidebar.fxml` dengan tombol menu baru**
    Tambahkan tombol "Beranda" (Dashboard) dan "Lapor Gagal Panen" di `PetaniSidebar.fxml`:
    ```xml
        <!-- Ganti baris menu lama (sekitar baris 27-29) dengan: -->
        <Button text="Beranda (Dashboard)" maxWidth="Infinity" styleClass="sidebar-button" onAction="#goToDashboard"/>
        <Button text="Pengajuan Pupuk Subsidi" maxWidth="Infinity" styleClass="sidebar-button" onAction="#goToPengajuanPupuk"/>
        <Button text="Lapor Gagal Panen" maxWidth="Infinity" styleClass="sidebar-button" onAction="#goToLaporGagalPanen"/>
        <Button text="Status Pengajuan" maxWidth="Infinity" styleClass="sidebar-button" onAction="#goToStatusPengajuan"/>
        <Button text="Profil" maxWidth="Infinity" styleClass="sidebar-button" onAction="#goToProfil"/>
    ```
- [ ] **Step 2: Update `PetaniSidebarController.java`**
    Tambahkan method navigasi di `PetaniSidebarController.java`:
    ```java
        @FXML
        private void goToDashboard() {
            NavigationManager.navigateToDashboardPetani();
        }

        @FXML
        private void goToLaporGagalPanen() {
            NavigationManager.navigateToLaporGagalPanen();
        }
    ```
- [ ] **Step 3: Update `PetugasSidebar.fxml` dengan menu baru**
    Tambahkan tombol "Beranda" dan "Verifikasi Gagal Panen" di `PetugasSidebar.fxml`:
    ```xml
        <!-- Ganti baris menu lama dengan: -->
        <Button text="Beranda (Dashboard)" maxWidth="Infinity" styleClass="sidebar-button" onAction="#goToDashboard"/>
        <Button text="Verifikasi Pengajuan" maxWidth="Infinity" styleClass="sidebar-button" onAction="#goToVerifikasiPengajuan"/>
        <Button text="Verifikasi Gagal Panen" maxWidth="Infinity" styleClass="sidebar-button" onAction="#goToVerifikasiLaporan"/>
        <Button text="Hasil Verifikasi" maxWidth="Infinity" styleClass="sidebar-button" onAction="#goToHasilVerifikasi"/>
        <Button text="Profil" maxWidth="Infinity" styleClass="sidebar-button" onAction="#goToProfil"/>
    ```
- [ ] **Step 4: Update `PetugasSidebarController.java`**
    Tambahkan method navigasi di `PetugasSidebarController.java`:
    ```java
        @FXML
        private void goToDashboard() {
            NavigationManager.navigateToDashboardPetugas();
        }

        @FXML
        private void goToVerifikasiLaporan() {
            NavigationManager.navigateToVerifikasiGagalPanen();
        }
    ```
- [ ] **Step 5: Kompilasi dan verifikasi**
    Jalankan: `mvn compile`
    Expected: BUILD SUCCESS
- [ ] **Step 6: Commit**
    Commit dengan pesan `feat: update sidebar layouts and controllers for dashboards and gagal panen`.

---

### Task 9: Integrate NavigationManager & Login Redirect
**Files:**
*   Modify: `src/main/java/com/datani/navigation/NavigationManager.java`

- [ ] **Step 1: Tambahkan rute navigasi baru di `NavigationManager.java`**
    Tambahkan method publik berikut:
    ```java
        public static void navigateToDashboardPetani() {
            requireRole(Role.PETANI);
            loadScene(FXML_BASE + "petani/DashboardPetani.fxml", APP_TITLE + " - Dasbor Petani");
        }

        public static void navigateToDashboardPetugas() {
            requireRole(Role.PETUGAS_BPP);
            loadScene(FXML_BASE + "petugas/DashboardPetugas.fxml", APP_TITLE + " - Dasbor Petugas BPP");
        }

        public static void navigateToLaporGagalPanen() {
            requireRole(Role.PETANI);
            loadScene(FXML_BASE + "petani/LaporGagalPanen.fxml", APP_TITLE + " - Lapor Gagal Panen");
        }

        public static void navigateToVerifikasiGagalPanen() {
            requireRole(Role.PETUGAS_BPP);
            loadScene(FXML_BASE + "petugas/VerifikasiLaporan.fxml", APP_TITLE + " - Verifikasi Gagal Panen");
        }
    ```
- [ ] **Step 2: Arahkan Login Sukses ke Halaman Dashboard**
    Ubah implementasi `navigateAfterLogin()` agar mengarah ke dasbor masing-masing:
    ```java
        public static void navigateAfterLogin() {
            User user = UserSession.getCurrentUser();
            if (user == null) {
                navigateToLogin();
                return;
            }
            if (user.getRole() == Role.PETUGAS_BPP) {
                navigateToDashboardPetugas();
            } else {
                navigateToDashboardPetani();
            }
        }
    ```
- [ ] **Step 3: Jalankan kompilasi proyek akhir**
    Jalankan: `mvn clean compile`
    Expected: BUILD SUCCESS
- [ ] **Step 4: Jalankan aplikasi secara langsung**
    Uji apakah aplikasi berjalan sempurna dan menampilkan Dasbor Beranda setelah login sukses:
    Jalankan: `mvn javafx:run`
- [ ] **Step 5: Commit**
    Commit dengan pesan `feat: complete navigation routing and final integration`.
