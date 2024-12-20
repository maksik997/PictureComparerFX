package pl.magzik.picture_comparer_fx.model.base;

import pl.magzik.picture_comparer_fx.model.ComparerModel;
import pl.magzik.picture_comparer_fx.model.GalleryModel;
import pl.magzik.picture_comparer_fx.model.SettingsModel;

public class Model {

    private final SettingsModel settingsModel;

    private final ComparerModel comparerModel;

    private final GalleryModel galleryModel;

    public Model() {
        this.settingsModel = new SettingsModel();
        this.comparerModel = new ComparerModel(settingsModel);
        this.galleryModel = new GalleryModel(settingsModel, settingsModel);
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
