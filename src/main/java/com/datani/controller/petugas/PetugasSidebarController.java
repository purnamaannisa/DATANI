package com.datani.controller.petugas;

import com.datani.navigation.NavigationManager;
import com.datani.session.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Controller untuk PetugasSidebar.fxml.
 * Hanya disertakan pada layar milik Petugas BPP. Menu: Verifikasi
 * Pengajuan, Hasil Verifikasi, Profil, Logout. Tidak ada menu Dashboard.
 */
public class PetugasSidebarController {

    @FXML
    private Label userNameLabel;

    @FXML
    private Label userRoleLabel;

    @FXML private javafx.scene.control.Button btnDashboard;
    @FXML private javafx.scene.control.Button btnVerifikasiPupuk;
    @FXML private javafx.scene.control.Button btnVerifikasiGagal;
    @FXML private javafx.scene.control.Button btnHasil;
    @FXML private javafx.scene.control.Button btnProfil;

    @FXML
    private void initialize() {
        if (UserSession.getCurrentUser() != null) {
            userNameLabel.setText(UserSession.getCurrentUser().getFullName());
        }
        userRoleLabel.setText("Petugas BPP");

        String fxml = NavigationManager.getCurrentFxml();
        if (fxml != null) {
            if (fxml.endsWith("DashboardPetugas.fxml")) btnDashboard.getStyleClass().add("active");
            else if (fxml.endsWith("VerifikasiLaporan.fxml")) btnVerifikasiGagal.getStyleClass().add("active");
            else if (fxml.endsWith("VerifikasiPengajuan.fxml")) btnVerifikasiPupuk.getStyleClass().add("active");
            else if (fxml.endsWith("HasilVerifikasi.fxml")) btnHasil.getStyleClass().add("active");
            else if (fxml.endsWith("ProfilPetugas.fxml")) btnProfil.getStyleClass().add("active");
        }
    }

    @FXML
    private void goToDashboard() {
        NavigationManager.navigateToDashboardPetugas();
    }

    @FXML
    private void goToVerifikasiPengajuan() {
        NavigationManager.navigateToVerifikasiPengajuan();
    }

    @FXML
    private void goToVerifikasiLaporan() {
        NavigationManager.navigateToVerifikasiGagalPanen();
    }

    @FXML
    private void goToHasilVerifikasi() {
        NavigationManager.navigateToHasilVerifikasi();
    }

    @FXML
    private void goToProfil() {
        NavigationManager.navigateToProfil();
    }

    @FXML
    private void handleLogout() {
        NavigationManager.navigateToLogin();
    }
}
