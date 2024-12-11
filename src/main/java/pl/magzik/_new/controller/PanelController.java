package pl.magzik._new.controller;

import javafx.fxml.FXML;

public class PanelController extends Controller {

    @FXML
    private void backToMenu() {
        switchScene("/fxml/main-view.fxml");
    }
}
