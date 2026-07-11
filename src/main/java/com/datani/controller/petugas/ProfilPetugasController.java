package com.datani.controller.petugas;

import com.datani.model.User;
import com.datani.session.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * Controller untuk ProfilPetugas.fxml.
 * Menampilkan Nama Lengkap, Username, Email, dan Nomor HP milik akun
 * Petugas BPP yang sedang login, dengan tombol "Edit Profil" untuk
 * memperbarui data yang boleh diubah. Username tidak dapat diubah karena
 * menjadi identitas login (Petugas BPP login menggunakan Username, bukan
 * NIK).
 */
public class ProfilPetugasController {

    @FXML
    private Label usernameLabel;

    @FXML
    private TextField namaLengkapField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField nomorHPField;

    @FXML
    private Button editProfilButton;

    private boolean modeEdit = false;

    @FXML
    private void initialize() {
        muatData();
        setModeEdit(false);
    }

    private void muatData() {
        User user = UserSession.getCurrentUser();
        if (user == null) {
            return;
        }
        usernameLabel.setText(user.getUsername());
        namaLengkapField.setText(user.getFullName());
        emailField.setText(user.getEmail());
        nomorHPField.setText(user.getPhone());
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
        User user = UserSession.getCurrentUser();
        if (user == null) {
            return;
        }

        String namaLengkap = trim(namaLengkapField.getText());
        String email = trim(emailField.getText());
        String nomorHP = trim(nomorHPField.getText());

        if (namaLengkap.isEmpty() || email.isEmpty() || nomorHP.isEmpty()) {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Data Tidak Lengkap");
            alert.setHeaderText(null);
            alert.setContentText("Semua field wajib diisi.");
            alert.showAndWait();
            return;
        }

        user.setFullName(namaLengkap);
        user.setEmail(email);
        user.setPhone(nomorHP);

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
        emailField.setEditable(edit);
        nomorHPField.setEditable(edit);
        editProfilButton.setText(edit ? "Simpan Perubahan" : "Edit Profil");
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
