package pl.magzik._new;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pl.magzik.Model;
import pl.magzik._new.controller.Controller;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class PictureComparerFX extends Application {

    private static final String MAIN_VIEW_FXML_PATH = "/fxml/main-view.fxml";

    private final Model model;

    public static void main(String[] args) {
        launch(args);
    }

    public PictureComparerFX() {
        this.model = new Model();

        Controller.setModel(model);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Locale locale = Locale.forLanguageTag("en"); // TODO REFACTOR FOR SAVED IN SETTINGS
        ResourceBundle bundle = ResourceBundle.getBundle("i18n.messages", locale);

        URL mainViewUrl = PictureComparerFX.class.getResource(MAIN_VIEW_FXML_PATH);
        FXMLLoader loader = new FXMLLoader(mainViewUrl, bundle);

        Parent root = loader.load();

        Controller controller = loader.getController();
        controller.setStage(stage);
        controller.setBundle(bundle);


        stage.setTitle("Picture Comparer FX");
        stage.setScene(new Scene(root));
        stage.show();
    }
}
