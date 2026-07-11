package com.datani.model;

import java.time.LocalDate;

/**
 * Representasi objek data Laporan Gagal Panen.
 */
public class LaporanGagalPanen {
    private int id;
    private int petaniId;
    private String petaniNama;
    private String penyebab; // Hama/Penyakit, Kekeringan, Banjir, Bencana Alam
    private double luasTerdampak;
    private int persentaseKerusakan; // 0 - 100
    private LocalDate tanggalKejadian;
    private String fotoBukti;
    private String status; // Menunggu Peninjauan, Terverifikasi, Ditolak
    private String catatanRekomendasi;
    private LocalDate tanggalVerifikasi;
    private String verifikatorUsername;

    public LaporanGagalPanen(int id, int petaniId, String petaniNama, String penyebab, 
                              double luasTerdampak, int persentaseKerusakan, 
                              LocalDate tanggalKejadian, String fotoBukti) {
        this.id = id;
        this.petaniId = petaniId;
        this.petaniNama = petaniNama;
        this.penyebab = penyebab;
        this.luasTerdampak = luasTerdampak;
        this.persentaseKerusakan = persentaseKerusakan;
        this.tanggalKejadian = tanggalKejadian;
        this.fotoBukti = fotoBukti;
        this.status = "Menunggu Peninjauan";
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getPetaniId() { return petaniId; }
    public void setPetaniId(int petaniId) { this.petaniId = petaniId; }
    public String getPetaniNama() { return petaniNama; }
    public void setPetaniNama(String petaniNama) { this.petaniNama = petaniNama; }
    public String getPenyebab() { return penyebab; }
    public void setPenyebab(String penyebab) { this.penyebab = penyebab; }
    public double getLuasTerdampak() { return luasTerdampak; }
    public void setLuasTerdampak(double luasTerdampak) { this.luasTerdampak = luasTerdampak; }
    public int getPersentaseKerusakan() { return persentaseKerusakan; }
    public void setPersentaseKerusakan(int persentaseKerusakan) { this.persentaseKerusakan = persentaseKerusakan; }
    public LocalDate getTanggalKejadian() { return tanggalKejadian; }
    public void setTanggalKejadian(LocalDate tanggalKejadian) { this.tanggalKejadian = tanggalKejadian; }
    public String getFotoBukti() { return fotoBukti; }
    public void setFotoBukti(String fotoBukti) { this.fotoBukti = fotoBukti; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCatatanRekomendasi() { return catatanRekomendasi; }
    public void setCatatanRekomendasi(String catatanRekomendasi) { this.catatanRekomendasi = catatanRekomendasi; }
    public LocalDate getTanggalVerifikasi() { return tanggalVerifikasi; }
    public void setTanggalVerifikasi(LocalDate tanggalVerifikasi) { this.tanggalVerifikasi = tanggalVerifikasi; }
    public String getVerifikatorUsername() { return verifikatorUsername; }
    public void setVerifikatorUsername(String verifikatorUsername) { this.verifikatorUsername = verifikatorUsername; }
}
