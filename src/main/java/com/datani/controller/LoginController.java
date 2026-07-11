package com.datani.controller;

import com.datani.model.Petani;
import com.datani.model.User;
import com.datani.navigation.NavigationManager;
import com.datani.service.DataService;
import com.datani.session.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

/**
 * Controller untuk halaman Login (Login.fxml).
 * <p>
 * Halaman ini menampilkan dua Tab:
 * <ul>
 *     <li><b>Tab Petani</b> - login menggunakan NIK + Kata Sandi.</li>
 *     <li><b>Tab Petugas BPP</b> - login menggunakan Username + Kata
 *     Sandi (TIDAK menggunakan NIK).</li>
 * </ul>
 * Setelah autentikasi berhasil, satu-satunya tempat yang menentukan layar
 * berikutnya adalah {@link NavigationManager#navigateAfterLogin()},
 * berdasarkan {@link User#getRole()}.
 */
public class LoginController {

    // --- Tab Petani ---
    @FXML
    private TextField nikField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabelPetani;

    @FXML
    private Hyperlink daftarLink;

    // --- Tab Petugas BPP ---
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordFieldPetugas;

    @FXML
    private Label errorLabelPetugas;

    @FXML
    private void initialize() {
        errorLabelPetani.setVisible(false);
        errorLabelPetani.setManaged(false);
        errorLabelPetugas.setVisible(false);
        errorLabelPetugas.setManaged(false);

        passwordField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleLoginPetani();
            }
        });
        passwordFieldPetugas.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleLoginPetugas();
            }
        });
    }

    @FXML
    private void handleLoginPetani() {
        String nik = nikField.getText() == null ? "" : nikField.getText().trim();
        String password = passwordField.getText() == null ? "" : passwordField.getText();

        if (nik.isEmpty() || password.isEmpty()) {
            showError(errorLabelPetani, "NIK dan Kata Sandi wajib diisi.");
            return;
        }

        User user = DataService.authenticatePetani(nik, password);
        if (user == null) {
            showError(errorLabelPetani, "NIK atau Kata Sandi salah.");
            return;
        }

        UserSession.setCurrentUser(user);
        Petani petani = DataService.getPetaniByNik(user.getNik()).orElse(null);
        UserSession.setCurrentPetani(petani);

        NavigationManager.navigateAfterLogin();
    }

    @FXML
    private void handleLoginPetugas() {
        String username = usernameField.getText() == null ? "" : usernameField.getText().trim();
        String password = passwordFieldPetugas.getText() == null ? "" : passwordFieldPetugas.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError(errorLabelPetugas, "Username dan Kata Sandi wajib diisi.");
            return;
        }

        User user = DataService.authenticatePetugas(username, password);
        if (user == null) {
            showError(errorLabelPetugas, "Username atau Kata Sandi salah.");
            return;
        }

        UserSession.setCurrentUser(user);
        NavigationManager.navigateAfterLogin();
    }

    @FXML
    private void handleGoToRegister() {
        NavigationManager.navigateToRegister();
    }

    private void showError(Label label, String message) {
        label.setText(message);
        label.setVisible(true);
        label.setManaged(true);
    }
}
