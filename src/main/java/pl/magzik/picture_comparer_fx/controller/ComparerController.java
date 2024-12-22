package pl.magzik.picture_comparer_fx.controller;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.magzik.picture_comparer_fx.controller.base.Controller;
import pl.magzik.picture_comparer_fx.controller.base.PanelController;
import pl.magzik.picture_comparer_fx.model.ComparerModel;
import pl.magzik.picture_comparer_fx.service.ComparerService;
import pl.magzik.picture_comparer_fx.state.ComparerLoadingState;
import pl.magzik.picture_comparer_fx.state.ComparerReadyState;
import pl.magzik.picture_comparer_fx.state.ComparerResetState;
import pl.magzik.picture_comparer_fx.state.StateMachine;
import pl.magzik.picture_comparer_fx.state.base.State;

import java.io.File;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/* TODO: JAVADOC */

public class ComparerController extends PanelController {

    private static final Logger log = LoggerFactory.getLogger(ComparerController.class);

    private final ComparerModel model;

    private final ComparerService service;

    private final PieChart.Data originalSlice;

    private final PieChart.Data duplicateSlice;

    private boolean edited; ///< If true, then user will be prompted before closing the panel.

    private final StateMachine<State<ComparerController>, ComparerController> stateMachine;

    public ComparerController() {
        this.model = Controller.getModel().getComparerModel();
        this.service = new ComparerService(model);

        this.edited = false;

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

    public void initialize() {
        stateMachine.changeState(new ComparerResetState());
//        setButtonsState(true, moveButton, removeButton, resetButton);
//        setButtonsState(false, backButton, pathButton, loadButton);

        originalListView.setItems(model.getLoadedFiles());
        duplicateListView.setItems(model.getDuplicateFiles());

//        taskProgressBar.setProgress(1);

        duplicateRatioPieChart.getData().addAll(originalSlice, duplicateSlice);
    }

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

    @FXML
    private void handleLoadingFiles() {
        String path = pathTextField.getText();

        if (path == null || path.isBlank()) {
            log.warn("Path is blank or null.");
            showErrorDialog("dialog.context.error.path-empty");
            return;
        }

        log.info("Loading files from path: {}", path);
        stateMachine.changeState(new ComparerLoadingState());
        edited = true;
//        handleTaskStart(States.PREPARE);


        service.validateFiles(new File(path))
                .thenApply(files -> updateUserInterface(States.MAP, model.getLoadedFiles(), files))
                .thenCompose(service::compareFiles)
                .thenApply(files -> updateUserInterface(States.UPDATE, model.getDuplicateFiles(), files))
                .exceptionally(e -> handleTaskError("Error occurred while loading the files:", "dialog.context.error.comparer.loading", e))
                .whenComplete((v, e) -> handleTaskCompleted(this::handleLoadTaskCompleted));
    }

    @FXML
    private void handleMovingFiles() {
        handleFileTransferTask(
            service::moveDuplicates,
            "dialog.header.comparer-move",
            "Moving duplicated images...",
            States.MOVE
        );
    }

    @FXML
    private void handleRemovingFiles() {
        handleFileTransferTask(
            service::removeDuplicates,
            "dialog.header.comparer-remove",
            "Removing duplicated images...",
            States.REMOVE
        );
    }

    private void handleFileTransferTask(Supplier<CompletableFuture<Void>> task, String confirmationText, String logMsg, States state) {
        if (!showConfirmationDialog(confirmationText)) return;

        log.info(logMsg);
        handleTaskStart(state);

        task.get()
                .exceptionally(e -> handleTaskError("Error occurred while transferring the files:", "dialog.context.error.comparer.file-transfer", e))
                .whenComplete((v, t) -> handleTaskCompleted());
    }

    @FXML
    private void handleReset() {
        if (!showConfirmationDialog("dialog.header.comparer-reset")) return;

        log.info("Resetting the comparer's state...");
        stateMachine.changeState(new ComparerResetState());
        model.clearLists();

        edited = false;
/*

        setButtonsState(true, moveButton, removeButton, resetButton);
        setButtonsState(false, backButton, pathButton, loadButton);

        taskProgressBar.setProgress(1);
        stateText.setText(translate(States.READY.toString()));

        originalTrayTextField.setText("0");
        duplicateTrayTextField.setText("0");

        originalSlice.setPieValue(50);
        duplicateSlice.setPieValue(50);

        pathTextField.setText("");*/
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

    private List<File> updateUserInterface(States state, ObservableList<File> list, List<File> files) {
        Platform.runLater(() -> {
            ComparerModel.clearAndAddAll(list, files);
            int totalCount = model.getLoadedFiles().size();
            int duplicateCount = model.getDuplicateFiles().size();

            changeStateLabel(state);
            originalTrayTextField.setText(String.valueOf(totalCount));
            duplicateTrayTextField.setText(String.valueOf(duplicateCount));

            log.info("UI updated: {} files processed, {} duplicates found", totalCount, duplicateCount);
        });
        return files;
    }

    private void handleTaskStart(@NotNull ComparerController.States state) {
        lockState(state);
        getStage().getScene().setCursor(Cursor.WAIT);
    }

    private void handleTaskCompleted(@Nullable Runnable additionalOperations) {
        Platform.runLater(() -> {
            unlockState();
            getStage().getScene().setCursor(Cursor.DEFAULT);

            if (additionalOperations != null)
                additionalOperations.run();
        });
    }

    private void handleTaskCompleted() {
        handleTaskCompleted(null);
    }

    private void handleLoadTaskCompleted() {
        if (!model.getDuplicateFiles().isEmpty()) {
            moveButton.setDisable(false);
            removeButton.setDisable(false);
        }

        int duplicateCount = model.getDuplicateFiles().size();
        int originalCount = model.getLoadedFiles().size() - duplicateCount;

        log.debug("Calculated: duplicates: {}, originals: {}", duplicateCount, originalCount);

        originalSlice.setPieValue(originalCount);
        duplicateSlice.setPieValue(duplicateCount);
    }

    private <D> @Nullable D handleTaskError(String logMsg, String headerText, Throwable e) {
        log.error("{}{}", logMsg, e.getMessage(), e);
        Platform.runLater(() -> showErrorDialog(headerText));
        return null;
    }

    public enum States {
        READY("comparer.state.ready"),
        PREPARE("comparer.state.prepare"),
        MAP("comparer.state.map"),
        UPDATE("comparer.state.update"),
        MOVE("comparer.state.move"),
        REMOVE("comparer.state.remove"),
        DONE("comparer.state.done");

        private final String value;

        States(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    private void lockState(@NotNull ComparerController.States state) {
        setButtonsState(true, backButton, pathButton, loadButton, moveButton, removeButton, resetButton);
        stateText.setText(translate(state.toString()));
        taskProgressBar.setProgress(-1);
    }

    private void unlockState() {
        setButtonsState(false, backButton, resetButton);
        stateText.setText(translate(States.DONE.toString()));
        taskProgressBar.setProgress(1);
    }

    private void changeStateLabel(@NotNull ComparerController.States state) {
        stateText.setText(translate(state.toString()));
    }

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
