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
    @FXML private ComboBox<String> satuanLuasCombo;
    @FXML private TextField luasField;
    @FXML private TextField kerusakanField;
    @FXML private DatePicker tanggalPicker;
    @FXML private Label fotoPlaceholder;
    @FXML private ImageView previewImage;

    private File fileFotoTerpilih;

    @FXML
    private void initialize() {
        penyebabCombo.setItems(FXCollections.observableArrayList("Hama/Penyakit", "Kekeringan", "Banjir", "Bencana Alam", "Lainnya"));
        penyebabCombo.setEditable(true);
        
        satuanLuasCombo.setItems(FXCollections.observableArrayList("Hektar", "Meter Persegi (m²)"));
        satuanLuasCombo.getSelectionModel().selectFirst();
        
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
        File file = chooser.showOpenDialog(fotoPlaceholder.getScene().getWindow());
        if (file != null) {
            fileFotoTerpilih = file;
            previewImage.setImage(new Image(file.toURI().toString()));
            previewImage.setVisible(true);
            fotoPlaceholder.setVisible(false);
        }
    }

    @FXML
    private void handleKirimLaporan() {
        String lahan = lahanCombo.getValue();
        String penyebab = penyebabCombo.getValue();
        String luasStr = luasField.getText() == null ? "" : luasField.getText().trim();
        luasStr = luasStr.replace(",", "."); // Ganti koma dengan titik untuk desimal
        String rusakStr = kerusakanField.getText();
        LocalDate tgl = tanggalPicker.getValue();

        if (lahan == null || penyebab == null || luasStr == null || rusakStr == null || tgl == null || fileFotoTerpilih == null) {
            showError("Semua form wajib diisi dan foto wajib dilampirkan.");
            return;
        }

        try {
            double luas = Double.parseDouble(luasStr);
            
            // Konversi ke hektar jika satuan yang dipilih adalah Meter Persegi
            if ("Meter Persegi (m²)".equals(satuanLuasCombo.getValue())) {
                luas = luas / 10000.0;
            }
            
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
            previewImage.setVisible(false);
            fotoPlaceholder.setVisible(true);

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
