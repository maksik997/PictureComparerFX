package pl.magzik._new.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pl.magzik.Model;

import java.io.IOException;
import java.net.URL;
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
}
