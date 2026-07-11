package com.datani.datastructure;

/**
 * Implementasi Struktur Data Tumpukan (Stack) - LIFO
 * Tugas Individu Anggota 2
 */
public class TumpukanNavigasi {
    
    // Inner class untuk Simpul (Node)
    private static class NodeString {
        String namaSceneFxml;
        NodeString halamanSebelumnya;

        NodeString(String namaSceneFxml) {
            this.namaSceneFxml = namaSceneFxml;
            this.halamanSebelumnya = null;
        }
    }

    private NodeString atas;
    private int ukuran;

    public TumpukanNavigasi() {
        this.atas = null;
        this.ukuran = 0;
    }

    // Push
    public void bukaHalamanBaru(String fxml) {
        NodeString nodeBaru = new NodeString(fxml);
        nodeBaru.halamanSebelumnya = atas;
        atas = nodeBaru;
        ukuran++;
    }

    // Pop
    public String kembaliKeHalamanSebelumnya() {
        if (atas == null) {
            return null;
        }
        String data = atas.namaSceneFxml;
        atas = atas.halamanSebelumnya;
        ukuran--;
        return data;
    }

    // Peek
    public String lihatHalamanSaatIni() {
        if (atas == null) {
            return null;
        }
        return atas.namaSceneFxml;
    }

    public boolean isKosong() {
        return atas == null;
    }

    public int getUkuran() {
        return ukuran;
    }
}
