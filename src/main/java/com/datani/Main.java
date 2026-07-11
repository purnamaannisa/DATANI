package com.datani;

import com.datani.navigation.NavigationManager;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Titik masuk aplikasi DATANI (Sistem Informasi Data Petani).
 * <p>
 * Aplikasi langsung membuka halaman Login. Seluruh perpindahan layar
 * berikutnya didelegasikan ke {@link NavigationManager}, yang sadar-peran
 * (role-aware) dan menentukan menu Petani atau Petugas BPP yang tampil.
 * <p>
 * Aplikasi ini TIDAK memiliki halaman Dashboard. Setelah login, Petani
 * langsung diarahkan ke "Pengajuan Pupuk Subsidi" dan Petugas BPP langsung
 * diarahkan ke "Verifikasi Pengajuan".
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        NavigationManager.setPrimaryStage(primaryStage);
        primaryStage.setResizable(true);
        primaryStage.setMinWidth(1180);
        primaryStage.setMinHeight(720);
        primaryStage.setMaximized(true);
        NavigationManager.navigateToLogin();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
