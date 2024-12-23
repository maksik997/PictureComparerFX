package pl.magzik.picture_comparer_fx.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.magzik.picture_comparer_fx.base.comparator.FileSizeComparator;
import pl.magzik.picture_comparer_fx.base.comparator.NaturalComparator;
import pl.magzik.picture_comparer_fx.controller.base.PanelController;
import pl.magzik.picture_comparer_fx.model.GalleryModel;
import pl.magzik.picture_comparer_fx.model.GalleryTableModel;
import pl.magzik.picture_comparer_fx.service.GalleryService;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * The {@code GalleryController} class is responsible for managing the gallery view in the application.
 * It provides functionality to add, remove, delete, and manage image files within the gallery.
 * Additionally, it handles image selection, searching, and various file operations such as renaming and removing duplicates.
 * This class extends {@link PanelController} and is tightly integrated with the {@link GalleryModel} and {@link GalleryService} to
 * perform file operations asynchronously.
 * <p>
 * It also manages the gallery's visual representation, including a table view to display the gallery's images and their properties.
 */
public class GalleryController extends PanelController {

    private static final Logger log = LoggerFactory.getLogger(GalleryController.class);

    private final GalleryModel model;

    private final GalleryService service;

    /**
     * Constructs a new {@code GalleryController} instance, initializing the model and service.
     */
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

    /**
     * Initializes the gallery table and sets up the table columns, including setting up cell value factories,
     * comparators, and editing options. Additionally, sets the initial state of the gallery UI.
     */
    public void initialize() {
        galleryTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        galleryTable.setSelectionModel(null);
        galleryTable.setEditable(true);

        selectColumn.setCellValueFactory(p -> p.getValue().selectedProperty());
        selectColumn.setCellFactory(CheckBoxTableCell.forTableColumn(selectColumn));
        selectColumn.setEditable(true);

        addSelectAllCheckbox();

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("filename"));
        nameColumn.setComparator(new NaturalComparator());

        sizeColumn.setCellValueFactory(new PropertyValueFactory<>("fileSize"));
        sizeColumn.setComparator(new FileSizeComparator());

        dateColumn.setCellValueFactory(new PropertyValueFactory<>("modifiedDate"));

        galleryTable.setItems(model.getGalleryData());

