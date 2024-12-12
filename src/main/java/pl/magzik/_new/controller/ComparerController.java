package pl.magzik._new.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import pl.magzik._new.controller.base.Controller;
import pl.magzik._new.controller.base.PanelController;
import pl.magzik._new.model.ComparerService;

import java.io.File;
import java.util.List;
import java.util.Objects;

public class ComparerController extends PanelController {

    private static final String LIST_VIEW_ORIGINAL = "LOADED",
                                LIST_VIEW_DUPLICATE = "DUPLICATES";

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
        this.comparerService = Controller.getModel().getComparerModel();
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
        lockButtons();
        backButton.setDisable(false);
        pathButton.setDisable(false);
        loadButton.setDisable(false);

        originalListView.setItems(originalFileList);
        duplicateListView.setItems(duplicateFileList);

        taskProgressBar.setProgress(1);
    }

    @FXML
    private void handleChoosePath() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(getStage());

        if (selectedDirectory != null) {
            pathTextField.setText(selectedDirectory.getAbsolutePath());
        }
    }

    @FXML
    private void handleLoadingFiles() {
        String path = pathTextField.getText();

        Objects.requireNonNull(path, "Path is null.");
        if (path.isEmpty()) throw new IllegalArgumentException("Path is empty"); // TODO TEMPORARY

        // Preparing.
        comparerService.setInput(List.of(new File(path)));

        edited = true;
        lockButtons();
        taskProgressBar.setProgress(-1);
        stateText.setText(translate(STATE_PREPARE));

        // Processing
        comparerService.execute(
            comparerService::handleLoadFiles,
            () -> updateUserInterface(STATE_MAP, LIST_VIEW_ORIGINAL, comparerService.getInput()),
            comparerService::handleCompareFiles,
            () -> updateUserInterface(STATE_UPDATE, LIST_VIEW_DUPLICATE, comparerService.getOutput())
        ).exceptionally(e -> {
            System.out.println("EXCEPTION: " + e.getMessage());
            throw new RuntimeException(e);
        }) // TODO TEMPORARY, SWITCH TO LOGGING
        .whenComplete((v, e) -> handleLoadingTaskCompletion());
    }

    @FXML
    private void handleMovingFiles() {
        if (!showConfirmationDialog("dialog.header.comparer-move")) return;

        lockButtons();
        taskProgressBar.setProgress(-1);
        stateText.setText(translate(STATE_MOVE));

        comparerService.execute(comparerService::handleMoveFiles)
            .exceptionally(e -> {
                System.out.println("EXCEPTION: " + e.getMessage());
                throw new RuntimeException(e);
            }) // TODO TEMPORARY, SWITCH TO LOGGING
            .whenComplete((v, t) -> handleFileTransferTaskCompletion());
    }

    @FXML
    private void handleRemovingFiles() {
        if (!showConfirmationDialog("dialog.header.comparer-remove")) return;

        lockButtons();
        taskProgressBar.setProgress(-1);
        stateText.setText(translate(STATE_REMOVE));

        comparerService.execute(comparerService::handleRemoveFiles)
            .exceptionally(e -> {
                System.out.println("EXCEPTION: " + e.getMessage());
                throw new RuntimeException(e);
            }) // TODO TEMPORARY, SWITCH TO LOGGING
            .whenComplete((v, t) -> handleFileTransferTaskCompletion());
    }

    @FXML
    private void handleReset() {
        if (!showConfirmationDialog("dialog.header.comparer-reset")) return;

        comparerService.handleReset();

        edited = false;

        lockButtons();
        backButton.setDisable(false);
        pathButton.setDisable(false);
        loadButton.setDisable(false);

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

    private void lockButtons() {
        backButton.setDisable(true);
        pathButton.setDisable(true);
        loadButton.setDisable(true);
        moveButton.setDisable(true);
        removeButton.setDisable(true);
        resetButton.setDisable(true);
    }

    private void updateUserInterface(String state, String listViewName, List<File> fileList) {
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
        });
    }

    private void handleLoadingTaskCompletion() {
        Platform.runLater(() -> {
            backButton.setDisable(false);
            resetButton.setDisable(false);

            stateText.setText(translate(STATE_DONE));

            taskProgressBar.setProgress(1);

            if (!duplicateFileList.isEmpty()) {
                moveButton.setDisable(false);
                removeButton.setDisable(false);
            }
        });
    }

    private void handleFileTransferTaskCompletion() {
        Platform.runLater(() -> {
            backButton.setDisable(false);
            resetButton.setDisable(false);

            stateText.setText(translate(STATE_DONE));

            taskProgressBar.setProgress(1);
        });
    }
}
