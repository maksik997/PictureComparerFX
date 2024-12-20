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
import pl.magzik.picture_comparer_fx.PictureComparerFX;
import pl.magzik.picture_comparer_fx.model.base.Model;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

public class Controller {

    private static final Logger log = LoggerFactory.getLogger(Controller.class);

    private static Model model;

    public static Model getModel() {
        if (Controller.model == null)
            throw new NullPointerException("Model is null.");

        return Controller.model;
    }

    public static void setModel(Model model) {
        if (Controller.model != null)
            throw new IllegalStateException("Model reference already assigned.");

        Controller.model = model;
    }

    private ResourceBundle bundle;

    private Stage stage;

    private HostServices hostServices;

    private String currentTheme;

    public HostServices getHostServices() {
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
            URL newView = Controller.class.getResource(fxmlPath);
            FXMLLoader loader = new FXMLLoader(newView, bundle);

            Parent root = loader.load();

            Controller controller = loader.getController();
            controller.setStage(stage);
            controller.setBundle(bundle);
            controller.setHostServices(hostServices);
            controller.setCurrentTheme(currentTheme);

            Scene scene = new Scene(root);

            String theme = Objects.requireNonNull(PictureComparerFX.class.getResource("/styles/" + currentTheme + "_theme.css")).toExternalForm();
            String stylesheet = Objects.requireNonNull(PictureComparerFX.class.getResource("/styles/stylesheet.css")).toExternalForm();
            scene.getStylesheets().add(theme);
            scene.getStylesheets().add(stylesheet);

            stage.setScene(scene);
        } catch (IOException e) {
            log.error("Couldn't switch scene, due to error: {}", e.getMessage(), e);
            showErrorDialog("dialog.context.error.scene-switch");
        }
    }

    public String translate(String s) {
        if (bundle.containsKey(s))
            return bundle.getString(s);

        return s;
    }

    public String findKey(String v) {
        return bundle.keySet().stream()
            .filter(k -> bundle.getString(k).equals(v))
            .findAny()
            .orElse(v);
    }

    private Alert createAlert(Alert.AlertType alertType, String title, String headerText, String contextText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(translate(title));
        alert.setHeaderText(translate(headerText));
        alert.setContentText(translate(contextText));
        alert.initOwner(stage);

        String cssForm = Controller.class.getResource("/styles/stylesheet.css").toExternalForm();

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(cssForm);

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

    public void setButtonsState(boolean disable, Button... buttons) {
        for (Button button : buttons)
            button.setDisable(disable);
    }
}
