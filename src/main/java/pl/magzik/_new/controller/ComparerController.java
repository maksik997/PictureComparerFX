package pl.magzik._new.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import org.intellij.lang.annotations.MagicConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.magzik._new.controller.base.Controller;
import pl.magzik._new.controller.base.PanelController;
import pl.magzik._new.model.ComparerService;
import pl.magzik.base.interfaces.Command;

import java.io.File;
import java.util.List;

public class ComparerController extends PanelController {

    private static final Logger log = LoggerFactory.getLogger(ComparerController.class);

    /**
     * Flags used {@link ComparerController#updateUserInterface(String, short, List)} method.
     * */
    private static final short LIST_VIEW_ORIGINAL = 0,
                                LIST_VIEW_DUPLICATE = 1;

    private static final String STATE_READY = "comparer.state.ready",
                                STATE_PREPARE = "comparer.state.prepare",
                                STATE_MAP = "comparer.state.map",
                                STATE_UPDATE = "comparer.state.update",
                                STATE_MOVE = "comparer.state.move",
                                STATE_REMOVE = "comparer.state.remove",
                                STATE_DONE = "comparer.state.done";

    private final ComparerService comparerService;

    private final ObservableList<File> originalFileList;

    private final ObservableList<File> duplicateFileList;

    private boolean edited; ///< If true, then user will be prompted before closing the panel.

    public ComparerController() {
        this.comparerService = Controller.getModel().getComparerService();
        this.originalFileList = FXCollections.observableArrayList();
        this.duplicateFileList = FXCollections.observableArrayList();

        this.edited = false;
    }

    @FXML
    private Button backButton;

    @FXML
    private Button pathButton;

    @FXML
    private Button loadButton;

    @FXML
    private Button moveButton;

    @FXML
    private Button removeButton;

    @FXML
    private Button resetButton;

    @FXML
    private TextField pathTextField;

    @FXML
    private ListView<File> originalListView;

    @FXML
    private ListView<File> duplicateListView;

    @FXML
    private TextField originalTrayTextField;

    @FXML
    private TextField duplicateTrayTextField;

    @FXML
    private Text stateText;

    @FXML
    private ProgressBar taskProgressBar;

    public void initialize() {
        setButtonsState(true, moveButton, removeButton, resetButton);
        setButtonsState(false, backButton, pathButton, loadButton);

        originalListView.setItems(originalFileList);
        duplicateListView.setItems(duplicateFileList);

        taskProgressBar.setProgress(1);
    }

    @FXML
    private void handleChoosePath() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        // TODO: Attaching stylesheet to this^...
        File selectedDirectory = directoryChooser.showDialog(getStage());

        if (selectedDirectory != null) {
            pathTextField.setText(selectedDirectory.getAbsolutePath());
            log.info("Selected directory: {}", selectedDirectory.getAbsolutePath());
        } else {
            log.info("No directory selected.");
        }
    }

    @FXML
    private void handleLoadingFiles() {
        String path = pathTextField.getText();

        if (path == null || path.isBlank()) {
            log.warn("Path is blank or null.");
            showErrorDialog("dialog.context.error.path-empty");
            return;
        }

        log.info("Loading files from path: {}", path);

        // Preparing.
        comparerService.setInput(new File(path));

        edited = true;
        handleTaskStart(STATE_PREPARE);

        // Processing
        comparerService.execute(
            comparerService::handleLoadFiles,
            () -> updateUserInterface(STATE_MAP, LIST_VIEW_ORIGINAL, comparerService.getInput()),
            comparerService::handleCompareFiles,
            () -> updateUserInterface(STATE_UPDATE, LIST_VIEW_DUPLICATE, comparerService.getOutput())
        ).exceptionally(e -> {
            handleTaskError("Error occurred while loading the files:", "dialog.context.error.comparer.loading", e);
            return null;
        })
        .whenComplete((v, e) -> handleLoadingTaskCompletion());
    }

    @FXML
    private void handleMovingFiles() {
        if (!showConfirmationDialog("dialog.header.comparer-move")) return;

        log.info("Moving duplicated images...");
        handleTaskStart(STATE_MOVE);
        handleFileTransfer(comparerService::handleMoveFiles);
    }

    @FXML
    private void handleRemovingFiles() {
        if (!showConfirmationDialog("dialog.header.comparer-remove")) return;

        log.info("Removing duplicated images...");
        handleTaskStart(STATE_REMOVE);
        handleFileTransfer(comparerService::handleRemoveFiles);
    }

    @FXML
    private void handleReset() {
        if (!showConfirmationDialog("dialog.header.comparer-reset")) return;

        log.info("Resetting the comparer's state...");
        comparerService.handleReset();
        edited = false;

        setButtonsState(true, moveButton, removeButton, resetButton);
        setButtonsState(false, backButton, pathButton, loadButton);

        originalFileList.clear();
        duplicateFileList.clear();

        taskProgressBar.setProgress(1);
        stateText.setText(translate(STATE_READY));

        originalTrayTextField.setText("0");
        duplicateTrayTextField.setText("0");

        pathTextField.setText("");
    }

    @Override
    protected void backToMenu() {
        if (edited && !showConfirmationDialog("dialog.header.back-menu")) return;

        super.backToMenu();
    }

    private void updateUserInterface(String state, @MagicConstant(flags={LIST_VIEW_ORIGINAL, LIST_VIEW_DUPLICATE}) short listViewName, List<File> fileList) {
        int totalCount = comparerService.getInput().size();
        int duplicateCount = comparerService.getOutput().size();

        ObservableList<File> list = switch (listViewName) {
            case LIST_VIEW_ORIGINAL -> originalFileList;
            case LIST_VIEW_DUPLICATE -> duplicateFileList;
            default -> throw new IllegalArgumentException("Given listViewName is invalid.");
        };

        Platform.runLater(() -> {
            list.addAll(fileList);
            stateText.setText(translate(state));
            originalTrayTextField.setText(String.valueOf(totalCount));
            duplicateTrayTextField.setText(String.valueOf(duplicateCount));

            log.info("UI updated: {} files processed, {} duplicates found", totalCount, duplicateCount);
        });
    }

    private void handleLoadingTaskCompletion() {
        Platform.runLater(() -> {
            handleTaskDone();

            if (!duplicateFileList.isEmpty()) {
                moveButton.setDisable(false);
                removeButton.setDisable(false);
            }
        });
    }

    private void handleFileTransfer(Command command) {
        comparerService.execute(command)
            .exceptionally(e -> {
                handleTaskError("Error occurred while transferring the files:", "dialog.context.error.comparer.file-transfer", e);
                return null;
            })
            .whenComplete((v, t) -> Platform.runLater(this::handleTaskDone));
    }

    private void handleTaskStart(String state) {
        setButtonsState(true, backButton, pathButton, loadButton, moveButton, removeButton, resetButton);
        taskProgressBar.setProgress(-1);
        stateText.setText(translate(state));
        getStage().getScene().setCursor(Cursor.WAIT);
    }

    private void handleTaskDone() {
        backButton.setDisable(false);
        resetButton.setDisable(false);
        stateText.setText(translate(STATE_DONE));
        taskProgressBar.setProgress(1);
        getStage().getScene().setCursor(Cursor.DEFAULT);

        log.info("Task completed successfully, UI reset.");
    }

    private void handleTaskError(String logMsg, String headerText, Throwable e) {
        log.error("{}{}", logMsg, e.getMessage(), e);
        showErrorDialog(headerText);

    }
}
