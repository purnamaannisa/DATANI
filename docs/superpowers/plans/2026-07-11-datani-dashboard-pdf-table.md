# DATANI Dashboard PDF Export & TableView Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Menambahkan tabel data distribusi tahunan yang menjadi dasar grafik dan fitur unduh laporan PDF pada Dasbor Petugas BPP.

**Architecture:** Memanfaatkan library OpenPDF untuk membuat dokumen PDF resmi yang memuat rekapitulasi data dashboard dan tabel penyaluran pupuk tahunan.

**Tech Stack:** Java SE 9, JavaFX, OpenPDF 1.3.30.

## Global Constraints
*   Target rilis dan kompatibilitas: Java SE 9.
*   Bahasa antarmuka dan laporan PDF: Bahasa Indonesia yang baku dan sederhana.
*   Penyimpanan PDF: Menggunakan dialog `FileChooser` agar user menentukan lokasinya sendiri.

---

### Task 1: Add OpenPDF Dependency
**Files:**
*   Modify: `pom.xml`

- [ ] **Step 1: Tambahkan dependensi OpenPDF**
    Buka `pom.xml` dan tambahkan dependensi `openpdf` di dalam `<dependencies>` utama:
    ```xml
    <dependency>
        <groupId>com.github.librepdf</groupId>
        <artifactId>openpdf</artifactId>
        <version>1.3.30</version>
    </dependency>
    ```
- [ ] **Step 2: Jalankan kompilasi untuk memverifikasi**
    Perintah: `mvn clean compile`
    Expected: BUILD SUCCESS
- [ ] **Step 3: Commit perubahan**
    Commit dengan pesan `feat: add openpdf dependency`.

---

### Task 2: Add Tombol Unduh PDF & TableView to FXML
**Files:**
*   Modify: `src/main/resources/com/datani/fxml/petugas/DashboardPetugas.fxml`

- [ ] **Step 1: Modifikasi `DashboardPetugas.fxml`**
    Ubah layout FXML untuk:
    1. Menambahkan tombol "Unduh Laporan PDF" di bagian header.
    2. Menambahkan `TableView` di dalam panel distribusi tahunan di bawah `BarChart`.
    ```xml
    <!-- Gantilah baris header page-title dengan layout HBox berikut agar tombol muat rapi di sebelah kanan: -->
    <HBox spacing="10" alignment="CENTER_LEFT">
        <VBox HBox.hgrow="ALWAYS">
            <Label styleClass="page-title" text="Dasbor Monitoring Petugas BPP" />
            <Label styleClass="page-subtitle" text="Ringkasan penyaluran pupuk subsidi dan peninjauan wilayah UPTD BP4 Pakem" />
        </VBox>
        <Button text="Unduh Laporan PDF" onAction="#handleUnduhPdf" styleClass="primary-button" />
    </HBox>

    <!-- Di dalam panel Right: Bar Chart Distribusi, tambahkan TableView di bawah BarChart: -->
    <VBox spacing="12" styleClass="dashboard-panel" HBox.hgrow="ALWAYS">
        <Label styleClass="section-title" text="Distribusi Pupuk Tahunan" />
        <BarChart fx:id="distribusiBarChart" prefHeight="250">
            <xAxis>
                <CategoryAxis label="Tahun" />
            </xAxis>
            <yAxis>
                <NumberAxis label="Jumlah Pupuk (Kg)" />
            </yAxis>
        </BarChart>
        <Label styleClass="field-label" text="Data Detail Penyaluran:" />
        <TableView fx:id="distribusiTable" prefHeight="150">
            <columns>
                <TableColumn fx:id="tahunColumn" text="Tahun" prefWidth="100"/>
                <TableColumn fx:id="jumlahColumn" text="Jumlah Penyaluran (Kg)" prefWidth="180"/>
                <TableColumn fx:id="jenisColumn" text="Jenis Pupuk Utama" prefWidth="180"/>
            </columns>
        </TableView>
    </VBox>
    ```
- [ ] **Step 2: Kompilasi untuk memverifikasi file FXML**
    Perintah: `mvn compile`
    Expected: BUILD SUCCESS
- [ ] **Step 3: Commit**
    Commit dengan pesan `feat: add pdf download button and distribution table to DashboardPetugas.fxml`.

---

### Task 3: Implement TableView & PDF Export in Controller
**Files:**
*   Modify: `src/main/java/com/datani/controller/petugas/DashboardPetugasController.java`

