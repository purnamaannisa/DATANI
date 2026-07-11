package com.datani.controller.petani;

import com.datani.model.Pengajuan;
import com.datani.model.Petani;
import com.datani.model.StatusPengajuan;
import com.datani.navigation.NavigationManager;
import com.datani.service.DataService;
import com.datani.session.UserSession;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.time.LocalDate;

/**
 * Controller untuk PengajuanPupuk.fxml (Petani only).
 * <p>
 * PENTING: layar ini TIDAK meminta Petani mengisi ulang data pribadi.
 * Data NIK, Nama Lengkap, Nomor KK, Alamat, Nomor HP, dan Kelompok Tani
 * dimuat secara otomatis dari akun Petani yang sedang login. Field input
 * yang benar-benar terlihat hanyalah: Luas Lahan, Status Kepemilikan Lahan,
 * Jenis Tanaman, dan Upload Foto Bukti Lahan.
 */
public class PengajuanPupukController {

    // --- Data pribadi (hanya ditampilkan, otomatis dimuat) ---

    @FXML
    private Label nikValueLabel;

    @FXML
    private Label namaLengkapValueLabel;

    @FXML
    private Label nomorKKValueLabel;

    @FXML
    private Label alamatValueLabel;

    @FXML
    private Label nomorHPValueLabel;

    @FXML
    private Label kelompokTaniValueLabel;

    // --- Field input pengajuan ---

    @FXML
    private TextField luasLahanField;

    @FXML
    private ComboBox<String> statusKepemilikanCombo;

    @FXML
    private ComboBox<String> jenisTanamanCombo;

    @FXML
    private ImageView fotoLahanPreview;

    @FXML
    private Label fotoLahanFileNameLabel;

    @FXML
    private Label errorLabel;

    private String fotoLahanPath;

    @FXML
    private void initialize() {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        statusKepemilikanCombo.setItems(FXCollections.observableArrayList("Milik Sendiri", "Sewa"));
        jenisTanamanCombo.setItems(FXCollections.observableArrayList(
                "Padi", "Jagung", "Kedelai", "Cabai", "Bawang", "Tebu", "Kopi", "Kakao", "Lainnya"));

        muatDataPetani();
    }

    private void muatDataPetani() {
        Petani petani = UserSession.getCurrentPetani();
        if (petani == null) {
            return;
        }
        nikValueLabel.setText(petani.getNik());
        namaLengkapValueLabel.setText(petani.getNamaLengkap());
        nomorKKValueLabel.setText(petani.getNomorKK());
        alamatValueLabel.setText(petani.getAlamat());
        nomorHPValueLabel.setText(petani.getNomorHP());
        kelompokTaniValueLabel.setText(petani.getKelompokTani());
    }

    @FXML
    private void handleUploadFotoLahan() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Pilih Foto Bukti Lahan");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Berkas Gambar", "*.jpg", "*.jpeg", "*.png"));
        Window window = luasLahanField.getScene().getWindow();
        File file = fileChooser.showOpenDialog(window);
        if (file == null) {
            return;
        }
        fotoLahanPath = file.getAbsolutePath();
        fotoLahanFileNameLabel.setText(file.getName());
        try {
            fotoLahanPreview.setImage(new Image(file.toURI().toString()));
        } catch (Exception e) {
            fotoLahanPreview.setImage(null);
            showError("Foto tidak dapat dimuat. Pastikan berkas berformat JPG, JPEG, atau PNG.");
        }
    }

    @FXML
    private void handleSubmit() {
        Petani petani = UserSession.getCurrentPetani();
        if (petani == null) {
            showError("Sesi tidak valid. Silakan login kembali.");
            return;
        }

        String luasLahanText = luasLahanField.getText() == null ? "" : luasLahanField.getText().trim();
        double luasLahan;
        try {
            luasLahan = Double.parseDouble(luasLahanText);
            if (luasLahan <= 0) {
                showError("Luas Lahan harus lebih besar dari 0.");
                return;
            }
        } catch (NumberFormatException e) {
            showError("Luas Lahan harus berupa angka yang valid (dalam hektar).");
            return;
        }

        String statusKepemilikan = statusKepemilikanCombo.getValue();
        if (statusKepemilikan == null) {
            showError("Silakan pilih Status Kepemilikan Lahan.");
            return;
        }

        String jenisTanaman = jenisTanamanCombo.getValue();
        if (jenisTanaman == null) {
            showError("Silakan pilih Jenis Tanaman.");
            return;
        }

        if (fotoLahanPath == null || fotoLahanPath.trim().isEmpty()) {
            showError("Silakan unggah Foto Bukti Lahan (JPG, JPEG, atau PNG).");
            return;
        }

        int newId = DataService.getNextPengajuanId();
        Pengajuan pengajuan = new Pengajuan(
                newId,
                petani.getId(),
                luasLahan,
                statusKepemilikan,
                jenisTanaman,
                fotoLahanPath,
                StatusPengajuan.MENUNGGU_VERIFIKASI,
                null,
                LocalDate.now(),
                null
        );
        DataService.addPengajuan(pengajuan);

        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Pengajuan Terkirim");
        alert.setHeaderText(null);
        alert.setContentText("Pengajuan #" + newId + " berhasil dibuat dan berstatus \"Menunggu Verifikasi\".");
        alert.showAndWait();

        clearForm();
        NavigationManager.navigateToStatusPengajuan();
    }

    private void clearForm() {
        luasLahanField.clear();
        statusKepemilikanCombo.getSelectionModel().clearSelection();
        jenisTanamanCombo.getSelectionModel().clearSelection();
        fotoLahanPath = null;
        fotoLahanPreview.setImage(null);
        fotoLahanFileNameLabel.setText("");
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }
}
