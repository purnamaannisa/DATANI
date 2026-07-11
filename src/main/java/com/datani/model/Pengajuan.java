package com.datani.model;

import java.time.LocalDate;

/**
 * Representasi satu Pengajuan Pupuk Subsidi yang dibuat oleh seorang Petani.
 */
public class Pengajuan {

    private int id;
    private int petaniId;
    private double luasLahan;
    private String statusKepemilikan;
    private String jenisTanaman;
    private String fotoLahan;
    private StatusPengajuan status;
    private String alasanPenolakan;
    private LocalDate tanggalPengajuan;
    private LocalDate tanggalVerifikasi;

    public Pengajuan() {
    }

    public Pengajuan(int id, int petaniId, double luasLahan, String statusKepemilikan,
                      String jenisTanaman, String fotoLahan, StatusPengajuan status,
                      String alasanPenolakan, LocalDate tanggalPengajuan, LocalDate tanggalVerifikasi) {
        this.id = id;
        this.petaniId = petaniId;
        this.luasLahan = luasLahan;
        this.statusKepemilikan = statusKepemilikan;
        this.jenisTanaman = jenisTanaman;
        this.fotoLahan = fotoLahan;
        this.status = status;
        this.alasanPenolakan = alasanPenolakan;
        this.tanggalPengajuan = tanggalPengajuan;
        this.tanggalVerifikasi = tanggalVerifikasi;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPetaniId() {
        return petaniId;
    }

    public void setPetaniId(int petaniId) {
        this.petaniId = petaniId;
    }

    public double getLuasLahan() {
        return luasLahan;
    }

    public void setLuasLahan(double luasLahan) {
        this.luasLahan = luasLahan;
    }

    public String getStatusKepemilikan() {
        return statusKepemilikan;
    }

    public void setStatusKepemilikan(String statusKepemilikan) {
        this.statusKepemilikan = statusKepemilikan;
    }

    public String getJenisTanaman() {
        return jenisTanaman;
    }

    public void setJenisTanaman(String jenisTanaman) {
        this.jenisTanaman = jenisTanaman;
    }

    public String getFotoLahan() {
        return fotoLahan;
    }

    public void setFotoLahan(String fotoLahan) {
        this.fotoLahan = fotoLahan;
    }

    public StatusPengajuan getStatus() {
        return status;
    }

    public void setStatus(StatusPengajuan status) {
        this.status = status;
    }

    public String getAlasanPenolakan() {
        return alasanPenolakan;
    }

    public void setAlasanPenolakan(String alasanPenolakan) {
        this.alasanPenolakan = alasanPenolakan;
    }

    public LocalDate getTanggalPengajuan() {
        return tanggalPengajuan;
    }

    public void setTanggalPengajuan(LocalDate tanggalPengajuan) {
        this.tanggalPengajuan = tanggalPengajuan;
    }

    public LocalDate getTanggalVerifikasi() {
        return tanggalVerifikasi;
    }

    public void setTanggalVerifikasi(LocalDate tanggalVerifikasi) {
        this.tanggalVerifikasi = tanggalVerifikasi;
    }

    @Override
    public String toString() {
        return "Pengajuan{id=" + id + ", petaniId=" + petaniId + ", status=" + status + "}";
    }
}
