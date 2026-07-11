package com.datani.controller;

import com.datani.navigation.NavigationManager;
import com.datani.service.DataService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Controller untuk halaman Registrasi Petani (Register.fxml).
 * Setelah registrasi berhasil, akun {@code User} (untuk login) dan data
 * {@code Petani} (untuk data pribadi) dibuat sekaligus, kemudian pengguna
 * otomatis diarahkan kembali ke halaman Login.
 */
public class RegisterController {

    @FXML
    private TextField nikField;

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
    private PasswordField passwordField;

    @FXML
    private PasswordField konfirmasiPasswordField;

    @FXML
    private Label errorLabel;

    @FXML
    private void initialize() {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        // Batasi input NIK hanya angka, maksimum 16 digit.
        restrictToDigits(nikField, 16);
        // Batasi input Nomor KK hanya angka, maksimum 16 digit.
        restrictToDigits(nomorKKField, 16);
    }

    private void restrictToDigits(TextField field, int maxLength) {
        field.textProperty().addListener((obs, oldValue, newValue) -> {
            String digitsOnly = newValue == null ? "" : newValue.replaceAll("[^0-9]", "");
            if (digitsOnly.length() > maxLength) {
                digitsOnly = digitsOnly.substring(0, maxLength);
            }
            if (!digitsOnly.equals(newValue)) {
                field.setText(digitsOnly);
            }
        });
    }

    @FXML
    private void handleDaftar() {
        String nik = trim(nikField.getText());
        String namaLengkap = trim(namaLengkapField.getText());
        String nomorKK = trim(nomorKKField.getText());
        String alamat = trim(alamatField.getText());
        String nomorHP = trim(nomorHPField.getText());
        String kelompokTani = trim(kelompokTaniField.getText());
        String password = passwordField.getText() == null ? "" : passwordField.getText();
        String konfirmasiPassword = konfirmasiPasswordField.getText() == null ? "" : konfirmasiPasswordField.getText();

        // Semua field wajib diisi.
        if (nik.isEmpty() || namaLengkap.isEmpty() || nomorKK.isEmpty() || alamat.isEmpty()
                || nomorHP.isEmpty() || kelompokTani.isEmpty() || password.isEmpty() || konfirmasiPassword.isEmpty()) {
            showError("Semua field wajib diisi.");
            return;
        }

        // NIK harus 16 digit.
        if (!nik.matches("\\d{16}")) {
            showError("NIK harus 16 digit angka.");
            return;
        }

        // NIK harus unik.
        if (DataService.isNikTerdaftar(nik)) {
            showError("NIK sudah terdaftar. Silakan login.");
            return;
        }

        // Nomor KK harus valid (16 digit angka).
        if (!nomorKK.matches("\\d{16}")) {
            showError("Nomor KK harus valid (16 digit angka).");
            return;
        }

        // Nomor HP wajib diisi dan berupa angka yang wajar.
        if (!nomorHP.matches("\\d{9,14}")) {
            showError("Nomor HP wajib diisi dan harus berupa angka (9-14 digit).");
            return;
        }

        // Password dan konfirmasi password harus sama.
        if (!password.equals(konfirmasiPassword)) {
            showError("Password dan konfirmasi password harus sama.");
            return;
        }

        DataService.registerPetani(nik, password, namaLengkap, nomorKK, alamat, nomorHP, kelompokTani);

        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Registrasi Berhasil");
        alert.setHeaderText(null);
        alert.setContentText("Tahap registrasi berhasil. Silakan login.");
        alert.showAndWait();

        NavigationManager.navigateToLogin();
    }

    @FXML
    private void handleKembaliKeLogin() {
        NavigationManager.navigateToLogin();
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }
}
