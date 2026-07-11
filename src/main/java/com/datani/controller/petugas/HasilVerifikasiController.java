package com.datani.controller.petugas;

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
    private TableView<Pengajuan> hasilTable;

    @FXML
    private TableColumn<Pengajuan, Number> idColumn;

    @FXML
    private TableColumn<Pengajuan, String> namaPetaniColumn;

    @FXML
    private TableColumn<Pengajuan, String> jenisTanamanColumn;

    @FXML
    private TableColumn<Pengajuan, String> statusColumn;

    @FXML
    private TableColumn<Pengajuan, String> tanggalVerifikasiColumn;

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId()));
        namaPetaniColumn.setCellValueFactory(data ->
                new SimpleStringProperty(DataService.getNamaPetaniById(data.getValue().getPetaniId())));
        jenisTanamanColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getJenisTanaman()));
        statusColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus().getLabel()));
        statusColumn.setCellFactory(col -> statusStyledCell());
        tanggalVerifikasiColumn.setCellValueFactory(data -> {
            java.time.LocalDate tanggal = data.getValue().getTanggalVerifikasi();
            return new SimpleStringProperty(tanggal == null ? "-" : tanggal.format(DATE_FORMAT));
        });

        filterCombo.setItems(FXCollections.observableArrayList(FILTER_SEMUA, FILTER_DISETUJUI, FILTER_DITOLAK));
        filterCombo.getSelectionModel().select(FILTER_SEMUA);
        filterCombo.valueProperty().addListener((obs, oldValue, newValue) -> refreshTable());

        refreshTable();
    }

    private void refreshTable() {
        String filter = filterCombo.getValue();
        StatusPengajuan statusFilter;
        String filterVal = (filter == null) ? FILTER_SEMUA : filter;
        switch (filterVal) {
            case FILTER_DISETUJUI:
                statusFilter = StatusPengajuan.DISETUJUI;
                break;
            case FILTER_DITOLAK:
                statusFilter = StatusPengajuan.DITOLAK;
                break;
            default:
                statusFilter = null;
                break;
        }
        hasilTable.setItems(DataService.getPengajuanTerverifikasi(statusFilter));
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
}
