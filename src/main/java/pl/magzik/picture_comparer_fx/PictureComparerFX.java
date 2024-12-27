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

/**
 * Main application class for the PictureComparerFX application.
 * <p>
 *     This class is responsible for initializing and launching the JavaFX application.
 *     It handles the loading of settings, resources, and the main GUI, as well as setting up
 *     the application window and its behavior.
 * </p>
 * <p>
 *     The application starts by loading the settings and the gallery, followed by setting up the
 *     user interface with the appropriate locale, theme, and style. The main view is loaded from an
 *     FXML file, and the stage (window) is configured with an icon and a title. The app listens for
 *     close requests to properly shut down.
 * </p>
 */
public class PictureComparerFX extends Application {

    private static final Logger log = LoggerFactory.getLogger(PictureComparerFX.class);

    private static final String MAIN_VIEW_FXML_PATH = "/fxml/main-view.fxml";

    private final Model model;

    /**
     * Main entry point for launching the PictureComparerFX application.
     *
     * @param args command-line arguments passed to the application
     */
    public static void main(String[] args) {
        PictureComparerFX.launch(args);
    }

    /**
     * Constructor for initializing the PictureComparerFX application.
     * Sets up the model and prepares the controller for the main view.
     */
    public PictureComparerFX() {
        log.info("Initializing application...");
        this.model = new Model();
        Controller.setModel(model);
    }

    /**
     * Initializes and starts the JavaFX application by setting up the main GUI and stage.
     * This method is invoked automatically when the application is launched.
     *
     * @param stage the primary stage for the application
     * @throws Exception if an error occurs while loading resources or setting up the stage
     */
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

    /**
     * Loads the application settings from the settings service.
     *
     * @return the loaded settings model
     * @throws IOException if an error occurs while loading the settings
     */
    private SettingsModel loadSettings() throws IOException {
        SettingsService settingsService = new SettingsService(model.getSettingsModel());
        settingsService.loadSettings();

        log.debug("Settings loaded.");
        return model.getSettingsModel();
    }

    /**
     * Loads the appropriate resource bundle based on the language settings.
     *
     * @param settingsModel the settings model containing the language preference
     * @return the loaded resource bundle
     */
    private @NotNull ResourceBundle loadResourceBundle(@NotNull SettingsModel settingsModel) {
        Locale locale = Locale.forLanguageTag(settingsModel.getLanguage());
        log.debug("Locale set to: {}", locale);

        return ResourceBundle.getBundle("i18n.messages", locale);
    }

    /**
     * Loads the gallery files using the gallery service.
     *
     * @throws IOException if an error occurs while loading the gallery files
     */
    private void loadGallery() throws IOException {
        GalleryService galleryService = new GalleryService(model.getGalleryModel());
        galleryService.loadFiles();

        log.debug("Gallery loaded.");
    }

    /**
     * Loads the FXML for the main view.
     *
     * @param bundle the resource bundle used for localization
     * @return the FXMLLoader instance for the main view
     */
    private @NotNull FXMLLoader loadFXML(@NotNull ResourceBundle bundle) {
        URL mainViewUrl = Controller.loadResource(MAIN_VIEW_FXML_PATH);
        return new FXMLLoader(mainViewUrl, bundle);
    }

    /**
     * Sets up the controller for the main view, passing necessary dependencies.
     *
     * @param loader the FXMLLoader for the main view
     * @param stage the primary stage of the application
     * @param bundle the resource bundle for localization
     * @param settingsModel the settings model containing theme and other preferences
     */
    private void setupController(@NotNull FXMLLoader loader, @NotNull Stage stage, @NotNull ResourceBundle bundle, @NotNull SettingsModel settingsModel) {
        Controller controller = loader.getController();
        controller.setStage(stage);
        controller.setBundle(bundle);
        controller.setHostServices(getHostServices());
        controller.setCurrentTheme(settingsModel.getTheme());
    }

    /**
     * Sets up the scene for the application, including stylesheets.
     *
     * @param stage the primary stage of the application
     * @param root the root node of the scene graph
     * @param settingsModel the settings model used to apply the theme
     */
    private void setupScene(@NotNull Stage stage, @NotNull Parent root, @NotNull SettingsModel settingsModel) {
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Controller.loadResource("/styles/" + settingsModel.getTheme() + "_theme.css").toExternalForm());
        scene.getStylesheets().add(Controller.loadResource("/styles/stylesheet.css").toExternalForm());
        stage.setScene(scene);
    }

    /**
     * Configures the application stage with an icon and title, and handles the close request.
     *
     * @param stage the primary stage of the application
     */
    private void setupStage(@NotNull Stage stage) {
        Image icon = new Image(Controller.loadResource("/images/thumbnail.png").toExternalForm());

        stage.getIcons().add(icon);
        stage.setTitle("Picture Comparer FX");
        stage.show();
        stage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });
    }
}
