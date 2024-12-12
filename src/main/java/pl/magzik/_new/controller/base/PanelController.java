package pl.magzik._new.controller.base;

import javafx.fxml.FXML;

public class PanelController extends Controller {

    @FXML
    protected void backToMenu() {
        switchScene("/fxml/main-view.fxml");
    }
}
