package pl.magzik._new;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class PictureComparerFX extends Application {

    private static final String MAIN_VIEW_FXML_PATH = "/fxml/main-view.fxml";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Locale locale = Locale.forLanguageTag("en"); // TODO REFACTOR FOR SAVED IN SETTINGS
        ResourceBundle bundle = ResourceBundle.getBundle("i18n.messages", locale);

        URL mainViewUrl = PictureComparerFX.class.getResource(MAIN_VIEW_FXML_PATH);
        FXMLLoader loader = new FXMLLoader(mainViewUrl, bundle);

        Parent root = loader.load();

        stage.setTitle("Picture Comparer FX");
        stage.setScene(new Scene(root));
        stage.show();
    }
}
