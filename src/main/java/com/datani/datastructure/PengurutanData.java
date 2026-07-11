package com.datani.datastructure;

import java.util.List;
import com.datani.model.LaporanGagalPanen;

/**
 * Implementasi Algoritma Pengurutan (Quick Sort)
 * Tugas Individu Anggota 5
 */
public class PengurutanData {

    /**
     * Mengurutkan daftar LaporanGagalPanen berdasarkan persentase kerusakan (Descending: Parah ke Ringan)
     */
    public static void quickSortKerusakan(List<LaporanGagalPanen> data, int kiri, int kanan) {
        if (kiri < kanan) {
            int indeksPartisi = partisi(data, kiri, kanan);
            
            // Rekursif mengurutkan elemen sebelum dan sesudah partisi
            quickSortKerusakan(data, kiri, indeksPartisi - 1);
            quickSortKerusakan(data, indeksPartisi + 1, kanan);
        }
    }

    private static int partisi(List<LaporanGagalPanen> data, int kiri, int kanan) {
        // Pilih elemen paling kanan sebagai pivot
        double pivot = data.get(kanan).getPersentaseKerusakan();
        
        int i = (kiri - 1); // Indeks elemen yang lebih besar
        
        for (int j = kiri; j < kanan; j++) {
            // Jika elemen saat ini lebih besar atau sama dengan pivot (untuk descending)
            if (data.get(j).getPersentaseKerusakan() >= pivot) {
                i++;
                
                // Tukar data[i] dan data[j]
                LaporanGagalPanen temp = data.get(i);
                data.set(i, data.get(j));
                data.set(j, temp);
            }
        }
        
        // Tukar elemen data[i+1] dengan pivot (data[kanan])
        LaporanGagalPanen temp = data.get(i + 1);
        data.set(i + 1, data.get(kanan));
        data.set(kanan, temp);
        
        return i + 1;
    }
}
