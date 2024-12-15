package pl.magzik._new.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Collection;

public class ComparerModel {

    private final ObservableList<File> loadedFiles;

    private final ObservableList<File> duplicateFiles;

    public ComparerModel() {
        this.loadedFiles = FXCollections.observableArrayList();
        this.duplicateFiles = FXCollections.observableArrayList();
    }

    public ObservableList<File> getLoadedFiles() {
        return loadedFiles;
    }

    public ObservableList<File> getDuplicateFiles() {
        return duplicateFiles;
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
