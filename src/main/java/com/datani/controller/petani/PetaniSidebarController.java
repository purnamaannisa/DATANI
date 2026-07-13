package com.datani.controller.petani;

import com.datani.navigation.NavigationManager;
import com.datani.session.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Controller untuk PetaniSidebar.fxml.
 * Hanya disertakan pada layar milik Petani. Menu yang tersedia persis
 * seperti spesifikasi: Pengajuan Pupuk Subsidi, Status Pengajuan, Profil,
 * Logout. Tidak ada menu Dashboard.
 */
public class PetaniSidebarController {

    @FXML
    private Label userNameLabel;

    @FXML
    private Label userRoleLabel;

    @FXML private javafx.scene.control.Button btnDashboard;
    @FXML private javafx.scene.control.Button btnPengajuan;
    @FXML private javafx.scene.control.Button btnLapor;
    @FXML private javafx.scene.control.Button btnStatus;
    @FXML private javafx.scene.control.Button btnProfil;

    @FXML
    private void initialize() {
        if (UserSession.getCurrentPetani() != null) {
            userNameLabel.setText(UserSession.getCurrentPetani().getNamaLengkap());
        }
        userRoleLabel.setText("Petani");

        String fxml = NavigationManager.getCurrentFxml();
        if (fxml != null) {
            if (fxml.endsWith("DashboardPetani.fxml")) btnDashboard.getStyleClass().add("active");
            else if (fxml.endsWith("PengajuanPupuk.fxml")) btnPengajuan.getStyleClass().add("active");
            else if (fxml.endsWith("LaporGagalPanen.fxml")) btnLapor.getStyleClass().add("active");
            else if (fxml.endsWith("StatusPengajuan.fxml")) btnStatus.getStyleClass().add("active");
            else if (fxml.endsWith("ProfilPetani.fxml")) btnProfil.getStyleClass().add("active");
        }
    }

    @FXML
    private void goToDashboard() {
        NavigationManager.navigateToDashboardPetani();
    }

    @FXML
    private void goToPengajuanPupuk() {
        NavigationManager.navigateToPengajuanPupuk();
    }

    @FXML
    private void goToLaporGagalPanen() {
        NavigationManager.navigateToLaporGagalPanen();
    }

    @FXML
    private void goToStatusPengajuan() {
        NavigationManager.navigateToStatusPengajuan();
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
