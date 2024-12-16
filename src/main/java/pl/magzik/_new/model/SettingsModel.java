package pl.magzik._new.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Set;

public class SettingsModel {

    private static final Set<String> languages = Set.of("en-US", "pl-PL");
    private static final Set<String> themes = Set.of("light", "dark");

    public static Set<String> getLanguages() {
        return languages;
    }

    public static Set<String> getThemes() {
        return themes;
    }

    // General

    private final StringProperty language;

    private final StringProperty theme;

    // Comparer

    private final StringProperty moveDestination;

    private final BooleanProperty recursiveMode;

    private final BooleanProperty perceptualHash;

    private final BooleanProperty pixelByPixel;

    // Gallery

    private final StringProperty namePrefix;

    private final BooleanProperty lowercaseExtension;

    public SettingsModel() {
        this.language = new SimpleStringProperty("en-US");
        this.theme = new SimpleStringProperty("light");
        this.moveDestination = new SimpleStringProperty(System.getProperty("user.dir"));
        this.recursiveMode = new SimpleBooleanProperty(false);
        this.perceptualHash = new SimpleBooleanProperty(true);
        this.pixelByPixel = new SimpleBooleanProperty(true);
        this.namePrefix = new SimpleStringProperty("img_");
        this.lowercaseExtension = new SimpleBooleanProperty(false);
    }

    public String getLanguage() {
        return language.get();
    }

    public StringProperty languageProperty() {
        return language;
    }

    public String getTheme() {
        return theme.get();
    }

    public StringProperty themeProperty() {
        return theme;
    }

    public String getMoveDestination() {
        return moveDestination.get();
    }

    public StringProperty moveDestinationProperty() {
        return moveDestination;
    }

    public boolean isRecursiveMode() {
        return recursiveMode.get();
    }

    public BooleanProperty recursiveModeProperty() {
        return recursiveMode;
    }

    public boolean isPerceptualHash() {
        return perceptualHash.get();
    }

    public BooleanProperty perceptualHashProperty() {
        return perceptualHash;
    }

    public boolean isPixelByPixel() {
        return pixelByPixel.get();
    }

    public BooleanProperty pixelByPixelProperty() {
        return pixelByPixel;
    }

    public String getNamePrefix() {
        return namePrefix.get();
    }

    public StringProperty namePrefixProperty() {
        return namePrefix;
    }

    public boolean isLowercaseExtension() {
        return lowercaseExtension.get();
    }

    public BooleanProperty lowercaseExtensionProperty() {
        return lowercaseExtension;
    }
}
