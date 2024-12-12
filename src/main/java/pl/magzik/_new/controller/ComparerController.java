package pl.magzik._new.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import pl.magzik.modules.comparer.ComparerCoordinator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ComparerController extends PanelController {

    private final ComparerCoordinator coordinator;

    public ComparerController() {
        this.coordinator = Controller.getModel().getCc();
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

    public void initialize() {
        lockButtons();
        backButton.setDisable(false);
        pathButton.setDisable(false);
        loadButton.setDisable(false);

        List<File> files = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            files.add(new File("txt.txt"));
        }
        ObservableList<File> list = FXCollections.observableList(files);

        originalListView.setItems(list);

        duplicateListView.setItems(FXCollections.observableList(List.of(new File("dup.txt"))));
    }

    @FXML
    private void handleLoading() {
        String path = pathTextField.getText();

        Objects.requireNonNull(path, "Path is null.");

        coordinator.setInput(path);
        lockButtons();
    }

    @FXML
    private void handleChoosePath() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(getStage());

        if (selectedDirectory != null) {
            pathTextField.setText(selectedDirectory.getAbsolutePath());
        }
    }

    private void lockButtons() {
        backButton.setDisable(true);
        pathButton.setDisable(true);
        loadButton.setDisable(true);
        moveButton.setDisable(true);
        removeButton.setDisable(true);
        resetButton.setDisable(true);
    }

}
