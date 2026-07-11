package com.datani.model;

/**
 * Status siklus hidup sebuah Pengajuan Pupuk Subsidi.
 */
public enum StatusPengajuan {
    MENUNGGU_VERIFIKASI("Menunggu Verifikasi"),
    DISETUJUI("Disetujui"),
    DITOLAK("Ditolak");

    private final String label;

    StatusPengajuan(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}
