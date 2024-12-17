package pl.magzik._new.model;

import javafx.beans.property.*;
import javafx.scene.image.Image;

public class GalleryTableModel {

    private final BooleanProperty selected;
    private final ObjectProperty<Image> icon;
    private final StringProperty filename;
    private final StringProperty fileSize;
    private final StringProperty modifiedDate;

    public GalleryTableModel(Image icon, String filename, String fileSize, String modifiedDate) {
        this.selected = new SimpleBooleanProperty(false);
        this.icon = new SimpleObjectProperty<>(icon);
        this.filename = new SimpleStringProperty(filename);
        this.fileSize = new SimpleStringProperty(fileSize);
        this.modifiedDate = new SimpleStringProperty(modifiedDate);
    }

    public BooleanProperty selectedProperty() {
        return selected;
    }

    public ObjectProperty<Image> iconProperty() {
        return icon;
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
