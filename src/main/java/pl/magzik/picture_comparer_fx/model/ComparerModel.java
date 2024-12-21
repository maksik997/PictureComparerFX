package pl.magzik.picture_comparer_fx.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.jetbrains.annotations.NotNull;
import pl.magzik.picture_comparer_fx.model.access.IOComparerSettingsAccess;

import java.io.File;
import java.util.Collection;

/* TODO: JAVADOC */

public class ComparerModel {

    private final ObservableList<File> loadedFiles;

    private final ObservableList<File> duplicateFiles;

    private final IOComparerSettingsAccess comparerSettings;

    public ComparerModel(IOComparerSettingsAccess comparerSettings) {
        this.loadedFiles = FXCollections.observableArrayList();
        this.duplicateFiles = FXCollections.observableArrayList();

        this.comparerSettings = comparerSettings;
    }

    public ObservableList<File> getLoadedFiles() {
        return loadedFiles;
    }

    public ObservableList<File> getDuplicateFiles() {
        return duplicateFiles;
    }

    public String getMoveDestination() {
        return comparerSettings.getMoveDestination();
    }

    public boolean isRecursiveMode() {
        return comparerSettings.isRecursiveMode();
    }

    public boolean isPerceptualHash() {
        return comparerSettings.isPerceptualHash();
    }

    public boolean isPixelByPixel() {
        return comparerSettings.isPixelByPixel();
    }

    public void clearLists() {
        loadedFiles.clear();
        duplicateFiles.clear();
    }

    public static void clearAndAddAll(@NotNull ObservableList<File> list, @NotNull Collection<File> files) {
        list.clear();
        list.addAll(files);
    }
}
