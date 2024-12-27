package pl.magzik.picture_comparer_fx.controller;

import javafx.fxml.FXML;
import pl.magzik.picture_comparer_fx.controller.base.Controller;

/**
 * The {@code MainController} class is responsible for managing the main navigation in the application.
 * It handles user interactions for switching between different views (e.g., Comparer, Gallery, Settings, and Credits).
 * This class extends {@link Controller}, providing functionality to change scenes within the application.
 * <p>
 * It provides methods for opening specific views and exiting the application.
 */
public class MainController extends Controller {

    /**
     * Switches the current scene to the "Comparer" view.
     */
    @FXML
    private void openComparer() {
        switchScene("/fxml/comparer-view.fxml");
    }

    /**
     * Switches the current scene to the "Gallery" view.
     */
    @FXML
    private void openGallery() {
        switchScene("/fxml/gallery-view.fxml");
    }

    /**
     * Switches the current scene to the "Settings" view.
     */
    @FXML
    private void openSettings() {
        switchScene("/fxml/settings-view.fxml");
    }

    /**
     * Switches the current scene to the "Credits" view.
     */
    @FXML
    private void openCredits() {
        switchScene("/fxml/credits-view.fxml");
    }

    /**
     * Exits the application, terminating the program.
     */
    @FXML
    private void exitApp() {
        System.exit(0);
    }
}
