package pl.magzik._new.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.magzik._new.controller.base.PanelController;
import pl.magzik._new.model.GalleryModel;
import pl.magzik._new.model.GalleryTableModel;
import pl.magzik._new.service.GalleryService;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class GalleryController extends PanelController {

    private static final Logger log = LoggerFactory.getLogger(GalleryController.class);

    private final GalleryModel model;

    private final GalleryService service;

    public GalleryController() {
        this.model = getModel().getGalleryModel();
        this.service = new GalleryService(model);
    }

    @FXML
    private TableColumn<GalleryTableModel, Boolean> selectColumn;

    @FXML
    private TableColumn<GalleryTableModel, String> nameColumn;

    @FXML
    private TableColumn<GalleryTableModel, String> sizeColumn;

    @FXML
    private TableColumn<GalleryTableModel, String> dateColumn;

    @FXML
    private TableView<GalleryTableModel> galleryTable;

    @FXML
    private Text elementCountText;

    @FXML
    private TextField searchTextField;

    @FXML
    private Button addButton;

    @FXML
    private Button removeButton;

    @FXML
    private Button backButton;

    @FXML
    private Button deleteFromDiskButton;

    @FXML
    private Button duplicatesButton;

    @FXML
    private Button nameButton;

    @FXML
    private Button openButton;

    public void initialize() {
        galleryTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        galleryTable.setSelectionModel(null);
        galleryTable.setEditable(true);

        selectColumn.setCellValueFactory(p -> p.getValue().selectedProperty());
        selectColumn.setCellFactory(CheckBoxTableCell.forTableColumn(selectColumn));
        selectColumn.setEditable(true);

        addSelectAllCheckbox();

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("filename"));
        nameColumn.setComparator((o1, o2) -> {
            Pattern pattern = Pattern.compile("(\\d+)|(\\D+)");
            Matcher m1 = pattern.matcher(o1);
            Matcher m2 = pattern.matcher(o2);

            while (m1.find() && m2.find()) {
                String s1 = m1.group(), s2 = m2.group();

                int cmp;
                if (s1.matches("\\d+") && s2.matches("\\d+"))
                    cmp = Long.compare(Long.parseLong(s1), Long.parseLong(s2));
                else
                    cmp = s1.compareTo(s2);

                if (cmp != 0) return cmp;
            }

            return Integer.compare(o1.length(), o2.length());
        });

        sizeColumn.setCellValueFactory(new PropertyValueFactory<>("fileSize"));
        sizeColumn.setComparator((o1, o2) -> {
            String[] so1 = o1.split(" "),
                    so2 = o2.split(" ");
            double do1 = Double.parseDouble(so1[0].replace(',', '.')) * (so1[1].equals("MB") ? 1024 : so1[1].equals("GB") ? 1024 * 1024 : 1),
                    do2 = Double.parseDouble(so2[0].replace(',', '.')) * (so2[1].equals("MB") ? 1024 : so2[1].equals("GB") ? 1024 * 1024 : 1);

            return Double.compare(do1, do2);
        });

        dateColumn.setCellValueFactory(new PropertyValueFactory<>("modifiedDate"));

        galleryTable.setItems(model.getGalleryData());

        updateElementCount();
    }

    @FXML
    public void handleAddImages() {
        FileChooser fileChooser = new FileChooser();
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(getStage());

        if (selectedFiles != null) {
            try {
                service.addImages(selectedFiles);
            } catch (IOException e) {
                showErrorDialog("dialog.context.error.gallery.add");
                log.error("Couldn't add images to gallery, because: {}", e.getMessage(), e);
            }

            log.info("Selected files: {}", selectedFiles.size());
        } else {
            log.info("No files selected.");
        }

        galleryTable.refresh();
        updateElementCount();
    }

    @FXML
    public void handleRemoveImages() {
        try {
            service.removeImages(model.getSelectedData());
        } catch (IOException e) {
            showErrorDialog("dialog.context.error.gallery.remove");
            log.error("Couldn't remove images, because: {}", e.getMessage(), e);
        }

        updateElementCount();
    }

    @FXML
    public void handleDeleteImagesFromDisk() {
        if (model.getSelectedData().isEmpty() || !showConfirmationDialog("dialog.header.gallery.delete")) return;

        try {
            service.deleteImagesFromDisk(model.getSelectedData());
        } catch (IOException e) {
            showErrorDialog("dialog.context.error.gallery.delete");
            log.error("Couldn't delete images from disk, because: {}", e.getMessage(), e);
        }

        updateElementCount();
    }

    @FXML
    public void handleRemoveDuplicates() {
        if (model.getSelectedData().isEmpty() || !showConfirmationDialog("dialog.header.gallery.duplicates")) return;

        setButtonsState(true);
        searchTextField.setEditable(false);
        getStage().getScene().setCursor(Cursor.WAIT);

        service.execute(() -> service.removeDuplicates(model.getSelectedData()))
            .exceptionally(t -> {
                handleTaskError("Couldn't remove all duplicates from gallery, because:", "dialog.context.error.gallery.duplicates", t);
                return null;
            })
            .whenComplete((v, t) -> handleTaskCompleted());
    }

    @FXML
    public void handleUnifyNaming() {
        if (model.getSelectedData().isEmpty() || !showConfirmationDialog("dialog.header.gallery.names")) return;

        setButtonsState(true);
        searchTextField.setEditable(false);
        getStage().getScene().setCursor(Cursor.WAIT);

        service.execute(() -> service.renameAll(model.getSelectedData()))
        .exceptionally(t -> {
            handleTaskError("Couldn't rename all images from gallery, because:", "dialog.context.error.gallery.names", t);
            return null;
        })
        .whenComplete((v, t) -> handleTaskCompleted());
    }

    @FXML
    public void handleOpenImage() {
        service.execute(() -> service.openImages(model.getSelectedData()))
            .exceptionally(t -> {
                handleTaskError("Couldn't remove all duplicates from gallery, because:", "dialog.context.error.gallery.open", t);
                return null;
            });
    }

    @FXML
    public void handleSearch() {
        String key = searchTextField.getText().toLowerCase();

        galleryTable.setItems(model.getGalleryData()
        .filtered(
            el -> el.filenameProperty().get().contains(key)
        ));
    }

    private void updateElementCount() {
        int count = model.getGalleryData().size();
        elementCountText.setText(String.valueOf(count));
    }

    private void addSelectAllCheckbox() {
        CheckBox checkBox = new CheckBox();
        selectColumn.setGraphic(checkBox);
        selectColumn.setSortable(false);

        checkBox.setOnAction(e -> {
            boolean isSelected = checkBox.isSelected();
            for (GalleryTableModel item : model.getGalleryData()) {
                item.selectedProperty().setValue(isSelected);
            }
        });
    }

    private void setButtonsState(boolean disable) {
        setButtonsState(disable, backButton, addButton, removeButton, deleteFromDiskButton, duplicatesButton, nameButton, openButton);
    }

    private void handleTaskCompleted() {
        Platform.runLater(() -> {
            setButtonsState(false);
            searchTextField.setEditable(true);
            updateElementCount();
            getStage().getScene().setCursor(Cursor.DEFAULT);
        });
    }

    private void handleTaskError(String logMsg, String headerText, Throwable e) {
        log.error("{}{}", logMsg, e.getMessage(), e);
        showErrorDialog(headerText);
    }
}
