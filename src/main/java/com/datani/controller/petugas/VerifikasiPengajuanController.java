package com.datani.controller.petugas;

import com.datani.model.Pengajuan;
import com.datani.model.Petani;
import com.datani.model.StatusPengajuan;
import com.datani.service.DataService;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Controller untuk VerifikasiPengajuan.fxml (Petugas BPP only).
 * <p>
 * Layout master-detail: daftar pengajuan "Menunggu Verifikasi" tampil di
 * panel kiri (TableView), dan detail lengkap pengajuan yang dipilih -
 * termasuk data petani, data lahan, dan foto bukti lahan - tampil di
 * panel kanan, lengkap dengan tombol Setujui / Tolak.
 */
public class VerifikasiPengajuanController {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy");

    @FXML
    private TableView<Pengajuan> pengajuanTable;

    @FXML
    private TableColumn<Pengajuan, Number> idColumn;

    @FXML
    private TableColumn<Pengajuan, String> namaPetaniColumn;

    @FXML
    private TableColumn<Pengajuan, String> jenisTanamanColumn;

    @FXML
    private TableColumn<Pengajuan, String> luasLahanColumn;

    @FXML
    private TableColumn<Pengajuan, String> tanggalPengajuanColumn;

    // --- Panel detail ---
    @FXML
    private VBox emptyDetailPlaceholder;

    @FXML
    private ScrollPane detailScrollPane;

    @FXML
    private Label detailIdLabel;

    @FXML
    private Label detailStatusLabel;

    @FXML
    private Label detailNikLabel;

    @FXML
    private Label detailNamaLabel;

    @FXML
    private Label detailNomorKKLabel;

    @FXML
    private Label detailNomorHPLabel;

    @FXML
    private Label detailAlamatLabel;

    @FXML
    private Label detailKelompokTaniLabel;

    @FXML
    private Label detailLuasLahanLabel;

    @FXML
    private Label detailStatusKepemilikanLabel;

    @FXML
    private Label detailJenisTanamanLabel;

    @FXML
    private ImageView detailFotoLahan;

    @FXML
    private Label detailFotoKosongLabel;

    @FXML
    private VBox alasanPenolakanBox;

    @FXML
    private Label detailAlasanPenolakanLabel;

    @FXML
    private HBox aksiBox;

