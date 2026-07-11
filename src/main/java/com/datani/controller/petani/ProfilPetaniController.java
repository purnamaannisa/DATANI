package com.datani.controller.petani;

import com.datani.model.Petani;
import com.datani.session.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * Controller untuk ProfilPetani.fxml.
 * Menampilkan data pribadi Petani (NIK, Nama Lengkap, Nomor KK, Alamat,
 * Nomor HP, Kelompok Tani) dan menyediakan tombol "Edit Profil" untuk
 * memperbarui data yang boleh diubah (NIK tidak dapat diubah karena
 * menjadi identitas login).
 */
public class ProfilPetaniController {

    @FXML
    private Label nikLabel;

    @FXML
    private TextField namaLengkapField;

    @FXML
    private TextField nomorKKField;

    @FXML
    private TextField alamatField;

    @FXML
    private TextField nomorHPField;

    @FXML
    private TextField kelompokTaniField;

    @FXML
    private Button editProfilButton;

    private boolean modeEdit = false;

    @FXML
    private void initialize() {
        muatData();
        setModeEdit(false);
    }

    private void muatData() {
        Petani petani = UserSession.getCurrentPetani();
        if (petani == null) {
            return;
        }
        nikLabel.setText(petani.getNik());
        namaLengkapField.setText(petani.getNamaLengkap());
        nomorKKField.setText(petani.getNomorKK());
        alamatField.setText(petani.getAlamat());
        nomorHPField.setText(petani.getNomorHP());
        kelompokTaniField.setText(petani.getKelompokTani());
    }

    @FXML
    private void handleEditProfil() {
        if (!modeEdit) {
            setModeEdit(true);
            return;
        }
        simpanPerubahan();
    }

    private void simpanPerubahan() {
        Petani petani = UserSession.getCurrentPetani();
        if (petani == null) {
            return;
        }

        String namaLengkap = trim(namaLengkapField.getText());
        String nomorKK = trim(nomorKKField.getText());
        String alamat = trim(alamatField.getText());
        String nomorHP = trim(nomorHPField.getText());
        String kelompokTani = trim(kelompokTaniField.getText());

        if (namaLengkap.isEmpty() || nomorKK.isEmpty() || alamat.isEmpty()
                || nomorHP.isEmpty() || kelompokTani.isEmpty()) {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Data Tidak Lengkap");
            alert.setHeaderText(null);
            alert.setContentText("Semua field wajib diisi.");
            alert.showAndWait();
            return;
        }

        petani.setNamaLengkap(namaLengkap);
        petani.setNomorKK(nomorKK);
        petani.setAlamat(alamat);
        petani.setNomorHP(nomorHP);
        petani.setKelompokTani(kelompokTani);

        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Profil Diperbarui");
        alert.setHeaderText(null);
        alert.setContentText("Profil Anda berhasil diperbarui.");
        alert.showAndWait();

        setModeEdit(false);
    }

    private void setModeEdit(boolean edit) {
        modeEdit = edit;
        namaLengkapField.setEditable(edit);
        nomorKKField.setEditable(edit);
        alamatField.setEditable(edit);
        nomorHPField.setEditable(edit);
        kelompokTaniField.setEditable(edit);
        editProfilButton.setText(edit ? "Simpan Perubahan" : "Edit Profil");
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
