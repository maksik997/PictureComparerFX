package pl.magzik.picture_comparer_fx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.magzik.picture_comparer_fx.base.PathResolver;
import pl.magzik.picture_comparer_fx.controller.base.Controller;
import pl.magzik.picture_comparer_fx.model.SettingsModel;
import pl.magzik.picture_comparer_fx.model.base.Model;
import pl.magzik.picture_comparer_fx.service.GalleryService;
import pl.magzik.picture_comparer_fx.service.SettingsService;

import java.net.URL;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

public class PictureComparerFX extends Application {

    private static Logger log;

    private static final String MAIN_VIEW_FXML_PATH = "/fxml/main-view.fxml";

    private final Model model;

    public static void main(String[] args) {

        boolean portableMode = args.length > 0 && args[0].equals("--portable"); // TODO: DUMMY, WILL BE CHANGED

        PathResolver.create(portableMode);
        PathResolver pathResolver = PathResolver.getInstance();
        System.setProperty("logPath", pathResolver.getLogDirectory().getAbsolutePath());
        log = LoggerFactory.getLogger(PictureComparerFX.class);

        log.warn("Arguments: {}", Arrays.toString(args));

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

        SettingsService settingsService = new SettingsService(model.getSettingsModel());
        settingsService.loadSettings();
        SettingsModel settingsModel = model.getSettingsModel();

        Locale locale = Locale.forLanguageTag(settingsModel.getLanguage());
        ResourceBundle bundle = ResourceBundle.getBundle("i18n.messages", locale);
        log.debug("Locale set to: {}", locale);

        GalleryService galleryService = new GalleryService(model.getGalleryModel());
        galleryService.loadFiles();

        URL mainViewUrl = PictureComparerFX.class.getResource(MAIN_VIEW_FXML_PATH);
        FXMLLoader loader = new FXMLLoader(mainViewUrl, bundle);
        Parent root = loader.load();
        log.debug("FXML loaded successfully from: {}", MAIN_VIEW_FXML_PATH);

        Controller controller = loader.getController();
        controller.setStage(stage);
        controller.setBundle(bundle);
        controller.setHostServices(getHostServices());
        controller.setCurrentTheme(settingsModel.getTheme());

        Scene scene = new Scene(root);

        String theme = Objects.requireNonNull(PictureComparerFX.class.getResource("/styles/" + settingsModel.getTheme() + "_theme.css")).toExternalForm();
        String stylesheet = Objects.requireNonNull(PictureComparerFX.class.getResource("/styles/stylesheet.css")).toExternalForm();
        scene.getStylesheets().add(theme);
        scene.getStylesheets().add(stylesheet);

         stage.setTitle("Picture Comparer FX");
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });
        log.info("Application started successfully.");
    }
}
