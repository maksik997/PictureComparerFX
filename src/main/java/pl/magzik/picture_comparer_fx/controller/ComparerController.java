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

/* TODO: JAVADOC */

public class ComparerController extends PanelController {

    private static final Logger log = LoggerFactory.getLogger(ComparerController.class);

    private final ComparerModel model;

    private final ComparerService service;

    private final PieChart.Data originalSlice;

    private final PieChart.Data duplicateSlice;

    private final StateMachine<State<ComparerController>, ComparerController> stateMachine;

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

    public void initialize() {
        originalListView.setItems(model.getLoadedFiles());
        duplicateListView.setItems(model.getDuplicateFiles());

        duplicateRatioPieChart.getData().addAll(originalSlice, duplicateSlice);
    }

    @Override
    protected void backToMenu() {
        if (
            !(stateMachine.getCurrentState() instanceof ComparerResetState)
            && !showConfirmationDialog("dialog.header.back-menu")
        ) return;

        super.backToMenu();
    }

    @Override
    public void setBundle(ResourceBundle bundle) {
        super.setBundle(bundle);
        originalSlice.setName(translate("comparer.chart.label.originals"));
        duplicateSlice.setName(translate("comparer.chart.label.duplicates"));
        stateMachine.changeState(new ComparerResetState());
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

    private boolean validatePath(String path) {
        if (path == null || path.isBlank()) {
            log.warn("Path is blank or null.");
            showErrorDialog("dialog.context.error.path-empty");
            return false;
        }
        return true;
    }

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

    private void handleLoadTaskCompleted() {
        Platform.runLater(() -> {
            int duplicateCount = model.getDuplicateFiles().size();
            int originalCount = model.getLoadedFiles().size() - duplicateCount;
            log.debug("Calculated: duplicates: {}, originals: {}", duplicateCount, originalCount);

            stateMachine.changeState(new ComparerLoadCompletedState(originalCount, duplicateCount));
        });
    }

    @FXML
    private void handleMovingFiles() {
        handleFileTransferTask(
            service::moveDuplicates,
            "dialog.header.comparer-move",
            "Moving duplicated images...",
            StatePhase.MOVE
        );
    }

    @FXML
    private void handleRemovingFiles() {
        handleFileTransferTask(
            service::removeDuplicates,
            "dialog.header.comparer-remove",
            "Removing duplicated images...",
            StatePhase.REMOVE
        );
    }

    private void handleFileTransferTask(Supplier<CompletableFuture<Void>> task, String confirmationText, String logMsg, StatePhase state) {
        if (!showConfirmationDialog(confirmationText)) return;

        log.info(logMsg);
        stateMachine.changeState(new ComparerProcessingState(state));

        task.get()
            .exceptionally(e -> handleTaskError("Error occurred while transferring the files:", "dialog.context.error.comparer.file-transfer", e))
            .whenComplete((v, t) -> Platform.runLater(() -> stateMachine.changeState(new ComparerPostProcessState())));
    }

    private <D> @Nullable D handleTaskError(String logMsg, String headerText, Throwable e) {
        log.error("{}{}", logMsg, e.getMessage(), e);
        Platform.runLater(() -> showErrorDialog(headerText));
        return null;
    }

    @FXML
    private void handleReset() {
        if (!showConfirmationDialog("dialog.header.comparer-reset")) return;

        model.clearLists();
        stateMachine.changeState(new ComparerResetState());
        log.info("Comparer's state has been reset.");
    }

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
