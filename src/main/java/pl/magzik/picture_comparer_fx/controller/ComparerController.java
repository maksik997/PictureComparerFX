package pl.magzik.picture_comparer_fx.controller;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.magzik.picture_comparer_fx.controller.base.Controller;
import pl.magzik.picture_comparer_fx.controller.base.PanelController;
import pl.magzik.picture_comparer_fx.model.ComparerModel;
import pl.magzik.picture_comparer_fx.service.ComparerService;
import pl.magzik.picture_comparer_fx.state.*;
import pl.magzik.picture_comparer_fx.state.base.State;

import java.io.File;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * The {@code ComparerController} class is responsible for handling the user interface and interactions related to
 * comparing images. It manages the loading, moving, removing, and resetting of duplicate files, and displays the
 * progress and results of the comparison process in the UI.
 * <p>
 * This class extends {@link PanelController} and provides methods to switch between different states during
 * the image comparison process, including selecting directories, loading files, comparing files, and managing
 * duplicate files.
 */
public class ComparerController extends PanelController {

    private static final Logger log = LoggerFactory.getLogger(ComparerController.class);

    private final ComparerModel model;

    private final ComparerService service;

    private final PieChart.Data originalSlice;

    private final PieChart.Data duplicateSlice;

    private final StateMachine<State<ComparerController>, ComparerController> stateMachine;

    /**
     * Constructs a new {@code ComparerController}.
     */
    public ComparerController() {
        this.model = Controller.getModel().getComparerModel();
        this.service = new ComparerService(model);

        this.stateMachine = new StateMachine<>(this);

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

    /**
     * Initializes the UI components and binds the data model to the corresponding UI elements.
     */
    public void initialize() {
        originalListView.setItems(model.getLoadedFiles());
        duplicateListView.setItems(model.getDuplicateFiles());

        duplicateRatioPieChart.getData().addAll(originalSlice, duplicateSlice);
    }

    /**
     * Navigates back to the main menu. If the current state is not {@link ComparerResetState}, it will
     * show a confirmation dialog before returning to the menu.
     */
    @Override
    protected void backToMenu() {
        if (
            !(stateMachine.getCurrentState() instanceof ComparerResetState)
            && !showConfirmationDialog("dialog.header.back-menu")
        ) return;

        super.backToMenu();
    }

    /**
     * Sets the resource bundle for the UI.
     *
     * @param bundle the resource bundle containing the translations
     */
    @Override
    public void setBundle(ResourceBundle bundle) {
        super.setBundle(bundle);
        originalSlice.setName(translate("comparer.chart.label.originals"));
        duplicateSlice.setName(translate("comparer.chart.label.duplicates"));
        stateMachine.changeState(new ComparerResetState());
    }


    /**
     * Opens a directory chooser to select a directory for file comparison.
     *
     * @see DirectoryChooser
     */
    @FXML
    private void handleChoosePath() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(getStage());

        if (selectedDirectory != null) {
            pathTextField.setText(selectedDirectory.getAbsolutePath());

            stateMachine.changeState(new ComparerReadyState());
            log.info("Selected directory: {}", selectedDirectory.getAbsolutePath());
        } else {
            log.info("No directory selected.");
        }
    }

    /**
     * Initiates the file loading process. It validates the path and begins loading the files from the specified
     * directory, updating the UI as the process progresses.
     */
    @FXML
    private void handleLoadingFiles() {
        String path = pathTextField.getText();
        if (!validatePath(path)) return;

        log.info("Loading files from path: {}", path);
        stateMachine.changeState(new ComparerProcessingState(StatePhase.PREPARE));

        service.validateFiles(new File(path))
                .thenApply(files -> updateUserInterface(StatePhase.MAP, model.getLoadedFiles(), files))
                .thenCompose(service::compareFiles)
                .thenApply(files -> updateUserInterface(StatePhase.UPDATE, model.getDuplicateFiles(), files))
                .exceptionally(e -> handleTaskError("Error occurred while loading the files:", "dialog.context.error.comparer.loading", e))
                .whenComplete((v, e) -> handleLoadTaskCompleted());
    }

    /**
     * Validates if the given path is not null or blank.
     *
     * @param path the path to validate
     * @return {@code true} if the path is valid, {@code false} otherwise
     */
    private boolean validatePath(String path) {
        if (path == null || path.isBlank()) {
            log.warn("Path is blank or null.");
            showErrorDialog("dialog.context.error.path-empty");
            return false;
        }
        return true;
    }

