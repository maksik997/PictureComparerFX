package pl.magzik._new.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import pl.magzik._new.model.access.ComparerSettingsAccess;
import pl.magzik._new.model.access.GallerySettingsAccess;

import java.util.List;

public class GalleryModel {

    private final ObservableList<GalleryTableModel> galleryData;

    private final ComparerSettingsAccess comparerSettings;

    private final GallerySettingsAccess gallerySettings;

    public GalleryModel(ComparerSettingsAccess comparerSettings, GallerySettingsAccess gallerySettings) {
        this.galleryData = FXCollections.observableArrayList();

        this.comparerSettings = comparerSettings;
        this.gallerySettings = gallerySettings;
    }

    public ObservableList<GalleryTableModel> getGalleryData() {
        return galleryData;
    }

    public List<GalleryTableModel> getSelectedData() {
        return galleryData.filtered(el -> el.selectedProperty().get());
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

    public String getNamePrefix() {
        return gallerySettings.getNamePrefix();
    }

    public boolean isLowercaseExtension() {
        return gallerySettings.isLowercaseExtension();
    }
}
