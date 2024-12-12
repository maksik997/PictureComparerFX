package pl.magzik._new.controller.base;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import pl.magzik._new.model.Model;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class Controller {

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

    public void setBundle(ResourceBundle bundle) {
        this.bundle = bundle;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    protected void switchScene(String fxmlPath) {
        try {
            URL newView = Controller.class.getResource(fxmlPath);
            FXMLLoader loader = new FXMLLoader(newView, bundle);

            Parent root = loader.load();

            Controller controller = loader.getController();
            controller.setStage(stage);
            controller.setBundle(bundle);

            stage.setScene(new Scene(root));
        } catch (IOException e) {
            throw new RuntimeException(e); // TODO SWITCH TO LOGGING
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

    public boolean showConfirmationDialog(String headerText) {
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle(translate("dialog.title.confirmation"));
        dialog.setHeaderText(translate(headerText));
        dialog.setContentText(translate("dialog.context.confirmation"));

        dialog.initOwner(stage);

        Optional<ButtonType> result = dialog.showAndWait();

        return result.isPresent() && result.get() == ButtonType.OK;
    }
}
