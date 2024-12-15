package pl.magzik._new;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.magzik._new.base.PathResolver;
import pl.magzik._new.controller.base.Controller;
import pl.magzik._new.model.Model;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class PictureComparerFX extends Application {

    private static Logger log;

    private static final String MAIN_VIEW_FXML_PATH = "/fxml/main-view.fxml";

    private final Model model;

    public static void main(String[] args) {

        boolean portableMode = args.length > 0 && args[0].equals("--portable"); // TODO: DUMMY, WILL BE CHANGED

        PathResolver pathResolver = new PathResolver(portableMode);
        System.setProperty("logPath", pathResolver.getLogDirectory().getAbsolutePath());
        log = LoggerFactory.getLogger(PictureComparerFX.class);

        log.info("Application is starting...");
        log.debug("Log path resolved to: {}", pathResolver.getLogDirectory());
        log.debug("LogPath property: {}" , System.getProperty("logPath"));

        launch(args);
    }

    public PictureComparerFX() {
        this.model = new Model();
        Controller.setModel(model);
    }

    @Override
    public void start(Stage stage) throws Exception {
        log.info("Initializing GUI...");

        Locale locale = Locale.forLanguageTag("en"); // TODO REFACTOR FOR SAVED IN SETTINGS
        ResourceBundle bundle = ResourceBundle.getBundle("i18n.messages", locale);
        log.debug("Locale set to: {}", locale);

        URL mainViewUrl = PictureComparerFX.class.getResource(MAIN_VIEW_FXML_PATH);
        FXMLLoader loader = new FXMLLoader(mainViewUrl, bundle);
        Parent root = loader.load();
        log.debug("FXML loaded successfully from: {}", MAIN_VIEW_FXML_PATH);

        Controller controller = loader.getController();
        controller.setStage(stage);
        controller.setBundle(bundle);

        stage.setTitle("Picture Comparer FX");
        stage.setScene(new Scene(root));
        stage.show();
        log.info("Application started successfully.");
    }
}
