package com.datani.service;

import com.datani.datastructure.PohonPetani;
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
    private static PohonPetani pohonPetani = new PohonPetani();

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
        }
        
        if (PETANI_LIST.size() < 100) {
            java.util.Random rnd = new java.util.Random();
            String[] firstNames = {"Ahmad", "Budi", "Cipto", "Dedi", "Eko", "Fajar", "Gunawan", "Hadi", "Iwan", "Joko", "Rina", "Siti", "Lestari", "Dewi"};
            String[] lastNames = {"Santoso", "Wijaya", "Kusuma", "Nugroho", "Pratama", "Saputra", "Wibowo", "Setiawan", "Hidayat", "Baskoro", "Putri", "Sari"};
            for (int i = 0; i < 100; i++) {
                String nik = "32" + (10000000000000L + rnd.nextInt(9000000) * 1000L + i);
                String nama = firstNames[rnd.nextInt(firstNames.length)] + " " + lastNames[rnd.nextInt(lastNames.length)];
                registerPetaniInternal(nik, "petani123", nama, "32" + (10000000000000L + rnd.nextInt(9000000) * 1000L), "Desa Tani Jaya " + i, "0812" + (10000000 + rnd.nextInt(89999999)), "Kelompok " + (i%10));
            }
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
        
        // Remove specific NIK
        Optional<Petani> target = PETANI_LIST.stream().filter(p -> "1571085508070001".equals(p.getNik())).findFirst();
        if (target.isPresent()) {
            int pId = target.get().getId();
            PETANI_LIST.removeIf(p -> p.getId() == pId);
            USERS.removeIf(u -> "1571085508070001".equals(u.getNik()));
            PENGAJUAN_LIST.removeIf(p -> p.getPetaniId() == pId);
            GAGAL_PANEN_LIST.removeIf(g -> g.getPetaniId() == pId);
            saveToXml(new ArrayList<>(USERS), "users.xml");
            saveToXml(new ArrayList<>(PETANI_LIST), "petani.xml");
            saveToXml(new ArrayList<>(PENGAJUAN_LIST), "pengajuan.xml");
            saveToXml(new ArrayList<>(GAGAL_PANEN_LIST), "gagal_panen.xml");
        }

        // Pastikan tidak ada data yang luas lahannya melebihi 2.0 Hektar
        boolean adaYangDikoreksi = false;
        java.util.Random rnd = new java.util.Random();
        String[] jenisTanamanArr = {"Padi", "Jagung", "Kedelai", "Cabai", "Bawang", "Tebu", "Kopi", "Kakao"};
        String[] penyebabArr = {"Hama/Penyakit", "Banjir", "Kekeringan", "Cuaca Ekstrem", "Gagal Benih"};
        
        for (Pengajuan p : PENGAJUAN_LIST) {
            if (p.getLuasLahan() > 2.0) {
                p.setLuasLahan(2.0);
                adaYangDikoreksi = true;
            }
            // Acak ulang tanaman yang sebelumnya hanya didominasi Padi/Jagung
            if ("Padi".equals(p.getJenisTanaman()) || "Jagung".equals(p.getJenisTanaman())) {
                p.setJenisTanaman(jenisTanamanArr[rnd.nextInt(jenisTanamanArr.length)]);
                adaYangDikoreksi = true;
            }
        }
        for (LaporanGagalPanen g : GAGAL_PANEN_LIST) {
            if (g.getLuasTerdampak() > 2.0) {
                g.setLuasTerdampak(2.0);
                adaYangDikoreksi = true;
            }
            if ("Hama/Penyakit".equals(g.getPenyebab())) {
                g.setPenyebab(penyebabArr[rnd.nextInt(penyebabArr.length)]);
                adaYangDikoreksi = true;
            }
        }
        if (adaYangDikoreksi) {
            saveToXml(new ArrayList<>(PENGAJUAN_LIST), "pengajuan.xml");
            saveToXml(new ArrayList<>(GAGAL_PANEN_LIST), "gagal_panen.xml");
        }

        if (PENGAJUAN_LIST.size() < 100 && PETANI_LIST.size() >= 100) {
            for (int i = 0; i < PETANI_LIST.size(); i++) {
                Petani p = PETANI_LIST.get(i);
                if (PENGAJUAN_LIST.stream().noneMatch(peng -> peng.getPetaniId() == p.getId())) {
                    StatusPengajuan stat = (i % 3 == 0) ? StatusPengajuan.DISETUJUI : ((i % 3 == 1) ? StatusPengajuan.MENUNGGU_VERIFIKASI : StatusPengajuan.DITOLAK);
                    PENGAJUAN_LIST.add(new Pengajuan(nextPengajuanId++, p.getId(), 0.5 + rnd.nextDouble(), "Milik Sendiri", jenisTanamanArr[rnd.nextInt(jenisTanamanArr.length)], "sample-photos/dummy.jpg", stat, (stat == StatusPengajuan.DITOLAK ? "Berkas tidak lengkap" : null), LocalDate.now().minusDays(rnd.nextInt(10) + 1), stat == StatusPengajuan.MENUNGGU_VERIFIKASI ? null : LocalDate.now().minusDays(rnd.nextInt(2))));
                }
                if (i % 5 == 0 && GAGAL_PANEN_LIST.stream().noneMatch(l -> l.getPetaniId() == p.getId())) {
                    LaporanGagalPanen l = new LaporanGagalPanen(GAGAL_PANEN_LIST.size() + 1, p.getId(), p.getNamaLengkap(), penyebabArr[rnd.nextInt(penyebabArr.length)], 0.2 + rnd.nextDouble(), 50 + rnd.nextInt(50), LocalDate.now().minusDays(rnd.nextInt(10) + 1), "sample-photos/damage.jpg");
                    if (i % 10 == 0) {
                        l.setStatus("Terverifikasi");
                        l.setCatatanRekomendasi("Disetujui");
                        l.setTanggalVerifikasi(LocalDate.now());
                        l.setVerifikatorUsername("petugas");
                    }
                    GAGAL_PANEN_LIST.add(l);
                }
            }
            saveToXml(new ArrayList<>(PENGAJUAN_LIST), "pengajuan.xml");
            saveToXml(new ArrayList<>(GAGAL_PANEN_LIST), "gagal_panen.xml");
        }
        
        // Build BST (Tugas ASD)
        pohonPetani = new PohonPetani();
        for (Petani p : PETANI_LIST) {
            pohonPetani.sisipkanPetaniBaru(p);
        }
    }

    private static void registerPetaniInternal(String nik, String password, String namaLengkap,
                                                 String nomorKK, String alamat, String nomorHP,
                                                 String kelompokTani) {
        USERS.add(new User(nextUserId++, nik, password, Role.PETANI));
        Petani p = new Petani(nextPetaniId++, nik, namaLengkap, nomorKK, alamat, nomorHP, kelompokTani);
        PETANI_LIST.add(p);
        pohonPetani.sisipkanPetaniBaru(p); // Insert ke BST
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
        // Menggunakan pencarian O(log n) dengan BST (Tugas ASD)
        Petani p = pohonPetani.cariBerdasarkanNIK(nik.trim());
        return Optional.ofNullable(p);
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

    public static void updatePengajuan(Pengajuan pengajuan) {
        for (int i = 0; i < PENGAJUAN_LIST.size(); i++) {
            if (PENGAJUAN_LIST.get(i).getId() == pengajuan.getId()) {
                PENGAJUAN_LIST.set(i, pengajuan);
                break;
            }
        }
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
