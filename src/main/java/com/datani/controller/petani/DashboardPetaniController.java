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
