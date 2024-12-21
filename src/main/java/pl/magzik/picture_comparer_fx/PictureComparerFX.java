package pl.magzik.picture_comparer_fx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.magzik.picture_comparer_fx.controller.base.Controller;
import pl.magzik.picture_comparer_fx.model.SettingsModel;
import pl.magzik.picture_comparer_fx.model.base.Model;
import pl.magzik.picture_comparer_fx.service.GalleryService;
import pl.magzik.picture_comparer_fx.service.SettingsService;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class PictureComparerFX extends Application {

    private static final Logger log = LoggerFactory.getLogger(PictureComparerFX.class);

    private static final String MAIN_VIEW_FXML_PATH = "/fxml/main-view.fxml";

    private final Model model;

    public PictureComparerFX() {
        log.info("Initializing application...");
        this.model = new Model();
        Controller.setModel(model);
    }

    @Override
    public void start(Stage stage) throws Exception {
        log.info("Initializing GUI...");
        SettingsModel settingsModel = loadSettings();
        ResourceBundle bundle = loadResourceBundle(settingsModel);
        loadGallery();

        FXMLLoader loader = loadFXML(bundle);
        Parent root = loader.load();

        setupController(loader, stage, bundle, settingsModel);
        setupScene(stage, root, settingsModel);
        setupStage(stage);

        log.info("Application started successfully.");
    }

    private SettingsModel loadSettings() throws IOException {
        SettingsService settingsService = new SettingsService(model.getSettingsModel());
        settingsService.loadSettings();

        log.debug("Settings loaded.");
        return model.getSettingsModel();
    }

    private @NotNull ResourceBundle loadResourceBundle(@NotNull SettingsModel settingsModel) {
        Locale locale = Locale.forLanguageTag(settingsModel.getLanguage());
        log.debug("Locale set to: {}", locale);

        return ResourceBundle.getBundle("i18n.messages", locale);
    }

    private void loadGallery() throws IOException {
        GalleryService galleryService = new GalleryService(model.getGalleryModel());
        galleryService.loadFiles();

        log.debug("Gallery loaded.");
    }

    private @NotNull FXMLLoader loadFXML(@NotNull ResourceBundle bundle) {
        URL mainViewUrl = Controller.loadResource(MAIN_VIEW_FXML_PATH);
        return new FXMLLoader(mainViewUrl, bundle);
    }

    private void setupController(@NotNull FXMLLoader loader, @NotNull Stage stage, @NotNull ResourceBundle bundle, @NotNull SettingsModel settingsModel) {
        Controller controller = loader.getController();
        controller.setStage(stage);
        controller.setBundle(bundle);
        controller.setHostServices(getHostServices());
        controller.setCurrentTheme(settingsModel.getTheme());
    }

    private void setupScene(@NotNull Stage stage, @NotNull Parent root, @NotNull SettingsModel settingsModel) {
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Controller.loadResource("/styles/" + settingsModel.getTheme() + "_theme.css").toExternalForm());
        scene.getStylesheets().add(Controller.loadResource("/styles/stylesheet.css").toExternalForm());
        stage.setScene(scene);
    }

    private void setupStage(@NotNull Stage stage) {
        Image icon = new Image(Controller.loadResource("/fxml/images/thumbnail.png").toExternalForm());

        stage.getIcons().add(icon);
        stage.setTitle("Picture Comparer FX");
        stage.show();
        stage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });
    }
}
