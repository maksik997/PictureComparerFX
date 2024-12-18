package pl.magzik._new.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.magzik._new.controller.base.Controller;
import pl.magzik._new.controller.base.PanelController;
import pl.magzik._new.model.ComparerModel;
import pl.magzik._new.service.ComparerService;
import pl.magzik.base.interfaces.Command;

import java.io.File;
import java.util.ResourceBundle;

public class ComparerController extends PanelController {

    private static final Logger log = LoggerFactory.getLogger(ComparerController.class);

    private static final String STATE_READY = "comparer.state.ready",
                                STATE_PREPARE = "comparer.state.prepare",
                                STATE_MAP = "comparer.state.map",
                                STATE_UPDATE = "comparer.state.update",
                                STATE_MOVE = "comparer.state.move",
                                STATE_REMOVE = "comparer.state.remove",
                                STATE_DONE = "comparer.state.done";

    private final ComparerModel model;

    private final ComparerService service;

    private final PieChart.Data originalSlice;

    private final PieChart.Data duplicateSlice;

    private boolean edited; ///< If true, then user will be prompted before closing the panel.

    public ComparerController() {
        this.model = Controller.getModel().getComparerModel();
        this.service = new ComparerService(model);

        this.edited = false;

        this.originalSlice = new PieChart.Data("O", 50);
        this.duplicateSlice = new PieChart.Data("D", 50);
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
    private PieChart duplicateRatioPieChart;

    @FXML
    private Text stateText;

    @FXML
    private ProgressBar taskProgressBar;

    public void initialize() {
        setButtonsState(true, moveButton, removeButton, resetButton);
        setButtonsState(false, backButton, pathButton, loadButton);

        originalListView.setItems(model.getLoadedFiles());
        duplicateListView.setItems(model.getDuplicateFiles());

        taskProgressBar.setProgress(1);

        duplicateRatioPieChart.getData().addAll(originalSlice, duplicateSlice);
    }

    @FXML
    private void handleChoosePath() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
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

        edited = true;
        handleTaskStart(STATE_PREPARE);

        // Processing
        service.execute(
            () -> service.validateFilesAsync(new File(path)),
            () -> updateUserInterface(STATE_MAP),
            service::compareFilesAsync,
            () -> updateUserInterface(STATE_UPDATE)
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
        handleFileTransfer(service::moveDuplicatesAsync);
    }

    @FXML
    private void handleRemovingFiles() {
        if (!showConfirmationDialog("dialog.header.comparer-remove")) return;

        log.info("Removing duplicated images...");
        handleTaskStart(STATE_REMOVE);
        handleFileTransfer(service::removeDuplicatesAsync);
    }

    @FXML
    private void handleReset() {
        if (!showConfirmationDialog("dialog.header.comparer-reset")) return;

        log.info("Resetting the comparer's state...");

        model.clearLists();

        edited = false;

        setButtonsState(true, moveButton, removeButton, resetButton);
        setButtonsState(false, backButton, pathButton, loadButton);

        taskProgressBar.setProgress(1);
        stateText.setText(translate(STATE_READY));

        originalTrayTextField.setText("0");
        duplicateTrayTextField.setText("0");

        originalSlice.setPieValue(50);
        duplicateSlice.setPieValue(50);

        pathTextField.setText("");
    }

    @Override
    protected void backToMenu() {
        if (edited && !showConfirmationDialog("dialog.header.back-menu")) return;

        super.backToMenu();
    }

    @Override
    public void setBundle(ResourceBundle bundle) {
        super.setBundle(bundle);
        originalSlice.setName(translate("comparer.chart.label.originals"));
        duplicateSlice.setName(translate("comparer.chart.label.duplicates"));
    }

    private void updateUserInterface(String state) {
        int totalCount = model.getLoadedFiles().size();
        int duplicateCount = model.getDuplicateFiles().size();

        Platform.runLater(() -> {
            stateText.setText(translate(state));
            originalTrayTextField.setText(String.valueOf(totalCount));
            duplicateTrayTextField.setText(String.valueOf(duplicateCount));

            log.info("UI updated: {} files processed, {} duplicates found", totalCount, duplicateCount);
        });
    }

    private void handleLoadingTaskCompletion() {
        Platform.runLater(() -> {
            handleTaskDone();

            if (!model.getDuplicateFiles().isEmpty()) {
                moveButton.setDisable(false);
                removeButton.setDisable(false);
            }

            int duplicateCount = model.getDuplicateFiles().size();
            int originalCount = model.getLoadedFiles().size() - duplicateCount;

            log.debug("Calculated: duplicates: {}, originals: {}", duplicateCount, originalCount);

            originalSlice.setPieValue(originalCount);
            duplicateSlice.setPieValue(duplicateCount);
        });
    }

    private void handleFileTransfer(Command command) {
        service.execute(command)
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
