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

    @FXML
    private void initialize() {
        if (UserSession.getCurrentUser() != null) {
            userNameLabel.setText(UserSession.getCurrentUser().getFullName());
        }
        userRoleLabel.setText("Petugas BPP");
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
