package com.datani.navigation;

import com.datani.datastructure.TumpukanNavigasi;
import com.datani.model.Role;
import com.datani.model.User;
import com.datani.session.UserSession;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

/**
 * Pusat semua navigasi layar aplikasi DATANI.
 * <p>
 * Hanya kelas ini yang boleh mengetahui lokasi berkas FXML - controller
 * tidak pernah memuat FXML secara langsung, melainkan memanggil salah satu
 * method {@code navigateTo...} di bawah ini. Dengan begitu, routing
 * berbasis peran (Petani vs Petugas BPP) terpusat di satu tempat yang
 * mudah diaudit, bukan tersebar di banyak controller.
 * <p>
 * Aplikasi ini TIDAK memiliki halaman Dashboard.
 */
public final class NavigationManager {

    private static final String FXML_BASE = "/com/datani/fxml/";
    private static final String CSS_PATH = "/com/datani/css/styles.css";
    private static final String APP_TITLE = "DATANI - Sistem Informasi Data Petani";

    private static Stage primaryStage;
    
    // ASD Task 3: Tumpukan Navigasi
    private static final TumpukanNavigasi TUMPUKAN_NAVIGASI = new TumpukanNavigasi();
    private static String currentFxml = null;

    private NavigationManager() {
    }

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    // ------------------------------------------------------------------
    // Autentikasi
    // ------------------------------------------------------------------

    public static void navigateToLogin() {
        UserSession.clear();
        loadScene(FXML_BASE + "login/Login.fxml", APP_TITLE + " - Login");
    }

    public static void navigateToRegister() {
        loadScene(FXML_BASE + "login/Register.fxml", APP_TITLE + " - Registrasi Petani");
    }

    public static void navigateToLogout() {
        navigateToLogin();
    }

    /**
     * Dipanggil oleh LoginController setelah autentikasi berhasil.
     * Mengarahkan Petani ke Dashboard Petani dan Petugas BPP ke Dashboard Petugas BPP.
     */
    public static void navigateAfterLogin() {
        User user = UserSession.getCurrentUser();
        if (user == null) {
            navigateToLogin();
            return;
        }
        if (user.getRole() == Role.PETUGAS_BPP) {
            navigateToDashboardPetugas();
        } else {
            navigateToDashboardPetani();
        }
    }

    // ------------------------------------------------------------------
    // Petani
    // ------------------------------------------------------------------

    public static void navigateToDashboardPetani() {
        requireRole(Role.PETANI);
        loadScene(FXML_BASE + "petani/DashboardPetani.fxml", APP_TITLE + " - Dasbor Petani");
    }

    public static void navigateToPengajuanPupuk() {
        requireRole(Role.PETANI);
        loadScene(FXML_BASE + "petani/PengajuanPupuk.fxml", APP_TITLE + " - Pengajuan Pupuk Subsidi");
    }

    public static void navigateToLaporGagalPanen() {
        requireRole(Role.PETANI);
        loadScene(FXML_BASE + "petani/LaporGagalPanen.fxml", APP_TITLE + " - Lapor Gagal Panen");
    }

    public static void navigateToStatusPengajuan() {
        requireRole(Role.PETANI);
        loadScene(FXML_BASE + "petani/StatusPengajuan.fxml", APP_TITLE + " - Status Pengajuan");
    }

    // ------------------------------------------------------------------
    // Petugas BPP
    // ------------------------------------------------------------------

    public static void navigateToDashboardPetugas() {
        requireRole(Role.PETUGAS_BPP);
        loadScene(FXML_BASE + "petugas/DashboardPetugas.fxml", APP_TITLE + " - Dasbor Petugas BPP");
    }

    public static void navigateToVerifikasiPengajuan() {
        requireRole(Role.PETUGAS_BPP);
        loadScene(FXML_BASE + "petugas/VerifikasiPengajuan.fxml", APP_TITLE + " - Verifikasi Pengajuan");
    }

    public static void navigateToVerifikasiGagalPanen() {
        requireRole(Role.PETUGAS_BPP);
        loadScene(FXML_BASE + "petugas/VerifikasiLaporan.fxml", APP_TITLE + " - Verifikasi Gagal Panen");
    }

    public static void navigateToHasilVerifikasi() {
        requireRole(Role.PETUGAS_BPP);
        loadScene(FXML_BASE + "petugas/HasilVerifikasi.fxml", APP_TITLE + " - Hasil Verifikasi");
    }

    // ------------------------------------------------------------------
    // Bersama (diselesaikan berdasarkan peran)
    // ------------------------------------------------------------------

    /**
     * Profil adalah layar yang dimiliki kedua peran, tetapi masing-masing
     * peran mendapat FXML tersendiri (dengan sidebar-nya sendiri) - tidak
     * pernah berbagi satu Sidebar.fxml. Method ini menentukan FXML mana
     * yang dimuat berdasarkan peran pengguna yang sedang login, sehingga
     * setiap controller cukup memanggil satu method
     * {@code NavigationManager.navigateToProfil()} tanpa peduli perannya.
     */
    public static void navigateToProfil() {
        User user = UserSession.getCurrentUser();
        if (user == null) {
            navigateToLogin();
            return;
        }
        if (user.getRole() == Role.PETUGAS_BPP) {
            loadScene(FXML_BASE + "petugas/ProfilPetugas.fxml", APP_TITLE + " - Profil");
        } else {
            loadScene(FXML_BASE + "petani/ProfilPetani.fxml", APP_TITLE + " - Profil");
        }
    }

    // ------------------------------------------------------------------
    // Internal
    // ------------------------------------------------------------------

    private static void requireRole(Role required) {
        User user = UserSession.getCurrentUser();
        if (user == null || user.getRole() != required) {
            throw new IllegalStateException(
                    "Navigasi diblokir: layar ini membutuhkan peran " + required
                            + " tetapi pengguna saat ini adalah " + (user == null ? "belum login" : user.getRole()));
        }
    }

    public static void kembaliKeHalamanSebelumnya() {
        if (!TUMPUKAN_NAVIGASI.isKosong()) {
            String fxmlLama = TUMPUKAN_NAVIGASI.kembaliKeHalamanSebelumnya();
            currentFxml = null; // bypass pushing
            loadScene(fxmlLama, APP_TITLE + " - Kembali");
        }
    }

    private static void loadScene(String fxmlPath, String title) {
        try {
            // ASD Task 3: Simpan riwayat
            if (currentFxml != null && (TUMPUKAN_NAVIGASI.isKosong() || !Objects.equals(TUMPUKAN_NAVIGASI.lihatHalamanSaatIni(), currentFxml))) {
                if (currentFxml != null) {
                    TUMPUKAN_NAVIGASI.bukaHalamanBaru(currentFxml);
                }
            }
            currentFxml = fxmlPath;

            URL fxmlUrl = Objects.requireNonNull(NavigationManager.class.getResource(fxmlPath),
                    "FXML tidak ditemukan pada classpath: " + fxmlPath);
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            double width = primaryStage.getScene() != null ? primaryStage.getScene().getWidth() : 1366;
            double height = primaryStage.getScene() != null ? primaryStage.getScene().getHeight() : 768;

            Scene scene = new Scene(root, width, height);
            URL cssUrl = NavigationManager.class.getResource(CSS_PATH);
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }

            primaryStage.setScene(scene);
            primaryStage.setTitle(title);
            if (!primaryStage.isShowing()) {
                primaryStage.show();
            }
        } catch (IOException e) {
            throw new RuntimeException("Gagal memuat layar: " + fxmlPath, e);
        }
    }
}
