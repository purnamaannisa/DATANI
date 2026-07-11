package com.datani.datastructure;

import com.datani.model.Petani;

/**
 * Implementasi Struktur Data Binary Search Tree (BST)
 * Tugas Individu Anggota 4
 */
public class PohonPetani {
    
    // Inner class untuk Simpul (Node) Pohon
    private static class NodePetani {
        Petani dataPetani;
        NodePetani cabangKiri;
        NodePetani cabangKanan;

        NodePetani(Petani data) {
            this.dataPetani = data;
            this.cabangKiri = null;
            this.cabangKanan = null;
        }
    }

    private NodePetani akarPohon;

    public PohonPetani() {
        this.akarPohon = null;
    }

    // Insert data (NIK sebagai key untuk komparasi string)
    public void sisipkanPetaniBaru(Petani petani) {
        akarPohon = sisipkanRekursif(akarPohon, petani);
    }

    private NodePetani sisipkanRekursif(NodePetani node, Petani petani) {
        if (node == null) {
            return new NodePetani(petani);
        }

        int komparasi = petani.getNik().compareTo(node.dataPetani.getNik());

        if (komparasi < 0) {
            node.cabangKiri = sisipkanRekursif(node.cabangKiri, petani);
        } else if (komparasi > 0) {
            node.cabangKanan = sisipkanRekursif(node.cabangKanan, petani);
        } else {
            // NIK sudah ada, update atau biarkan.
            // Dalam kasus ini kita biarkan (tidak boleh duplikat).
        }
        return node;
    }

    // Cari berdasarkan NIK O(log n)
    public Petani cariBerdasarkanNIK(String nik) {
        NodePetani hasil = cariRekursif(akarPohon, nik);
        if (hasil != null) {
            return hasil.dataPetani;
        }
        return null;
    }

    private NodePetani cariRekursif(NodePetani node, String nik) {
        if (node == null) {
            return null;
        }
        
        int komparasi = nik.compareTo(node.dataPetani.getNik());
        
        if (komparasi == 0) {
            return node;
        }
        
        if (komparasi < 0) {
            return cariRekursif(node.cabangKiri, nik);
        } else {
            return cariRekursif(node.cabangKanan, nik);
        }
    }
}
