package com.datani.controller.petugas;

import com.datani.datastructure.AntreanLaporan;
import com.datani.datastructure.PengurutanData;
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
import java.util.ArrayList;
import java.util.List;

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
        List<LaporanGagalPanen> tempList = new ArrayList<>();
        for (LaporanGagalPanen l : DataService.getAllLaporanGagalPanen()) {
            if (l.getStatus().equals("Menunggu Peninjauan")) {
                tempList.add(l);
            }
        }
        
        // ASD Task 5: Quick Sort (Urutkan dari kerusakan terparah/descending)
        if (!tempList.isEmpty()) {
            PengurutanData.quickSortKerusakan(tempList, 0, tempList.size() - 1);
        }

        // ASD Task 1: Masukkan ke Queue (Antrean FIFO prioritas)
        AntreanLaporan antreanBpp = new AntreanLaporan();
        for (LaporanGagalPanen l : tempList) {
            antreanBpp.tambahAntrean(l);
        }

        // Keluarkan dari Queue untuk ditampilkan di UI TableView
        ObservableList<LaporanGagalPanen> antreanVisual = FXCollections.observableArrayList();
        while (!antreanBpp.isKosong()) {
            antreanVisual.add(antreanBpp.prosesPengajuanDepan());
        }

        antreanTable.setItems(antreanVisual);
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
