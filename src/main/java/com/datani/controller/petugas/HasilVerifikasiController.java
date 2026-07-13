package com.datani.controller.petugas;

import com.datani.model.LaporanGagalPanen;
import com.datani.model.Pengajuan;
import com.datani.model.StatusPengajuan;
import com.datani.service.DataService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.time.format.DateTimeFormatter;

/**
 * Controller untuk HasilVerifikasi.fxml (Petugas BPP only, read-only).
 * Menampilkan seluruh pengajuan yang sudah diverifikasi (Disetujui /
 * Ditolak) dengan filter: Semua, Disetujui, Ditolak. Tidak ada tombol
 * Setujui/Tolak di layar ini; itu hanya ada di layar Verifikasi Pengajuan.
 */
public class HasilVerifikasiController {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy");

    private static final String FILTER_SEMUA = "Semua";
    private static final String FILTER_DISETUJUI = "Disetujui";
    private static final String FILTER_DITOLAK = "Ditolak";

    @FXML
    private ComboBox<String> filterCombo;

    @FXML
    private javafx.scene.control.TextField searchNikField;

    @FXML
    private TableView<Pengajuan> hasilTable;

    @FXML
    private TableColumn<Pengajuan, Number> idColumn;

    @FXML
    private TableColumn<Pengajuan, String> nikColumn;

    @FXML
    private TableColumn<Pengajuan, String> namaPetaniColumn;

    @FXML
    private TableColumn<Pengajuan, String> jenisTanamanColumn;

    @FXML
    private TableColumn<Pengajuan, String> statusColumn;

    @FXML
    private TableColumn<Pengajuan, String> tanggalVerifikasiColumn;

    @FXML
    private TableView<LaporanGagalPanen> gpTable;

    @FXML
    private TableColumn<LaporanGagalPanen, Number> gpIdColumn;

    @FXML
    private TableColumn<LaporanGagalPanen, String> gpNikColumn;

    @FXML
    private TableColumn<LaporanGagalPanen, String> gpNamaPetaniColumn;

    @FXML
    private TableColumn<LaporanGagalPanen, String> gpKerusakanColumn;

    @FXML
    private TableColumn<LaporanGagalPanen, String> gpStatusColumn;