- [ ] **Step 1: Tulis logika di `DashboardPetugasController.java`**
    Tulis penampung data table, inisialisasi tabel, dan kode ekspor PDF menggunakan library OpenPDF.
    ```java
    // Import tambahan
    import com.datani.model.Petani;
    import com.datani.session.UserSession;
    import com.lowagie.text.Document;
    import com.lowagie.text.Element;
    import com.lowagie.text.Font;
    import com.lowagie.text.Paragraph;
    import com.lowagie.text.pdf.PdfPTable;
    import com.lowagie.text.pdf.PdfWriter;
    import javafx.beans.property.SimpleIntegerProperty;
    import javafx.beans.property.SimpleStringProperty;
    import javafx.stage.FileChooser;
    import java.io.File;
    import java.io.FileOutputStream;
    import java.time.format.DateTimeFormatter;

    // Field baru di controller
    @FXML private TableView<DistribusiTahunan> distribusiTable;
    @FXML private TableColumn<DistribusiTahunan, Integer> tahunColumn;
    @FXML private TableColumn<DistribusiTahunan, String> jumlahColumn;
    @FXML private TableColumn<DistribusiTahunan, String> jenisColumn;

    // Inisialisasi table di dalam initialize()
    tahunColumn.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getTahun()).asObject());
    jumlahColumn.setCellValueFactory(d -> new SimpleStringProperty(String.format("%,.0f Kg", d.getValue().getJumlah())));
    jenisColumn.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getJenis()));

    ObservableList<DistribusiTahunan> listDist = FXCollections.observableArrayList(
            new DistribusiTahunan(2024, 12500, "Urea & NPK"),
            new DistribusiTahunan(2025, 14300, "Urea & NPK"),
            new DistribusiTahunan(2026, 16800, "Urea, NPK, & Organik")
    );
    distribusiTable.setItems(listDist);

    // Method export PDF
    @FXML
    private void handleUnduhPdf() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Simpan Laporan LPDF");
        chooser.setInitialFileName("Laporan_Dashboard_BPP_" + LocalDate.now() + ".pdf");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files (*.pdf)", "*.pdf"));
        File file = chooser.showSaveDialog(distribusiTable.getScene().getWindow());

        if (file != null) {
            try {
                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(file));
                document.open();

                // Style Font
                Font fontTitle = new Font(Font.HELVETICA, 16, Font.BOLD);
                Font fontSubtitle = new Font(Font.HELVETICA, 10, Font.ITALIC);
                Font fontBold = new Font(Font.HELVETICA, 11, Font.BOLD);
                Font fontNormal = new Font(Font.HELVETICA, 11, Font.NORMAL);

                // 1. Kop Surat
                Paragraph kop = new Paragraph("UPTD BP4 WILAYAH V PAKEM", fontTitle);
                kop.setAlignment(Element.ALIGN_CENTER);
                document.add(kop);
                
                Paragraph subKop = new Paragraph("Sistem Informasi Pengajuan Data Petani (DATANI)\nAlamat: Pakem, Sleman, Yogyakarta. Laporan Monitoring Wilayah", fontSubtitle);
                subKop.setAlignment(Element.ALIGN_CENTER);
                document.add(subKop);
                document.add(new Paragraph("\n-------------------------------------------------------------------------------------------------------------------------\n"));

                // 2. Info Pembuat Laporan
                String petugasNama = UserSession.getCurrentUser() != null ? UserSession.getCurrentUser().getFullName() : "Petugas BPP";
                document.add(new Paragraph("Dibuat Oleh: " + petugasNama, fontNormal));
                document.add(new Paragraph("Tanggal Cetak: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy")), fontNormal));
                document.add(new Paragraph("\n"));

                // 3. Ringkasan Kinerja (Statistik)
                document.add(new Paragraph("I. RINGKASAN MONITORING WILAYAH", fontBold));
                document.add(new Paragraph("1. Total Petani Terdaftar: " + DataService.getAllPetani().size() + " Petani", fontNormal));
                
                long pengajuanTunggu = DataService.getAllPengajuan().stream().filter(p -> p.getStatus() == StatusPengajuan.MENUNGGU_VERIFIKASI).count();
                document.add(new Paragraph("2. Pengajuan Menunggu Verifikasi: " + pengajuanTunggu + " Pengajuan", fontNormal));
                
                long gagalTunggu = DataService.getAllLaporanGagalPanen().stream().filter(g -> g.getStatus().equals("Menunggu Peninjauan")).count();
                document.add(new Paragraph("3. Laporan Gagal Panen Tertunda: " + gagalTunggu + " Laporan", fontNormal));
                document.add(new Paragraph("\n"));

                // 4. Tabel Distribusi Pupuk
                document.add(new Paragraph("II. DATA HISTORIS DISTRIBUSI PUPUK TAHUNAN", fontBold));
                document.add(new Paragraph("\n"));

                PdfPTable table = new PdfPTable(3);
                table.setWidthPercentage(100);
                table.addCell(new Paragraph("Tahun", fontBold));
                table.addCell(new Paragraph("Jumlah Penyaluran", fontBold));
                table.addCell(new Paragraph("Jenis Pupuk Utama", fontBold));

                for (DistribusiTahunan dt : distribusiTable.getItems()) {
                    table.addCell(new Paragraph(String.valueOf(dt.getTahun()), fontNormal));
                    table.addCell(new Paragraph(String.format("%,.0f Kg", dt.getJumlah()), fontNormal));
                    table.addCell(new Paragraph(dt.getJenis(), fontNormal));
                }
                document.add(table);

                document.close();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Ekspor PDF Berhasil");
                alert.setHeaderText(null);
                alert.setContentText("Laporan dashboard BPP berhasil diekspor ke: " + file.getAbsolutePath());
                alert.showAndWait();

            } catch (Exception e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Ekspor PDF Gagal");
                alert.setHeaderText(null);
                alert.setContentText("Terjadi kesalahan saat memproses file PDF: " + e.getMessage());
                alert.showAndWait();
            }
        }
    }

    // Inner class helper untuk penampung tabel
    public static class DistribusiTahunan {
        private final int tahun;
        private final double jumlah;
        private final String jenis;

        public DistribusiTahunan(int tahun, double jumlah, String jenis) {
            this.tahun = tahun;
            this.jumlah = jumlah;
            this.jenis = jenis;
        }
        public int getTahun() { return tahun; }
        public double getJumlah() { return jumlah; }
        public String getJenis() { return jenis; }
    }
    ```
- [ ] **Step 2: Kompilasi dan jalankan**
    Lakukan kompilasi untuk memastikan tidak ada kesalahan penulisan kode Java:
    Perintah: `mvn clean compile`
    Expected: BUILD SUCCESS
- [ ] **Step 3: Commit**
    Commit dengan pesan `feat: implement PDF export and TableView logic in DashboardPetugasController.java`.
