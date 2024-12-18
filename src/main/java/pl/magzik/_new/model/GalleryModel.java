package pl.magzik._new.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class GalleryModel {

    private final ObservableList<GalleryTableModel> galleryData;

    private final BooleanProperty recursiveMode;

    private final BooleanProperty perceptualHash;

    private final BooleanProperty pixelByPixel;

    private final StringProperty namePrefix;

    private final BooleanProperty lowercaseExtension;

    public GalleryModel(BooleanProperty pixelByPixel, BooleanProperty perceptualHash, BooleanProperty recursiveMode, StringProperty namePrefix, BooleanProperty lowercaseExtension) {
        this.galleryData = FXCollections.observableArrayList();

        this.pixelByPixel = new SimpleBooleanProperty();
        this.perceptualHash = new SimpleBooleanProperty();
        this.recursiveMode = new SimpleBooleanProperty();
        this.namePrefix = new SimpleStringProperty();
        this.lowercaseExtension = new SimpleBooleanProperty();

        this.pixelByPixel.bind(pixelByPixel);
        this.perceptualHash.bind(perceptualHash);
        this.recursiveMode.bind(recursiveMode);
        this.namePrefix.bind(namePrefix);
        this.lowercaseExtension.bind(lowercaseExtension);
    }

    public ObservableList<GalleryTableModel> getGalleryData() {
        return galleryData;
    }

    public List<GalleryTableModel> getSelectedData() {
        return galleryData.filtered(el -> el.selectedProperty().get());
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

    public String getNamePrefix() {
        return namePrefix.get();
    }

    public boolean isLowercaseExtension() {
        return lowercaseExtension.get();
    }
}
