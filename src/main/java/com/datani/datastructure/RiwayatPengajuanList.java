package com.datani.datastructure;

import com.datani.model.Pengajuan;

/**
 * Implementasi Struktur Data Doubly Linked List
 * Tugas Individu Anggota 3
 */
public class RiwayatPengajuanList {
    
    // Inner class untuk Simpul (Node) pointer ganda
    public static class NodeRiwayat {
        public Pengajuan dataPengajuan;
        public NodeRiwayat riwayatSebelumnya;
        public NodeRiwayat riwayatSelanjutnya;

        NodeRiwayat(Pengajuan data) {
            this.dataPengajuan = data;
            this.riwayatSebelumnya = null;
            this.riwayatSelanjutnya = null;
        }
    }

    private NodeRiwayat riwayatPertama;
    private NodeRiwayat riwayatTerbaru;
    private int jumlah;

    public RiwayatPengajuanList() {
        this.riwayatPertama = null;
        this.riwayatTerbaru = null;
        this.jumlah = 0;
    }

    public void tambahRiwayatBaru(Pengajuan pengajuan) {
        NodeRiwayat nodeBaru = new NodeRiwayat(pengajuan);
        if (riwayatPertama == null) {
            riwayatPertama = nodeBaru;
            riwayatTerbaru = nodeBaru;
        } else {
            riwayatTerbaru.riwayatSelanjutnya = nodeBaru;
            nodeBaru.riwayatSebelumnya = riwayatTerbaru;
            riwayatTerbaru = nodeBaru;
        }
        jumlah++;
    }
    
    public NodeRiwayat getRiwayatPertama() {
        return riwayatPertama;
    }
    
    public NodeRiwayat getRiwayatTerbaru() {
        return riwayatTerbaru;
    }

    public int getJumlahRiwayat() {
        return jumlah;
    }
}
