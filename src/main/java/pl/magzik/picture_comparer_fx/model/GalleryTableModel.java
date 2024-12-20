package pl.magzik.picture_comparer_fx.model;

import javafx.beans.property.*;

import java.io.File;
import java.util.Objects;

public class GalleryTableModel {

    private final File file;

    private final BooleanProperty selected;
    private final StringProperty filename;
    private final StringProperty fileSize;
    private final StringProperty modifiedDate;

    public GalleryTableModel(File file, String filename, String fileSize, String modifiedDate) {
        this.file = file;
        this.selected = new SimpleBooleanProperty(false);
        this.filename = new SimpleStringProperty(filename);
        this.fileSize = new SimpleStringProperty(fileSize);
        this.modifiedDate = new SimpleStringProperty(modifiedDate);
    }

    public File getFile() {
        return file;
    }

    public BooleanProperty selectedProperty() {
        return selected;
    }

    public boolean isSelected() {
        return selected.get();
    }

    public StringProperty filenameProperty() {
        return filename;
    }

    public StringProperty fileSizeProperty() {
        return fileSize;
    }

    public StringProperty modifiedDateProperty() {
        return modifiedDate;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        GalleryTableModel that = (GalleryTableModel) o;
        return Objects.equals(file, that.file);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(file);
    }
}
