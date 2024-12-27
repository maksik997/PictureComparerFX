package pl.magzik.picture_comparer_fx.model;

import pl.magzik.picture_comparer_fx.model.access.GallerySettingsAccess;
import pl.magzik.picture_comparer_fx.model.access.IOComparerSettingsAccess;

import java.util.Set;

/**
 * The {@code SettingsModel} class represents the configuration settings for the application.
 * It holds general, comparer, and gallery settings, and implements the {@link IOComparerSettingsAccess}
 * and {@link GallerySettingsAccess} interfaces to provide access to specific configuration properties.
 * <p>
 * The model supports settings for language, theme, file comparison options, and gallery settings.
 * It is designed to be used for persisting, loading, and modifying user preferences in the application.
 * </p>
 */
public class SettingsModel implements IOComparerSettingsAccess, GallerySettingsAccess {

    /**
     * A set of supported languages in the format {@code "en-US", "pl-PL"}.
     * The language setting is used to configure the application's UI language.
     */
    private static final Set<String> languages = Set.of("en-US", "pl-PL");

    /**
     * A set of supported themes: light and dark modes.
     * The theme setting is used to configure the application's UI theme.
     */
    private static final Set<String> themes = Set.of("light", "dark");

    /**
     * Returns the available languages.
     *
     * @return a set of supported language codes
     */
    public static Set<String> getLanguages() {
        return languages;
    }

    /**
     * Returns the available themes.
     *
     * @return a set of supported theme names
     */
    public static Set<String> getThemes() {
        return themes;
    }

    // General settings
    private String language;
    private String theme;

    // Comparer settings
    private String moveDestination;
    private boolean recursiveMode;
    private boolean perceptualHash;
    private boolean pixelByPixel;

    // Gallery settings
    private String namePrefix;
    private boolean lowercaseExtension;

    /**
     * Constructs a new {@code SettingsModel} instance with default settings:
     * <ul>
     * <li>language: "en-US"</li>
     * <li>theme: "light"</li>
     * <li>moveDestination: current user directory</li>
     * <li>recursiveMode: false</li>
     * <li>perceptualHash: true</li>
     * <li>pixelByPixel: true</li>
     * <li>namePrefix: "img_"</li>
     * <li>lowercaseExtension: false</li>
     * </ul>
     */
    public SettingsModel() {
        this.language = "en-US";
        this.theme = "light";
        this.moveDestination = System.getProperty("user.dir");
        this.recursiveMode = false;
        this.perceptualHash = true;
        this.pixelByPixel = true;
        this.namePrefix = "img_";
        this.lowercaseExtension = false;
    }

    /**
     * Returns the current language setting.
     *
     * @return the language code (e.g., "en-US")
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Sets the language for the application.
     *
     * @param language the language code (e.g., "en-US")
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * Returns the current theme setting.
     *
     * @return the theme name (either "light" or "dark")
     */
    public String getTheme() {
        return theme;
    }

    /**
     * Sets the theme for the application.
     *
     * @param theme the theme name ("light" or "dark")
     */
    public void setTheme(String theme) {
        this.theme = theme;
    }

    /**
     * Returns the destination directory where files should be moved during comparison.
     *
     * @return the move destination directory path
     */
    @Override
    public String getMoveDestination() {
        return moveDestination;
    }

    /**
     * Sets the destination directory for moving files during comparison.
     *
     * @param moveDestination the destination directory path
     */
    public void setMoveDestination(String moveDestination) {
        this.moveDestination = moveDestination;
    }

    /**
     * Returns whether the recursive mode for file comparison is enabled.
     *
     * @return {@code true} if recursive mode is enabled, {@code false} otherwise
     */
    @Override
    public boolean isRecursiveMode() {
        return recursiveMode;
    }

    /**
     * Sets whether to use recursive mode for file comparison.
     *
     * @param recursiveMode {@code true} to enable recursive mode, {@code false} to disable
     */
    public void setRecursiveMode(boolean recursiveMode) {
        this.recursiveMode = recursiveMode;
    }

    /**
     * Returns whether perceptual hashing is enabled for file comparison.
     *
     * @return {@code true} if perceptual hashing is enabled, {@code false} otherwise
     */
    @Override
    public boolean isPerceptualHash() {
        return perceptualHash;
    }

    /**
     * Sets whether to use perceptual hashing for file comparison.
     *
     * @param perceptualHash {@code true} to enable perceptual hashing, {@code false} to disable
     */
    public void setPerceptualHash(boolean perceptualHash) {
        this.perceptualHash = perceptualHash;
    }

    /**
     * Returns whether pixel-by-pixel comparison is enabled for file comparison.
     *
     * @return {@code true} if pixel-by-pixel comparison is enabled, {@code false} otherwise
     */
    @Override
    public boolean isPixelByPixel() {
        return pixelByPixel;
    }

    /**
     * Sets whether to use pixel-by-pixel comparison for file comparison.
     *
     * @param pixelByPixel {@code true} to enable pixel-by-pixel comparison, {@code false} to disable
     */
    public void setPixelByPixel(boolean pixelByPixel) {
        this.pixelByPixel = pixelByPixel;
    }

    /**
     * Returns the prefix used for naming files in the gallery.
     *
     * @return the file name prefix
     */
    @Override
    public String getNamePrefix() {
        return namePrefix;
    }

    /**
     * Sets the prefix used for naming files in the gallery.
     *
     * @param namePrefix the file name prefix
     */
    public void setNamePrefix(String namePrefix) {
        this.namePrefix = namePrefix;
    }

    /**
     * Returns whether file extensions should be lowercase in the gallery.
     *
     * @return {@code true} if extensions should be lowercase, {@code false} otherwise
     */
    @Override
    public boolean isLowercaseExtension() {
        return lowercaseExtension;
    }

    /**
     * Sets whether to use lowercase extensions in the gallery.
     *
     * @param lowercaseExtension {@code true} to make extensions lowercase, {@code false} otherwise
     */
    public void setLowercaseExtension(boolean lowercaseExtension) {
        this.lowercaseExtension = lowercaseExtension;
    }
}
