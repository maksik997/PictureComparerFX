package pl.magzik._new.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ComparerController extends PanelController {

    @FXML
    private TextField pathTextField;

    @FXML
    private ListView<File> originalListView;

    @FXML
    private ListView<File> duplicateListView;

    public void initialize() {
        List<File> files = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            files.add(new File("txt.txt"));
        }
        ObservableList<File> list = FXCollections.observableList(files);

        originalListView.setItems(list);



        duplicateListView.setItems(FXCollections.observableList(List.of(new File("dup.txt"))));
    }

    @FXML
    private void handleChoosePath() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(getStage());

        if (selectedDirectory != null) {
            pathTextField.setText(selectedDirectory.getAbsolutePath());
        }
    }

}
