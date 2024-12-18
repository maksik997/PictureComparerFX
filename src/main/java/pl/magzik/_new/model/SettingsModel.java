package pl.magzik._new.model;

import pl.magzik._new.model.access.GallerySettingsAccess;
import pl.magzik._new.model.access.IOComparerSettingsAccess;

import java.util.Set;

public class SettingsModel implements IOComparerSettingsAccess, GallerySettingsAccess {

    private static final Set<String> languages = Set.of("en-US", "pl-PL");
    private static final Set<String> themes = Set.of("light", "dark");

    public static Set<String> getLanguages() {
        return languages;
    }

    public static Set<String> getThemes() {
        return themes;
    }

    // General

    private String language;

    private String theme;

    // Comparer

    private String moveDestination;

    private boolean recursiveMode;

    private boolean perceptualHash;

    private boolean pixelByPixel;

    // Gallery

    private String namePrefix;

    private boolean lowercaseExtension;

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

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    @Override
    public String getMoveDestination() {
        return moveDestination;
    }

    public void setMoveDestination(String moveDestination) {
        this.moveDestination = moveDestination;
    }

    @Override
    public boolean isRecursiveMode() {
        return recursiveMode;
    }

    public void setRecursiveMode(boolean recursiveMode) {
        this.recursiveMode = recursiveMode;
    }

    @Override
    public boolean isPerceptualHash() {
        return perceptualHash;
    }

    public void setPerceptualHash(boolean perceptualHash) {
        this.perceptualHash = perceptualHash;
    }

    @Override
    public boolean isPixelByPixel() {
        return pixelByPixel;
    }

    public void setPixelByPixel(boolean pixelByPixel) {
        this.pixelByPixel = pixelByPixel;
    }

    @Override
    public String getNamePrefix() {
        return namePrefix;
    }

    public void setNamePrefix(String namePrefix) {
        this.namePrefix = namePrefix;
    }

    @Override
    public boolean isLowercaseExtension() {
        return lowercaseExtension;
    }

    public void setLowercaseExtension(boolean lowercaseExtension) {
        this.lowercaseExtension = lowercaseExtension;
    }
}