    /**
     * Updates the user interface with the list of files, updating the loaded and duplicate file lists, and the pie chart.
     *
     * @param state the current state of the comparison process
     * @param list the list to update
     * @param files the list of files to add to the list
     * @return the updated list of files
     */
    private List<File> updateUserInterface(StatePhase state, ObservableList<File> list, List<File> files) {
        Platform.runLater(() -> {
            ComparerModel.clearAndAddAll(list, files);
            int totalCount = model.getLoadedFiles().size();
            int duplicateCount = model.getDuplicateFiles().size();

            stateMachine.changeState(new ComparerUpdateState(state, totalCount, duplicateCount));

            log.info("UI updated: {} files processed, {} duplicates found", totalCount, duplicateCount);
        });
        return files;
    }

    /**
     * Finalizes the loading process and updates the UI with the final number of duplicates and originals.
     */
    private void handleLoadTaskCompleted() {
        Platform.runLater(() -> {
            int duplicateCount = model.getDuplicateFiles().size();
            int originalCount = model.getLoadedFiles().size() - duplicateCount;
            log.debug("Calculated: duplicates: {}, originals: {}", duplicateCount, originalCount);

            stateMachine.changeState(new ComparerLoadCompletedState(originalCount, duplicateCount));
        });
    }

    /**
     * Handles the process of moving duplicate files. Displays a confirmation dialog and initiates the move operation.
     */
    @FXML
    private void handleMovingFiles() {
        handleFileTransferTask(
            service::moveDuplicates,
            "dialog.header.comparer-move",
            "Moving duplicated images...",
            StatePhase.MOVE
        );
    }

    /**
     * Handles the process of removing duplicate files. Displays a confirmation dialog and initiates the remove operation.
     */
    @FXML
    private void handleRemovingFiles() {
        handleFileTransferTask(
            service::removeDuplicates,
            "dialog.header.comparer-remove",
            "Removing duplicated images...",
            StatePhase.REMOVE
        );
    }

    /**
     * Handles the file transfer task (moving or removing duplicates). Initiates the task and shows a progress dialog.
     *
     * @param task the task to execute (move or remove)
     * @param confirmationText the text to display in the confirmation dialog
     * @param logMsg the log message to display during the operation
     * @param state the state to change to while the operation is in progress
     */
    private void handleFileTransferTask(Supplier<CompletableFuture<Void>> task, String confirmationText, String logMsg, StatePhase state) {
        if (!showConfirmationDialog(confirmationText)) return;

        log.info(logMsg);
        stateMachine.changeState(new ComparerProcessingState(state));

        task.get()
            .exceptionally(e -> handleTaskError("Error occurred while transferring the files:", "dialog.context.error.comparer.file-transfer", e))
            .whenComplete((v, t) -> Platform.runLater(() -> stateMachine.changeState(new ComparerPostProcessState())));
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
     * Resets the state of the comparer and clears all loaded and duplicate files.
     */
    @FXML
    private void handleReset() {
        if (!showConfirmationDialog("dialog.header.comparer-reset")) return;

        model.clearLists();
        stateMachine.changeState(new ComparerResetState());
        log.info("Comparer's state has been reset.");
    }

    /**
     * Enum representing the various phases of the comparison process.
     */
    public enum StatePhase {
        READY("comparer.state.ready"),
        PREPARE("comparer.state.prepare"),
        MAP("comparer.state.map"),
        UPDATE("comparer.state.update"),
        MOVE("comparer.state.move"),
        REMOVE("comparer.state.remove"),
        DONE("comparer.state.done");

        private final String value;

        StatePhase(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    // Getters for various UI components

    public Button getResetButton() {
        return resetButton;
    }

    public Button getRemoveButton() {
        return removeButton;
    }

    public Button getMoveButton() {
        return moveButton;
    }

    public Button getLoadButton() {
        return loadButton;
    }

    public Button getPathButton() {
        return pathButton;
    }

    public Button getBackButton() {
        return backButton;
    }

    public ProgressBar getTaskProgressBar() {
        return taskProgressBar;
    }

    public Text getStateText() {
        return stateText;
    }

    public PieChart.Data getDuplicateSlice() {
        return duplicateSlice;
    }

    public PieChart.Data getOriginalSlice() {
        return originalSlice;
    }

    public TextField getOriginalTrayTextField() {
        return originalTrayTextField;
    }

    public TextField getDuplicateTrayTextField() {
        return duplicateTrayTextField;
    }

    public TextField getPathTextField() {
        return pathTextField;
    }
}
