package com.datani.session;

import com.datani.model.Petani;
import com.datani.model.Role;
import com.datani.model.User;

/**
 * Menyimpan pengguna yang sedang login selama aplikasi berjalan.
 * Semua keputusan berbasis peran (menu mana yang tampil, data siapa yang
 * ditampilkan, dsb.) membaca dari kelas ini.
 * <p>
 * Untuk pengguna dengan role {@link Role#PETANI}, data pribadi lengkap
 * ({@link Petani}) turut disimpan di sini agar layar "Pengajuan Pupuk
 * Subsidi" dapat memuat data tersebut secara otomatis tanpa Petani perlu
 * mengetik ulang data pribadinya.
 */
public final class UserSession {

    private static User currentUser;
    private static Petani currentPetani;

    private UserSession() {
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentPetani(Petani petani) {
        currentPetani = petani;
    }

    public static Petani getCurrentPetani() {
        return currentPetani;
    }

    public static void clear() {
        currentUser = null;
        currentPetani = null;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static boolean isPetugasBPP() {
        return currentUser != null && currentUser.getRole() == Role.PETUGAS_BPP;
    }

    public static boolean isPetani() {
        return currentUser != null && currentUser.getRole() == Role.PETANI;
    }
}
