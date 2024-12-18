package pl.magzik._new.model;

import javafx.beans.property.*;

import java.io.File;

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
}
