package pl.magzik.picture_comparer_fx.service;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.magzik.picture_comparer_fx.base.PathResolver;
import pl.magzik.picture_comparer_fx.model.SettingsModel;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * A service responsible for loading, saving, and updating the application settings.
 * <p>
 * This service manages the configuration file where application settings are stored.
 * It reads settings from the configuration file, applies them to the {@link SettingsModel},
 * and writes the updated settings back to the file when needed.
 * </p>
 */
public class SettingsService {

    private static final Logger log = LoggerFactory.getLogger(SettingsService.class);

    private static final String CONFIG_FILE_NAME = "config.cfg";

    private final Path configFile;

    private final SettingsModel model;

    /**
     * Constructs a {@link SettingsService} instance with the given {@link SettingsModel}.
     * <p>
     * Initializes the service with the path for the configuration file and checks if the file exists.
     * If the file doesn't exist, it calls {@link #saveSettings()} to create the default settings.
     * </p>
     *
     * @param model the {@link SettingsModel} that holds the application settings
     * @throws IOException if an error occurs while initializing the configuration file or loading settings
     */
    public SettingsService(SettingsModel model) throws IOException {
        this.configFile = PathResolver.getInstance().getConfigDirectory().resolve(CONFIG_FILE_NAME);
        this.model = model;

        if (Files.notExists(configFile))
            saveSettings();
    }

    /**
     * Saves the current settings from the {@link SettingsModel} to the configuration file.
     * <p>
     * This method writes the current settings, such as language, theme, move destination, and others,
     * into the configuration file. If the file doesn't exist, it is created.
     * </p>
     *
     * @throws IOException if an error occurs while writing the settings to the configuration file
     */
    public void saveSettings() throws IOException {
        Properties properties = new Properties();
        properties.setProperty("language", model.getLanguage());
        properties.setProperty("theme", model.getTheme());
        properties.setProperty("move.destination", model.getMoveDestination());
        properties.setProperty("recursive.mode", model.isRecursiveMode() ? "true" : "false");
        properties.setProperty("perceptual.hash", model.isPerceptualHash() ? "true" : "false");
        properties.setProperty("pixel.by.pixel", model.isPixelByPixel() ? "true" : "false");
        properties.setProperty("name.prefix", model.getNamePrefix());
        properties.setProperty("lowercase.extension", model.isLowercaseExtension() ? "true" : "false");

        try (FileOutputStream fos = new FileOutputStream(configFile.toFile())) {
            properties.store(fos, "PictureComparerFX Configuration File");
        }

        log.info("Settings saved to {} successfully.", configFile);
    }

    /**
     * Loads the settings from the configuration file into the {@link SettingsModel}.
     * <p>
     * This method reads the settings from the configuration file and applies them to the model.
     * If any setting is invalid or missing, default values are used.
     * </p>
     *
     * @throws IOException if an error occurs while reading from the configuration file
     */
    public void loadSettings() throws IOException {
        Properties properties = new Properties();

        try (FileInputStream fis = new FileInputStream(configFile.toFile())) {
            properties.load(fis);
        }

        updateModel(
            properties.getProperty("language", "en-US"),
            properties.getProperty("theme", "light"),
            properties.getProperty("move.destination", System.getProperty("user.dir")),
            Boolean.parseBoolean(properties.getProperty("recursive.mode", "false")),
            Boolean.parseBoolean(properties.getProperty("perceptual.hash", "true")),
            Boolean.parseBoolean(properties.getProperty("pixel.by.pixel", "true")),
            properties.getProperty("name.prefix", "img_"),
            Boolean.parseBoolean(properties.getProperty("lowercase.extension", "false"))
        );

        log.info("Settings loaded from {} successfully.", configFile);
    }

    /**
     * Updates the {@link SettingsModel} with the provided settings.
     * <p>
     * This method sets the provided values in the {@link SettingsModel} instance,
     * ensuring that invalid values are replaced with default values and logging warnings for them.
     * </p>
     *
     * @param language the language setting
     * @param theme the theme setting
     * @param moveDestination the move destination setting
     * @param recursiveMode the recursive mode setting
     * @param perceptualHash the perceptual hash setting
     * @param pixelByPixel the pixel by pixel setting
     * @param namePrefix the name prefix setting
     * @param lowercaseExtension the lowercase extension setting
     */
    public void updateModel(
            @NotNull String language,
            @NotNull String theme,
            @NotNull String moveDestination,
            boolean recursiveMode,
            boolean perceptualHash,
            boolean pixelByPixel,
            @NotNull String namePrefix,
            boolean lowercaseExtension
    ) {
        if (!language.matches("^[a-z]{2}-[A-Z]{2}$")) {
            log.warn("Invalid language format. Using default 'en-US'.");
            language = "en-US";
        }
        if (!SettingsModel.getLanguages().contains(language)) {
            log.warn("Invalid language. Using default 'en-US'.");
            language = "en-US";
        }
        model.setLanguage(language);

        if (!SettingsModel.getThemes().contains(theme)) {
            log.warn("Invalid theme. Using default 'light'.");
            theme = "light";
        }
        model.setTheme(theme);

        model.setMoveDestination(moveDestination);
        model.setRecursiveMode(recursiveMode);
        model.setPerceptualHash(perceptualHash);
        model.setPixelByPixel(pixelByPixel);
        model.setNamePrefix(namePrefix);
        model.setLowercaseExtension(lowercaseExtension);
    }
}
