package pl.magzik._new.controller;

import javafx.fxml.FXML;
import pl.magzik._new.controller.base.Controller;

public class MainController extends Controller {

    @FXML
    private void openComparer() {
        switchScene("/fxml/comparer-view.fxml");
    }

    @FXML
    private void openGallery() {
        switchScene("/fxml/gallery-view.fxml");
    }

    @FXML
    private void openSettings() {
        switchScene("/fxml/settings-view.fxml");
    }

    @FXML
    private void openCredits() {
        switchScene("/fxml/credits-view.fxml");
    }

    @FXML
    private void exitApp() {
        System.exit(0);
    }
}