    private Pengajuan pengajuanTerpilih;

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId()));
        namaPetaniColumn.setCellValueFactory(data ->
                new SimpleStringProperty(DataService.getNamaPetaniById(data.getValue().getPetaniId())));
        jenisTanamanColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getJenisTanaman()));
        luasLahanColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getLuasLahan() + " ha"));
        tanggalPengajuanColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getTanggalPengajuan().format(DATE_FORMAT)));

        pengajuanTable.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            pengajuanTerpilih = newValue;
            tampilkanDetail(newValue);
        });

        refreshTable();
        showEmptyState();
    }

    private void refreshTable() {
        pengajuanTable.setItems(DataService.getPengajuanMenungguVerifikasi());
    }

    private void showEmptyState() {
        emptyDetailPlaceholder.setVisible(true);
        emptyDetailPlaceholder.setManaged(true);
        detailScrollPane.setVisible(false);
        detailScrollPane.setManaged(false);
    }

    private void tampilkanDetail(Pengajuan pengajuan) {
        if (pengajuan == null) {
            showEmptyState();
            return;
        }

        emptyDetailPlaceholder.setVisible(false);
        emptyDetailPlaceholder.setManaged(false);
        detailScrollPane.setVisible(true);
        detailScrollPane.setManaged(true);

        Petani petani = DataService.getPetaniById(pengajuan.getPetaniId()).orElse(null);

        detailIdLabel.setText("Pengajuan #" + pengajuan.getId());
        detailStatusLabel.setText(pengajuan.getStatus().getLabel());
        detailStatusLabel.getStyleClass().removeAll("status-menunggu", "status-disetujui", "status-ditolak");
        detailStatusLabel.getStyleClass().add(styleClassForStatus(pengajuan.getStatus()));

        if (petani != null) {
            detailNikLabel.setText(petani.getNik());
            detailNamaLabel.setText(petani.getNamaLengkap());
            detailNomorKKLabel.setText(petani.getNomorKK());
            detailNomorHPLabel.setText(petani.getNomorHP());
            detailAlamatLabel.setText(petani.getAlamat());
            detailKelompokTaniLabel.setText(petani.getKelompokTani());
        }

        detailLuasLahanLabel.setText(pengajuan.getLuasLahan() + " ha");
        detailStatusKepemilikanLabel.setText(pengajuan.getStatusKepemilikan());
        detailJenisTanamanLabel.setText(pengajuan.getJenisTanaman());

        String fotoPath = pengajuan.getFotoLahan();
        boolean adaFoto = fotoPath != null && !fotoPath.trim().isEmpty() && new File(fotoPath).exists();
        if (adaFoto) {
            try {
                detailFotoLahan.setImage(new Image(new File(fotoPath).toURI().toString()));
                detailFotoLahan.setVisible(true);
                detailFotoKosongLabel.setVisible(false);
            } catch (Exception e) {
                detailFotoLahan.setVisible(false);
                detailFotoKosongLabel.setText("Gagal memuat foto");
                detailFotoKosongLabel.setVisible(true);
            }
        } else {
            detailFotoLahan.setVisible(false);
            detailFotoKosongLabel.setText("Tidak ada foto");
            detailFotoKosongLabel.setVisible(true);
        }

        boolean sudahDiverifikasi = pengajuan.getStatus() != StatusPengajuan.MENUNGGU_VERIFIKASI;
        aksiBox.setVisible(!sudahDiverifikasi);
        aksiBox.setManaged(!sudahDiverifikasi);

        boolean adaAlasanPenolakan = pengajuan.getStatus() == StatusPengajuan.DITOLAK
                && pengajuan.getAlasanPenolakan() != null && !pengajuan.getAlasanPenolakan().trim().isEmpty();
        alasanPenolakanBox.setVisible(adaAlasanPenolakan);
        alasanPenolakanBox.setManaged(adaAlasanPenolakan);
        if (adaAlasanPenolakan) {
            detailAlasanPenolakanLabel.setText(pengajuan.getAlasanPenolakan());
        }
    }

    private String styleClassForStatus(StatusPengajuan status) {
        switch (status) {
            case DISETUJUI:
                return "status-disetujui";
            case DITOLAK:
                return "status-ditolak";
            default:
                return "status-menunggu";
        }
    }

    @FXML
    private void handleSetujui() {
        if (pengajuanTerpilih == null) {
            return;
        }
        Alert confirm = new Alert(AlertType.CONFIRMATION);
        confirm.setTitle("Konfirmasi Persetujuan");
        confirm.setHeaderText(null);
        confirm.setContentText("Setujui pengajuan #" + pengajuanTerpilih.getId() + " ("
                + DataService.getNamaPetaniById(pengajuanTerpilih.getPetaniId()) + ")?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            DataService.setujuiPengajuan(pengajuanTerpilih.getId());
            refreshTable();
            showEmptyState();
        }
    }

    @FXML
    private void handleTolak() {
        if (pengajuanTerpilih == null) {
            return;
        }
        TextArea alasanArea = new TextArea();
        alasanArea.setPromptText("Alasan Penolakan wajib diisi");
        alasanArea.setPrefRowCount(4);
        alasanArea.setWrapText(true);

        Alert dialog = new Alert(AlertType.CONFIRMATION);
        dialog.setTitle("Tolak Pengajuan #" + pengajuanTerpilih.getId());
        dialog.setHeaderText("Alasan Penolakan");
        dialog.getDialogPane().setContent(new VBox(8, new Label("Alasan Penolakan wajib diisi:"), alasanArea));
        dialog.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if (!result.isPresent() || result.get() != ButtonType.OK) {
            return;
        }

        String alasan = alasanArea.getText() == null ? "" : alasanArea.getText().trim();
        if (alasan.isEmpty()) {
            Alert warning = new Alert(AlertType.WARNING);
            warning.setTitle("Alasan Penolakan Wajib Diisi");
            warning.setHeaderText(null);
            warning.setContentText("Alasan Penolakan wajib diisi sebelum menolak pengajuan.");
            warning.showAndWait();
            return;
        }

        DataService.tolakPengajuan(pengajuanTerpilih.getId(), alasan);
        refreshTable();
        showEmptyState();
    }
}
