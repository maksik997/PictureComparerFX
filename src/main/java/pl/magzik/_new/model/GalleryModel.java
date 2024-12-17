package pl.magzik._new.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class GalleryModel {

    private final ObservableList<GalleryTableModel> galleryData;

    public GalleryModel() {
        this.galleryData = FXCollections.observableArrayList();
    }

    public ObservableList<GalleryTableModel> getGalleryData() {
        return galleryData;
    }
}
