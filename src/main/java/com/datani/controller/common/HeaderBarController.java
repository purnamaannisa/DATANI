package com.datani.controller.common;

import com.datani.model.Role;
import com.datani.model.User;
import com.datani.session.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Controller untuk HeaderBar.fxml - header profesional yang tampil di
 * bagian atas setiap halaman konten (Petani maupun Petugas BPP), berisi
 * nama aplikasi serta identitas singkat pengguna yang sedang login.
 * <p>
 * Komponen ini digunakan bersama (shared) oleh kedua peran, berbeda dari
 * Sidebar yang tetap terpisah per peran.
 */
public class HeaderBarController {

    @FXML
    private Label userInitialLabel;

    @FXML
    private Label userNameLabel;

    @FXML
    private Label userRoleLabel;

    @FXML
    private void initialize() {
        User user = UserSession.getCurrentUser();
        if (user == null) {
            return;
        }

        String displayName = resolveDisplayName(user);
        userNameLabel.setText(displayName);
        userRoleLabel.setText(user.getRole() == Role.PETUGAS_BPP ? "Petugas BPP" : "Petani");
        userInitialLabel.setText(initialOf(displayName));
    }

    private String resolveDisplayName(User user) {
        if (user.getRole() == Role.PETUGAS_BPP) {
            return user.getFullName() != null ? user.getFullName() : user.getUsername();
        }
        return UserSession.getCurrentPetani() != null
                ? UserSession.getCurrentPetani().getNamaLengkap()
                : "Petani";
    }

    private String initialOf(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "?";
        }
        return name.trim().substring(0, 1).toUpperCase();
    }
}
