package pl.magzik.picture_comparer_fx.controller.base;

import javafx.fxml.FXML;

/**
 * The {@code PanelController} class extends the {@link Controller} class and serves as the controller for a specific panel in the application.
 * It provides a method for navigating back to the main menu.
 * This class is designed to handle actions specific to a panel in the user interface, such as switching scenes.
 */
public class PanelController extends Controller {

    /**
     * Handles the action of navigating back to the main menu by switching the scene to the main view.
     * This method is invoked by an event in the UI (such as a button click).
     */
    @FXML
    protected void backToMenu() {
        switchScene("/fxml/main-view.fxml");
    }
}
