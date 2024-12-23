package pl.magzik.picture_comparer_fx.model.base;

import pl.magzik.picture_comparer_fx.model.ComparerModel;
import pl.magzik.picture_comparer_fx.model.GalleryModel;
import pl.magzik.picture_comparer_fx.model.SettingsModel;

/**
 * The {@code Model} class serves as a container for the core models used in the application:
 * {@link SettingsModel}, {@link ComparerModel}, and {@link GalleryModel}.
 * <p>
 * This class provides centralized access to these models, which are responsible for managing
 * the application's settings, image comparison logic, and gallery data respectively.
 * </p>
 */
public class Model {

    private final SettingsModel settingsModel;

    private final ComparerModel comparerModel;

    private final GalleryModel galleryModel;

    /**
     * Constructs a new {@code Model} instance, initializing all the core models:
     * {@link SettingsModel}, {@link ComparerModel}, and {@link GalleryModel}.
     * <p>
     * The {@code Model} constructor ensures that all required models are created and
     * initialized with appropriate dependencies.
     * </p>
     */
    public Model() {
        this.settingsModel = new SettingsModel();
        this.comparerModel = new ComparerModel(settingsModel);
        this.galleryModel = new GalleryModel(settingsModel, settingsModel);
    }

    /**
     * Returns the {@link SettingsModel} instance that holds the configuration settings for the application.
     *
     * @return the {@code SettingsModel} instance
     */
    public SettingsModel getSettingsModel() {
        return settingsModel;
    }

    /**
     * Returns the {@link ComparerModel} instance that contains the image comparison logic and data.
     *
     * @return the {@code ComparerModel} instance
     */
    public ComparerModel getComparerModel() {
        return comparerModel;
    }

    /**
     * Returns the {@link GalleryModel} instance that manages the image gallery data.
     *
     * @return the {@code GalleryModel} instance
     */
    public GalleryModel getGalleryModel() {
        return galleryModel;
    }
}
