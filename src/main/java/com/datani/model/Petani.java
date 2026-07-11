package com.datani.model;

/**
 * Data pribadi Petani yang dikumpulkan pada saat registrasi.
 * Terhubung ke {@link User} melalui NIK yang sama.
 * <p>
 * Data ini TIDAK diminta ulang saat Petani membuat Pengajuan Pupuk Subsidi -
 * data akan otomatis dimuat dari akun Petani yang sedang login.
 */
public class Petani {

    private int id;
    private String nik;
    private String namaLengkap;
    private String nomorKK;
    private String alamat;
    private String nomorHP;
    private String kelompokTani;

    public Petani() {
    }

    public Petani(int id, String nik, String namaLengkap, String nomorKK,
                  String alamat, String nomorHP, String kelompokTani) {
        this.id = id;
        this.nik = nik;
        this.namaLengkap = namaLengkap;
        this.nomorKK = nomorKK;
        this.alamat = alamat;
        this.nomorHP = nomorHP;
        this.kelompokTani = kelompokTani;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNik() {
        return nik;
    }

    public void setNik(String nik) {
        this.nik = nik;
    }

    public String getNamaLengkap() {
        return namaLengkap;
    }

    public void setNamaLengkap(String namaLengkap) {
        this.namaLengkap = namaLengkap;
    }

    public String getNomorKK() {
        return nomorKK;
    }

    public void setNomorKK(String nomorKK) {
        this.nomorKK = nomorKK;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getNomorHP() {
        return nomorHP;
    }

    public void setNomorHP(String nomorHP) {
        this.nomorHP = nomorHP;
    }

    public String getKelompokTani() {
        return kelompokTani;
    }

    public void setKelompokTani(String kelompokTani) {
        this.kelompokTani = kelompokTani;
    }

    @Override
    public String toString() {
        return "Petani{id=" + id + ", nik='" + nik + "', namaLengkap='" + namaLengkap + "'}";
    }
}