        setProcessingState(false);
    }

    /**
     * Adds a "Select All" checkbox to the gallery table header, allowing users to select or deselect all images at once.
     */
    private void addSelectAllCheckbox() {
        CheckBox checkBox = new CheckBox();
        selectColumn.setGraphic(checkBox);
        selectColumn.setSortable(false);

        checkBox.selectedProperty().addListener(((observable, wasSelected, isSelected) -> model.getGalleryData()
            .forEach(item -> item.selectedProperty().setValue(isSelected))));
    }

    /**
     * Handles the action of adding new images to the gallery. A file chooser dialog is shown to select multiple image files.
     * The selected files are then processed and added to the gallery.
     */
    @FXML
    public void handleAddImages() {
        FileChooser fileChooser = new FileChooser();
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(getStage());

        if (selectedFiles != null) {
            try {
                service.addImages(selectedFiles);
            } catch (IOException e) {
                handleTaskError("Couldn't add images to gallery, because: {}", "dialog.context.error.gallery.add", e);
            }

            log.info("Selected files: {}", selectedFiles.size());
        } else {
            log.info("No files selected.");
        }

        setProcessingState(false);
    }

    /**
     * Handles the action of removing selected images from the gallery. The images are processed and removed from the gallery.
     */
    @FXML
    public void handleRemoveImages() {
        try {
            service.removeImages(model.getSelectedData());
        } catch (IOException e) {
            handleTaskError("Couldn't remove images, because:", "dialog.context.error.gallery.remove", e);
        }

        setProcessingState(false);
    }

    /**
     * Handles the action of deleting selected images from the gallery and also removing them from disk.
     * The user is prompted to confirm the deletion before the files are permanently removed.
     */
    @FXML
    public void handleDeleteImagesFromDisk() {
        if (model.getSelectedData().isEmpty() || !showConfirmationDialog("dialog.header.gallery.delete")) return;

        try {
            service.deleteImagesFromDisk(model.getSelectedData());
        } catch (IOException e) {
            handleTaskError("Couldn't delete images from disk, because:", "dialog.context.error.gallery.delete", e);
        }

        setProcessingState(false);
    }

    /**
     * Handles the action of removing duplicate images from the gallery. The user is prompted to confirm the removal.
     * Duplicate images are then processed and removed from the gallery.
     */
    @FXML
    public void handleRemoveDuplicates() {
        if (model.getSelectedData().isEmpty() || !showConfirmationDialog("dialog.header.gallery.duplicates")) return;
        setProcessingState(true);

        service.removeDuplicates(model.getSelectedData())
            .exceptionally(t -> handleTaskError("Couldn't remove all duplicates from gallery, because:", "dialog.context.error.gallery.duplicates", t))
            .whenComplete((v, t) -> setProcessingState(false));
    }

    /**
     * Handles the action of renaming selected images in the gallery. The user is prompted to confirm the renaming.
     * The selected images are then renamed based on the specified naming convention.
     */
    @FXML
    public void handleUnifyNaming() {
        if (model.getSelectedData().isEmpty() || !showConfirmationDialog("dialog.header.gallery.names")) return;
        setProcessingState(true);

        service.renameAll(model.getSelectedData())
            .exceptionally(t -> handleTaskError("Couldn't rename all images from gallery, because:", "dialog.context.error.gallery.names", t))
            .whenComplete((v, t) -> setProcessingState(false));
    }

    /**
     * Opens the selected images from the gallery in the default image viewer or editor.
     */
    @FXML
    public void handleOpenImage() {
        service.openImages(model.getSelectedData())
            .exceptionally(t -> handleTaskError("Couldn't remove all duplicates from gallery, because:", "dialog.context.error.gallery.open", t));
    }

    /**
     * Handles the search functionality for images in the gallery. Filters the displayed images based on the search query.
     */
    @FXML
    public void handleSearch() {
        String key = searchTextField.getText().toLowerCase();
        if (key.isBlank()) {
            galleryTable.setItems(model.getGalleryData());
            return;
        }

        galleryTable.setItems(model.getGalleryData()
            .filtered(el -> el.filenameProperty().get().contains(key))
        );
    }

    /**
     * Handles an error during a task and displays an error dialog to the user.
     *
     * @param logMsg the log message to display
     * @param headerText the header text to display in the error dialog
     * @param e the exception that occurred
     * @return {@code null} to continue the execution flow
     */
    private <D> @Nullable D handleTaskError(String logMsg, String headerText, Throwable e) {
        log.error("{}{}", logMsg, e.getMessage(), e);
        Platform.runLater(() -> showErrorDialog(headerText));
        return null;
    }

    /**
     * Updates the UI to reflect the processing state. When processing is active, it disables buttons, sets the cursor to a wait state,
     * and disables the search functionality. Once processing is complete, it refreshes the table and updates the element count.
     *
     * @param isProcessing true if processing is in progress, false otherwise
     */
    private void setProcessingState(boolean isProcessing) {
        Platform.runLater(() -> {
            setButtonsState(isProcessing);
            searchTextField.setEditable(!isProcessing);
            getStage().getScene().setCursor(isProcessing ? Cursor.WAIT : Cursor.DEFAULT);
            if (!isProcessing) {
                galleryTable.refresh();
                int count = model.getGalleryData().size();
                elementCountText.setText(String.valueOf(count));
            }
        });
    }

    /**
     * Toggles the state of the buttons in the UI (enabled or disabled) based on the processing state.
     *
     * @param disable true to disable buttons, false to enable them
     */
    private void setButtonsState(boolean disable) {
        setButtonsState(disable, backButton, addButton, removeButton, deleteFromDiskButton, duplicatesButton, nameButton, openButton);
    }
}
