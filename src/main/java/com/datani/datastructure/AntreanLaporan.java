package com.datani.datastructure;

import com.datani.model.LaporanGagalPanen;

/**
 * Implementasi Struktur Data Antrean (Queue) - FIFO
 * Tugas Individu Anggota 1
 */
public class AntreanLaporan {
    
    // Inner class untuk Simpul (Node)
    private static class NodeLaporan {
        LaporanGagalPanen data;
        NodeLaporan berikutnya;

        NodeLaporan(LaporanGagalPanen data) {
            this.data = data;
            this.berikutnya = null;
        }
    }

    private NodeLaporan terdepan;
    private NodeLaporan terakhir;
    private int jumlah;

    public AntreanLaporan() {
        this.terdepan = null;
        this.terakhir = null;
        this.jumlah = 0;
    }

    // Enqueue
    public void tambahAntrean(LaporanGagalPanen laporan) {
        NodeLaporan nodeBaru = new NodeLaporan(laporan);
        if (terakhir == null) {
            terdepan = nodeBaru;
            terakhir = nodeBaru;
        } else {
            terakhir.berikutnya = nodeBaru;
            terakhir = nodeBaru;
        }
        jumlah++;
    }

    // Dequeue
    public LaporanGagalPanen prosesPengajuanDepan() {
        if (terdepan == null) {
            return null;
        }
        LaporanGagalPanen data = terdepan.data;
        terdepan = terdepan.berikutnya;
        if (terdepan == null) {
            terakhir = null;
        }
        jumlah--;
        return data;
    }

    // Peek
    public LaporanGagalPanen lihatAntreanDepan() {
        if (terdepan == null) {
            return null;
        }
        return terdepan.data;
    }

    public boolean isKosong() {
        return terdepan == null;
    }

    public int getJumlahAntrean() {
        return jumlah;
    }
}
