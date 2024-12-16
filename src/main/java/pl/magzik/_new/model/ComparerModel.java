package pl.magzik._new.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Collection;

public class ComparerModel {

    private final ObservableList<File> loadedFiles;

    private final ObservableList<File> duplicateFiles;

    private final StringProperty moveDestination;

    private final BooleanProperty recursiveMode;

    private final BooleanProperty perceptualHash;

    private final BooleanProperty pixelByPixel;

    public ComparerModel(BooleanProperty pixelByPixel, BooleanProperty perceptualHash, BooleanProperty recursiveMode, StringProperty moveDestination) {
        this.loadedFiles = FXCollections.observableArrayList();
        this.duplicateFiles = FXCollections.observableArrayList();

        this.pixelByPixel = new SimpleBooleanProperty();
        this.perceptualHash = new SimpleBooleanProperty();
        this.recursiveMode = new SimpleBooleanProperty();
        this.moveDestination = new SimpleStringProperty();

        this.pixelByPixel.bind(pixelByPixel);
        this.perceptualHash.bind(perceptualHash);
        this.recursiveMode.bind(recursiveMode);
        this.moveDestination.bind(moveDestination);
    }

    public ObservableList<File> getLoadedFiles() {
        return loadedFiles;
    }

    public ObservableList<File> getDuplicateFiles() {
        return duplicateFiles;
    }

    public String getMoveDestination() {
        return moveDestination.get();
    }

    public boolean isRecursiveMode() {
        return recursiveMode.get();
    }

    public boolean isPerceptualHash() {
        return perceptualHash.get();
    }

    public boolean isPixelByPixel() {
        return pixelByPixel.get();
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
