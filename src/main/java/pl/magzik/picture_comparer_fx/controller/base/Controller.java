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

/* TODO: JAVADOC */

public class Controller {

    private static final Logger log = LoggerFactory.getLogger(Controller.class);

    private static volatile Model model;

    public static Model getModel() {
        if (Controller.model == null)
            throw new IllegalStateException("Model has not been initialized.");

        return Controller.model;
    }

    public static synchronized void setModel(Model model) {
        if (Controller.model != null)
            throw new UnsupportedOperationException("Model reference already assigned.");

        Controller.model = model;
    }

    private ResourceBundle bundle;

    private Stage stage;

    private HostServices hostServices;

    private String currentTheme;

    public HostServices getHostServices() {
        if (hostServices == null) {
            throw new IllegalStateException("HostServices not initialized.");
        }
        return hostServices;
    }

    public void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
    }

    public void setBundle(ResourceBundle bundle) {
        this.bundle = bundle;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setCurrentTheme(@NotNull String currentTheme) {
        this.currentTheme = currentTheme.equals("dark") ? "dark" : "light";
    }

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

    public @NotNull String translate(@NotNull String s) {
        if (bundle != null && bundle.containsKey(s))
            return bundle.getString(s);

        log.warn("Missing translation key: {}", s);
        return "[[" + s + "]]";
    }

    public @NotNull String findKey(@NotNull String v) {
        if (bundle == null) return v;

        return bundle.keySet().stream()
            .filter(k -> bundle.getString(k).equals(v))
            .findAny()
            .orElse(v);
    }

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

    public void showErrorDialog(String contextText) {
        Alert alert = createAlert(
            Alert.AlertType.ERROR,
            "dialog.title.error",
            "dialog.header.error",
            contextText
        );

        alert.showAndWait();
    }

    public void setButtonsState(boolean disable, Button @NotNull ...buttons) {
        for (Button button : buttons) {
            button.setDisable(disable);
        }
    }

    public static @NotNull URL loadResource(@NotNull String path) {
        URL res = Controller.class.getResource(path);
        if (res == null) {
            log.error("Resource not found at path: {}", path);
            throw new IllegalStateException("Resource not found.");
        }
        return res;
    }
}
