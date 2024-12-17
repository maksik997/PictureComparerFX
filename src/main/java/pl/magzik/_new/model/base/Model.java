package pl.magzik._new.model.base;

import pl.magzik._new.model.ComparerModel;
import pl.magzik._new.model.GalleryModel;
import pl.magzik._new.model.SettingsModel;

public class Model {

    private final SettingsModel settingsModel;

    private final ComparerModel comparerModel;

    private final GalleryModel galleryModel;

    public Model() {
        this.settingsModel = new SettingsModel();
        this.comparerModel = new ComparerModel(
            settingsModel.pixelByPixelProperty(),
            settingsModel.perceptualHashProperty(),
            settingsModel.recursiveModeProperty(),
            settingsModel.moveDestinationProperty()
        );
        this.galleryModel = new GalleryModel();
    }

    public SettingsModel getSettingsModel() {
        return settingsModel;
    }

    public ComparerModel getComparerModel() {
        return comparerModel;
    }

    public GalleryModel getGalleryModel() {
        return galleryModel;
    }
}
