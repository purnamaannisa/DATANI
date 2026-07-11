package com.datani.controller.petani;

import com.datani.model.Pengajuan;
import com.datani.service.DataService;
import com.datani.session.UserSession;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.time.format.DateTimeFormatter;

/**
 * Controller untuk StatusPengajuan.fxml (Petani only, read-only).
 * Seorang Petani hanya dapat melihat pengajuannya SENDIRI - query selalu
 * dibatasi oleh {@code UserSession.getCurrentPetani().getId()}. Tidak ada
 * tombol Setujui/Tolak di layar ini; itu hanya ada di layar Petugas BPP.
 */
public class StatusPengajuanController {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy");

    @FXML
    private Label subtitleLabel;

    @FXML
    private TableView<Pengajuan> statusTable;

    @FXML
    private TableColumn<Pengajuan, Number> idColumn;

    @FXML
    private TableColumn<Pengajuan, String> tanggalColumn;

    @FXML
    private TableColumn<Pengajuan, String> luasLahanColumn;

    @FXML
    private TableColumn<Pengajuan, String> jenisTanamanColumn;

    @FXML
    private TableColumn<Pengajuan, String> statusKepemilikanColumn;

    @FXML
    private TableColumn<Pengajuan, String> statusColumn;

    @FXML
    private TableColumn<Pengajuan, Void> detailColumn;

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId()));
        tanggalColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getTanggalPengajuan().format(DATE_FORMAT)));
        luasLahanColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getLuasLahan() + " ha"));
        jenisTanamanColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getJenisTanaman()));
        statusKepemilikanColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatusKepemilikan()));
        statusColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus().getLabel()));
        statusColumn.setCellFactory(col -> statusStyledCell());
        detailColumn.setCellFactory(col -> detailButtonCell());

        if (UserSession.getCurrentPetani() != null) {
            int petaniId = UserSession.getCurrentPetani().getId();
            statusTable.setItems(DataService.getPengajuanByPetaniId(petaniId));
            subtitleLabel.setText("Menampilkan pengajuan milik " + UserSession.getCurrentPetani().getNamaLengkap());
        }
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

    private TableCell<Pengajuan, Void> detailButtonCell() {
        return new TableCell<>() {
            private final Button detailButton = new Button("Lihat Detail");

            {
                detailButton.getStyleClass().add("table-action-button");
                detailButton.setOnAction(e -> tampilkanDetail(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : detailButton);
            }
        };
    }

    private void tampilkanDetail(Pengajuan pengajuan) {
        StringBuilder sb = new StringBuilder();
        sb.append("ID Pengajuan: ").append(pengajuan.getId()).append("\n");
        sb.append("Tanggal Pengajuan: ").append(pengajuan.getTanggalPengajuan().format(DATE_FORMAT)).append("\n");
        sb.append("Luas Lahan: ").append(pengajuan.getLuasLahan()).append(" ha\n");
        sb.append("Jenis Tanaman: ").append(pengajuan.getJenisTanaman()).append("\n");
        sb.append("Status Kepemilikan: ").append(pengajuan.getStatusKepemilikan()).append("\n");
        sb.append("Status Verifikasi: ").append(pengajuan.getStatus().getLabel()).append("\n");

        if (pengajuan.getStatus() == com.datani.model.StatusPengajuan.DITOLAK) {
            String alasan = pengajuan.getAlasanPenolakan();
            sb.append("Alasan Penolakan: ").append(alasan == null || alasan.trim().isEmpty() ? "-" : alasan).append("\n");
        }

        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Detail Pengajuan #" + pengajuan.getId());
        alert.setHeaderText(null);
        alert.setContentText(sb.toString());
        alert.showAndWait();
    }
}
