package pl.magzik.picture_comparer_fx.controller.base;

import javafx.application.HostServices;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.magzik.picture_comparer_fx.model.base.Model;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * The {@code Controller} class serves as the base controller for managing the user interface (UI) of the application.
 * It provides methods for switching scenes, managing application settings, handling internationalization (i18n),
 * and interacting with the model. The controller is responsible for managing the UI elements and their interactions
 * with the model, including handling theme changes, language translations, and confirmation/error dialogs.
 */
public class Controller {

    private static final Logger log = LoggerFactory.getLogger(Controller.class);

    private static volatile Model model;

    /**
     * Returns the {@link Model} instance associated with this controller.
     *
     * @return the {@code Model} instance
     * @throws IllegalStateException if the model has not been initialized
     */
    public static Model getModel() {
        if (Controller.model == null)
            throw new IllegalStateException("Model has not been initialized.");

        return Controller.model;
    }

    /**
     * Sets the {@link Model} instance for this controller.
     * This method should only be called once, as the model reference cannot be changed after initialization.
     *
     * @param model the {@code Model} instance to set
     * @throws UnsupportedOperationException if the model has already been initialized
     */
    public static synchronized void setModel(Model model) {
        if (Controller.model != null)
            throw new UnsupportedOperationException("Model reference already assigned.");

        Controller.model = model;
    }

    private ResourceBundle bundle;

    private Stage stage;

    private HostServices hostServices;

    private String currentTheme;

    /**
     * Returns the {@link HostServices} instance associated with this controller.
     *
     * @return the {@code HostServices} instance
     * @throws IllegalStateException if the {@code HostServices} has not been initialized
     */
    public HostServices getHostServices() {
        if (hostServices == null) {
            throw new IllegalStateException("HostServices not initialized.");
        }
        return hostServices;
    }

    /**
     * Sets the {@link HostServices} instance for this controller.
     *
     * @param hostServices the {@code HostServices} instance to set
     */
    public void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
    }

    /**
     * Sets the {@link ResourceBundle} for this controller, which is used for internationalization (i18n).
     *
     * @param bundle the {@code ResourceBundle} to set
     */
    public void setBundle(ResourceBundle bundle) {
        this.bundle = bundle;
    }

    /**
     * Returns the {@link Stage} associated with this controller.
     *
     * @return the {@code Stage} instance
     */
    public Stage getStage() {
        return stage;
    }

    /**
     * Sets the {@link Stage} associated with this controller.
     *
     * @param stage the {@code Stage} to set
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Sets the current theme for this controller.
     * The theme can either be "light" or "dark".
     *
     * @param currentTheme the theme to set ("light" or "dark")
     */
    public void setCurrentTheme(@NotNull String currentTheme) {
        this.currentTheme = currentTheme.equals("dark") ? "dark" : "light";
    }

    /**
     * Switches the current scene to the one specified by the provided FXML file path.
     *
     * @param fxmlPath the path to the FXML file to load
     */
    protected void switchScene(String fxmlPath) {
        try {
            URL newView = Controller.loadResource(fxmlPath);
            FXMLLoader loader = new FXMLLoader(newView, bundle);

            Parent root = loader.load();

            Controller controller = loader.getController();
            controller.setStage(stage);
            controller.setBundle(bundle);
            controller.setHostServices(hostServices);
            controller.setCurrentTheme(currentTheme);

            Scene scene = stage.getScene();
            if (scene == null) {
                scene = new Scene(root);
                stage.setScene(scene);
            } else {
                scene.setRoot(root);
            }

            scene.getStylesheets().setAll(
                Controller.loadResource("/styles/" + currentTheme + "_theme.css").toExternalForm(),
                Controller.loadResource("/styles/stylesheet.css").toExternalForm()
            );
        } catch (IOException e) {
            log.error("Couldn't switch scene, due to error: {}", e.getMessage(), e);
            showErrorDialog("dialog.context.error.scene-switch");
        }
    }

    /**
     * Translates the provided string key into the corresponding value from the {@link ResourceBundle}.
     * If the key is not found in the bundle, it returns the key itself surrounded by double square brackets.
     *
     * @param s the key to translate
     * @return the translated string, or the key if not found
     */
    public @NotNull String translate(@NotNull String s) {
        if (bundle != null && bundle.containsKey(s))
            return bundle.getString(s);

        log.warn("Missing translation key: {}", s);
        return "[[" + s + "]]";
    }

    /**
     * Finds the key corresponding to the provided value in the {@link ResourceBundle}.
     * If no key is found for the value, it returns the value itself.
     *
     * @param v the value to find the corresponding key for
     * @return the key for the value, or the value itself if not found
     */
    public @NotNull String findKey(@NotNull String v) {
        if (bundle == null) return v;

        return bundle.keySet().stream()
            .filter(k -> bundle.getString(k).equals(v))
            .findAny()
            .orElse(v);
    }

    /**
     * Creates and returns a new {@link Alert} of the specified type with the provided details.
     *
     * @param alertType the type of the alert
     * @param title the title of the alert
     * @param headerText the header text of the alert
     * @param contextText the content text of the alert
     * @return the created {@code Alert}
     */
    private @NotNull Alert createAlert(
        @NotNull Alert.AlertType alertType,
        @NotNull String title,
        @NotNull String headerText,
        @NotNull String contextText
    ) {
        Alert alert = new Alert(alertType);
        alert.setTitle(translate(title));
        alert.setHeaderText(translate(headerText));
        alert.setContentText(translate(contextText));

        if (stage != null && stage.getScene() != null) {
            alert.initOwner(stage);
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().addAll(stage.getScene().getStylesheets());
        }

        return alert;
    }

    /**
     * Displays a confirmation dialog with the provided header text and returns the result.
     *
     * @param headerText the header text of the confirmation dialog
     * @return {@code true} if the user clicked "OK", {@code false} otherwise
     */
    public boolean showConfirmationDialog(String headerText) {
        Alert alert = createAlert(
            Alert.AlertType.CONFIRMATION,
            "dialog.title.confirmation",
            headerText,
            "dialog.context.confirmation"
        );

        Optional<ButtonType> result = alert.showAndWait();

        return result.isPresent() && result.get() == ButtonType.OK;
    }

    /**
     * Displays an error dialog with the provided context text.
     *
     * @param contextText the context text for the error dialog
     */
    public void showErrorDialog(String contextText) {
        Alert alert = createAlert(
            Alert.AlertType.ERROR,
            "dialog.title.error",
            "dialog.header.error",
            contextText
        );

        alert.showAndWait();
    }

    /**
     * Enables or disables the provided buttons based on the given state.
     *
     * @param disable whether to disable or enable the buttons
     * @param buttons the buttons to enable/disable
     */
    public void setButtonsState(boolean disable, Button @NotNull ...buttons) {
        for (Button button : buttons) {
            button.setDisable(disable);
        }
    }

    /**
     * Loads the resource at the specified path and returns the {@link URL} for it.
     *
     * @param path the path to the resource
     * @return the {@code URL} of the resource
     * @throws IllegalStateException if the resource could not be found
     */
    public static @NotNull URL loadResource(@NotNull String path) {
        URL res = Controller.class.getResource(path);
        if (res == null) {
            log.error("Resource not found at path: {}", path);
            throw new IllegalStateException("Resource not found.");
        }
        return res;
    }
}
