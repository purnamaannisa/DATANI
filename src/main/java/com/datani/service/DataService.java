package com.datani.service;

import com.datani.model.LaporanGagalPanen;
import com.datani.model.Pengajuan;
import com.datani.model.Petani;
import com.datani.model.Role;
import com.datani.model.StatusPengajuan;
import com.datani.model.User;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.security.AnyTypePermission;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Sumber data berbasis XML menggunakan XStream untuk persistensi.
 */
public final class DataService {

    private static final ObservableList<User> USERS = FXCollections.observableArrayList();
    private static final ObservableList<Petani> PETANI_LIST = FXCollections.observableArrayList();
    private static final ObservableList<Pengajuan> PENGAJUAN_LIST = FXCollections.observableArrayList();
    private static final ObservableList<LaporanGagalPanen> GAGAL_PANEN_LIST = FXCollections.observableArrayList();

    private static final String DATA_DIR = "data";
    private static final XStream xstream = new XStream();

    private static int nextUserId = 1;
    private static int nextPetaniId = 1;
    private static int nextPengajuanId = 1;

    static {
        xstream.addPermission(AnyTypePermission.ANY);
        loadAllData();
    }

    private DataService() {
    }

    private static void saveToXml(Object list, String filename) {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
            String xml = xstream.toXML(list);
            try (PrintWriter out = new PrintWriter(new File(DATA_DIR, filename))) {
                out.println(xml);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Object loadFromXml(String filename) {
        File file = new File(DATA_DIR, filename);
        if (!file.exists()) {
            return null;
        }
        try {
            String xml = new String(Files.readAllBytes(file.toPath()));
            return xstream.fromXML(xml);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static void loadAllData() {
        // 1. Load Users
        List<User> loadedUsers = (List<User>) loadFromXml("users.xml");
        if (loadedUsers != null) {
            USERS.clear();
            USERS.addAll(loadedUsers);
            nextUserId = USERS.stream().mapToInt(User::getId).max().orElse(0) + 1;
        } else {
            USERS.add(User.petugas(1, "petugas", "petugas123", "Petugas BPP", "petugas.bpp@bpp.go.id", "081200000001"));
            nextUserId = 2;
            saveToXml(new ArrayList<>(USERS), "users.xml");
        }

        // 2. Load Petani
        List<Petani> loadedPetani = (List<Petani>) loadFromXml("petani.xml");
        if (loadedPetani != null) {
            PETANI_LIST.clear();
            PETANI_LIST.addAll(loadedPetani);
            nextPetaniId = PETANI_LIST.stream().mapToInt(Petani::getId).max().orElse(0) + 1;
        } else {
            registerPetaniInternal("3201012501900002", "petani123", "Budi Santoso", "3201011234560001", "Desa Sukamaju, Bogor", "081300000002", "Tani Makmur 1");
            registerPetaniInternal("3309022803880003", "petani123", "Wayan Sudarma", "3309021234560002", "Desa Tegal Rejo, Klaten", "081300000003", "Tani Sejahtera");
            registerPetaniInternal("3215017004950004", "petani123", "Siti Aminah", "3215011234560003", "Desa Cikampek, Karawang", "081300000004", "Tani Makmur 2");
            saveToXml(new ArrayList<>(PETANI_LIST), "petani.xml");
            saveToXml(new ArrayList<>(USERS), "users.xml");
        }

        // 3. Load Pengajuan
        List<Pengajuan> loadedPengajuan = (List<Pengajuan>) loadFromXml("pengajuan.xml");
        if (loadedPengajuan != null) {
            PENGAJUAN_LIST.clear();
            PENGAJUAN_LIST.addAll(loadedPengajuan);
            nextPengajuanId = PENGAJUAN_LIST.stream().mapToInt(Pengajuan::getId).max().orElse(0) + 1;
        } else {
            Petani budi = getPetaniByNik("3201012501900002").get();
            Petani wayan = getPetaniByNik("3309022803880003").get();
            Petani aminah = getPetaniByNik("3215017004950004").get();
            nextPengajuanId = 1;
            PENGAJUAN_LIST.add(new Pengajuan(nextPengajuanId++, budi.getId(), 1.5, "Milik Sendiri", "Padi", "sample-photos/lahan_budi_1.jpg", StatusPengajuan.DISETUJUI, null, LocalDate.now().minusDays(10), LocalDate.now().minusDays(8)));
            PENGAJUAN_LIST.add(new Pengajuan(nextPengajuanId++, budi.getId(), 0.8, "Sewa", "Jagung", "sample-photos/lahan_budi_2.jpg", StatusPengajuan.MENUNGGU_VERIFIKASI, null, LocalDate.now().minusDays(3), null));
            PENGAJUAN_LIST.add(new Pengajuan(nextPengajuanId++, wayan.getId(), 2.2, "Milik Sendiri", "Tebu", "sample-photos/lahan_wayan_1.jpg", StatusPengajuan.MENUNGGU_VERIFIKASI, null, LocalDate.now().minusDays(7), null));
            PENGAJUAN_LIST.add(new Pengajuan(nextPengajuanId++, aminah.getId(), 1.0, "Milik Sendiri", "Kedelai", "sample-photos/lahan_aminah_1.jpg", StatusPengajuan.DITOLAK, "Foto bukti tidak jelas.", LocalDate.now().minusDays(1), LocalDate.now()));
            saveToXml(new ArrayList<>(PENGAJUAN_LIST), "pengajuan.xml");
        }

        // 4. Load Gagal Panen
        List<LaporanGagalPanen> loadedGagal = (List<LaporanGagalPanen>) loadFromXml("gagal_panen.xml");
        if (loadedGagal != null) {
            GAGAL_PANEN_LIST.clear();
            GAGAL_PANEN_LIST.addAll(loadedGagal);
        } else {
            Petani budi = getPetaniByNik("3201012501900002").get();
            LaporanGagalPanen l = new LaporanGagalPanen(1, budi.getId(), budi.getNamaLengkap(), "Hama/Penyakit", 0.5, 70, LocalDate.now().minusDays(5), "sample-photos/damage_budi_1.jpg");
            l.setStatus("Terverifikasi");
            l.setCatatanRekomendasi("Disetujui untuk alokasi pupuk darurat.");
            l.setTanggalVerifikasi(LocalDate.now().minusDays(4));
            l.setVerifikatorUsername("petugas");
            GAGAL_PANEN_LIST.add(l);
            saveToXml(new ArrayList<>(GAGAL_PANEN_LIST), "gagal_panen.xml");
        }
    }

    private static void registerPetaniInternal(String nik, String password, String namaLengkap,
                                                 String nomorKK, String alamat, String nomorHP,
                                                 String kelompokTani) {
        USERS.add(new User(nextUserId++, nik, password, Role.PETANI));
        PETANI_LIST.add(new Petani(nextPetaniId++, nik, namaLengkap, nomorKK, alamat, nomorHP, kelompokTani));
    }

    // ------------------------------------------------------------------
    // Autentikasi & Registrasi
    // ------------------------------------------------------------------

    public static User authenticatePetani(String nik, String password) {
        if (nik == null || password == null) {
            return null;
        }
        for (User user : USERS) {
            if (user.getRole() == Role.PETANI
                    && user.getNik() != null
                    && user.getNik().equalsIgnoreCase(nik.trim())
                    && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    public static User authenticatePetugas(String username, String password) {
        if (username == null || password == null) {
            return null;
        }
        for (User user : USERS) {
            if (user.getRole() == Role.PETUGAS_BPP
                    && user.getUsername() != null
                    && user.getUsername().equalsIgnoreCase(username.trim())
                    && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    public static boolean isNikTerdaftar(String nik) {
        if (nik == null) {
            return false;
        }
        return USERS.stream().anyMatch(u -> u.getNik() != null && u.getNik().equalsIgnoreCase(nik.trim()));
    }

    public static synchronized void registerPetani(String nik, String password, String namaLengkap,
                                                     String nomorKK, String alamat, String nomorHP,
                                                     String kelompokTani) {
        USERS.add(new User(nextUserId++, nik, password, Role.PETANI));
        PETANI_LIST.add(new Petani(nextPetaniId++, nik, namaLengkap, nomorKK, alamat, nomorHP, kelompokTani));
        saveToXml(new ArrayList<>(USERS), "users.xml");
        saveToXml(new ArrayList<>(PETANI_LIST), "petani.xml");
    }

    // ------------------------------------------------------------------
    // Petani
    // ------------------------------------------------------------------

    public static ObservableList<Petani> getAllPetani() {
        return PETANI_LIST;
    }

    public static Optional<Petani> getPetaniByNik(String nik) {
        if (nik == null) {
            return Optional.empty();
        }
        return PETANI_LIST.stream().filter(p -> p.getNik().equalsIgnoreCase(nik.trim())).findFirst();
    }

    public static Optional<Petani> getPetaniById(int id) {
        return PETANI_LIST.stream().filter(p -> p.getId() == id).findFirst();
    }

    public static String getNamaPetaniById(int id) {
        return getPetaniById(id).map(Petani::getNamaLengkap).orElse("Petani Tidak Diketahui");
    }

    public static String getNikPetaniById(int id) {
        return getPetaniById(id).map(Petani::getNik).orElse("-");
    }

    // ------------------------------------------------------------------
    // Pengajuan
    // ------------------------------------------------------------------

    public static ObservableList<Pengajuan> getAllPengajuan() {
        return PENGAJUAN_LIST;
    }

    public static ObservableList<Pengajuan> getPengajuanByPetaniId(int petaniId) {
        List<Pengajuan> filtered = new ArrayList<>();
        for (Pengajuan p : PENGAJUAN_LIST) {
            if (p.getPetaniId() == petaniId) {
                filtered.add(p);
            }
        }
        return FXCollections.observableArrayList(filtered);
    }

    public static ObservableList<Pengajuan> getPengajuanMenungguVerifikasi() {
        List<Pengajuan> filtered = new ArrayList<>();
        for (Pengajuan p : PENGAJUAN_LIST) {
            if (p.getStatus() == StatusPengajuan.MENUNGGU_VERIFIKASI) {
                filtered.add(p);
            }
        }
        return FXCollections.observableArrayList(filtered);
    }

    public static ObservableList<Pengajuan> getPengajuanTerverifikasi(StatusPengajuan filter) {
        List<Pengajuan> filtered = new ArrayList<>();
        for (Pengajuan p : PENGAJUAN_LIST) {
            boolean sudahDiverifikasi = p.getStatus() == StatusPengajuan.DISETUJUI
                    || p.getStatus() == StatusPengajuan.DITOLAK;
            if (!sudahDiverifikasi) {
                continue;
            }
            if (filter == null || p.getStatus() == filter) {
                filtered.add(p);
            }
        }
        return FXCollections.observableArrayList(filtered);
    }

    public static Optional<Pengajuan> getPengajuanById(int id) {
        return PENGAJUAN_LIST.stream().filter(p -> p.getId() == id).findFirst();
    }

    public static synchronized int getNextPengajuanId() {
        int nextId = nextPengajuanId;
        nextPengajuanId++;
        return nextId;
    }

    public static void addPengajuan(Pengajuan pengajuan) {
        PENGAJUAN_LIST.add(pengajuan);
        saveToXml(new ArrayList<>(PENGAJUAN_LIST), "pengajuan.xml");
    }

    public static void setujuiPengajuan(int pengajuanId) {
        getPengajuanById(pengajuanId).ifPresent(p -> {
            p.setStatus(StatusPengajuan.DISETUJUI);
            p.setAlasanPenolakan(null);
            p.setTanggalVerifikasi(LocalDate.now());
        });
        saveToXml(new ArrayList<>(PENGAJUAN_LIST), "pengajuan.xml");
    }

    public static void tolakPengajuan(int pengajuanId, String alasanPenolakan) {
        getPengajuanById(pengajuanId).ifPresent(p -> {
            p.setStatus(StatusPengajuan.DITOLAK);
            p.setAlasanPenolakan(alasanPenolakan);
            p.setTanggalVerifikasi(LocalDate.now());
        });
        saveToXml(new ArrayList<>(PENGAJUAN_LIST), "pengajuan.xml");
    }

    // ------------------------------------------------------------------
    // Gagal Panen
    // ------------------------------------------------------------------

    public static ObservableList<LaporanGagalPanen> getAllLaporanGagalPanen() {
        return GAGAL_PANEN_LIST;
    }

    public static ObservableList<LaporanGagalPanen> getLaporanGagalPanenByPetani(int petaniId) {
        ObservableList<LaporanGagalPanen> result = FXCollections.observableArrayList();
        for (LaporanGagalPanen l : GAGAL_PANEN_LIST) {
            if (l.getPetaniId() == petaniId) {
                result.add(l);
            }
        }
        return result;
    }

    public static void addLaporanGagalPanen(LaporanGagalPanen laporan) {
        GAGAL_PANEN_LIST.add(laporan);
        saveToXml(new ArrayList<>(GAGAL_PANEN_LIST), "gagal_panen.xml");
    }

    public static void updateLaporanGagalPanen(LaporanGagalPanen laporan) {
        for (int i = 0; i < GAGAL_PANEN_LIST.size(); i++) {
            if (GAGAL_PANEN_LIST.get(i).getId() == laporan.getId()) {
                GAGAL_PANEN_LIST.set(i, laporan);
                break;
            }
        }
        saveToXml(new ArrayList<>(GAGAL_PANEN_LIST), "gagal_panen.xml");
    }

    public static int getNextGagalPanenId() {
        return GAGAL_PANEN_LIST.stream().mapToInt(LaporanGagalPanen::getId).max().orElse(0) + 1;
    }
}
