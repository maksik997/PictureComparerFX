package pl.magzik.picture_comparer_fx.controller;

import javafx.fxml.FXML;
import pl.magzik.picture_comparer_fx.controller.base.PanelController;

/**
 * The {@code CreditsController} class is responsible for handling user interactions with the credits section
 * of the application. It provides functionality for displaying external links, such as the GitHub repository
 * for the project, when the user interacts with the associated UI element.
 * <p>
 * This class extends {@link PanelController} and leverages the host services to open external resources, such
 * as web pages, in the system's default browser.
 */
public class CreditsController extends PanelController {

    /**
     * Handles the user action of clicking on the hyperlink to open the project's GitHub page.
     * This method utilizes the host services to display the external link in the default web browser.
     */
    @FXML
    public void handleHyperLink() {
        getHostServices().showDocument("https://github.com/maksik997");
    }
}