    @FXML
    private TableColumn<LaporanGagalPanen, String> gpTanggalVerifikasiColumn;

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId()));
        nikColumn.setCellValueFactory(data -> new SimpleStringProperty(DataService.getNikPetaniById(data.getValue().getPetaniId())));
        namaPetaniColumn.setCellValueFactory(data ->
                new SimpleStringProperty(DataService.getNamaPetaniById(data.getValue().getPetaniId())));
        jenisTanamanColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getJenisTanaman()));
        statusColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus().getLabel()));
        statusColumn.setCellFactory(col -> statusStyledCell());
        tanggalVerifikasiColumn.setCellValueFactory(data -> {
            java.time.LocalDate tanggal = data.getValue().getTanggalVerifikasi();
            return new SimpleStringProperty(tanggal == null ? "-" : tanggal.format(DATE_FORMAT));
        });

        gpIdColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId()));
        gpNikColumn.setCellValueFactory(data -> new SimpleStringProperty(DataService.getNikPetaniById(data.getValue().getPetaniId())));
        gpNamaPetaniColumn.setCellValueFactory(data ->
                new SimpleStringProperty(DataService.getNamaPetaniById(data.getValue().getPetaniId())));
        gpKerusakanColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPersentaseKerusakan() + "%"));
        gpStatusColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus()));
        gpStatusColumn.setCellFactory(col -> gpStatusStyledCell());
        gpTanggalVerifikasiColumn.setCellValueFactory(data -> {
            java.time.LocalDate tanggal = data.getValue().getTanggalVerifikasi();
            return new SimpleStringProperty(tanggal == null ? "-" : tanggal.format(DATE_FORMAT));
        });

        filterCombo.setItems(FXCollections.observableArrayList(FILTER_SEMUA, FILTER_DISETUJUI, FILTER_DITOLAK));
        filterCombo.getSelectionModel().select(FILTER_SEMUA);
        filterCombo.valueProperty().addListener((obs, oldValue, newValue) -> refreshTable());

        searchNikField.textProperty().addListener((obs, oldValue, newValue) -> refreshTable());

        refreshTable();
    }

    private void refreshTable() {
        String filter = filterCombo.getValue();
        StatusPengajuan statusFilter;
        String gpFilter;
        String filterVal = (filter == null) ? FILTER_SEMUA : filter;
        switch (filterVal) {
            case FILTER_DISETUJUI:
                statusFilter = StatusPengajuan.DISETUJUI;
                gpFilter = "Terverifikasi";
                break;
            case FILTER_DITOLAK:
                statusFilter = StatusPengajuan.DITOLAK;
                gpFilter = "Ditolak";
                break;
            default:
                statusFilter = null;
                gpFilter = null;
                break;
        }
        
        String searchNik = searchNikField.getText() == null ? "" : searchNikField.getText().trim();
        java.util.Optional<com.datani.model.Petani> searchedPetani = java.util.Optional.empty();
        if (!searchNik.isEmpty()) {
            searchedPetani = DataService.getPetaniByNik(searchNik);
        }
        final Integer searchedPetaniId = searchedPetani.map(com.datani.model.Petani::getId).orElse(null);

        // Filter Pengajuan Pupuk
        java.util.List<Pengajuan> pList = new java.util.ArrayList<>();
        for (Pengajuan p : DataService.getPengajuanTerverifikasi(statusFilter)) {
            if (searchNik.isEmpty() || (searchedPetaniId != null && p.getPetaniId() == searchedPetaniId)) {
                pList.add(p);
            }
        }
        hasilTable.setItems(FXCollections.observableArrayList(pList));
        
        // Memfilter Laporan Gagal Panen yang terverifikasi (Disetujui/Ditolak)
        java.util.List<LaporanGagalPanen> gpList = new java.util.ArrayList<>();
        for (LaporanGagalPanen gp : DataService.getAllLaporanGagalPanen()) {
            if ("Terverifikasi".equals(gp.getStatus()) || "Ditolak".equals(gp.getStatus())) {
                if (gpFilter == null || gp.getStatus().equals(gpFilter)) {
                    if (searchNik.isEmpty() || (searchedPetaniId != null && gp.getPetaniId() == searchedPetaniId)) {
                        gpList.add(gp);
                    }
                }
            }
        }
        gpTable.setItems(FXCollections.observableArrayList(gpList));
    }

    private TableCell<Pengajuan, String> statusStyledCell() {
        return new TableCell<>() {
            @Override
            protected void updateItem(String value, boolean empty) {
                super.updateItem(value, empty);
                getStyleClass().removeAll("status-menunggu", "status-disetujui", "status-ditolak");
                if (empty || value == null) {
                    setText(null);
                    return;
                }
                setText(value);
                Pengajuan pengajuan = getTableView().getItems().get(getIndex());
                switch (pengajuan.getStatus()) {
                    case MENUNGGU_VERIFIKASI:
                        getStyleClass().add("status-menunggu");
                        break;
                    case DISETUJUI:
                        getStyleClass().add("status-disetujui");
                        break;
                    case DITOLAK:
                        getStyleClass().add("status-ditolak");
                        break;
                }
            }
        };
    }

    private TableCell<LaporanGagalPanen, String> gpStatusStyledCell() {
        return new TableCell<>() {
            @Override
            protected void updateItem(String value, boolean empty) {
                super.updateItem(value, empty);
                getStyleClass().removeAll("status-menunggu", "status-disetujui", "status-ditolak");
                if (empty || value == null) {
                    setText(null);
                    return;
                }
                setText(value);
                LaporanGagalPanen laporan = getTableView().getItems().get(getIndex());
                switch (laporan.getStatus()) {
                    case "Menunggu Peninjauan":
                        getStyleClass().add("status-menunggu");
                        break;
                    case "Terverifikasi":
                        getStyleClass().add("status-disetujui");
                        break;
                    case "Ditolak":
                        getStyleClass().add("status-ditolak");
                        break;
                }
            }
        };
    }
}
