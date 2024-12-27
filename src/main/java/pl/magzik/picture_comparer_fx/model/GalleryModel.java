package pl.magzik.picture_comparer_fx.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.jetbrains.annotations.NotNull;
import pl.magzik.picture_comparer_fx.model.access.ComparerSettingsAccess;
import pl.magzik.picture_comparer_fx.model.access.GallerySettingsAccess;

import java.util.List;

/**
 * The {@code GalleryModel} class represents the model responsible for managing the gallery of images
 * in the application. It holds and provides access to the data related to the gallery, such as the list of images
 * and various settings related to the comparison and gallery management.
 * <p>
 * This model integrates with the {@link ComparerSettingsAccess} and {@link GallerySettingsAccess}
 * interfaces to retrieve the settings for comparing images (e.g., recursion, perceptual hashing, pixel-by-pixel comparison)
 * and gallery-specific settings (e.g., name prefix, lowercase file extensions).
 * </p>
 */
public class GalleryModel {

    private final ObservableList<GalleryTableModel> galleryData;

    private final ComparerSettingsAccess comparerSettings;

    private final GallerySettingsAccess gallerySettings;

    /**
     * Constructs a new {@code GalleryModel} instance with the specified settings.
     * This initializes the gallery data list and sets the access to the comparer and gallery settings.
     *
     * @param comparerSettings the settings object used to configure image comparison behavior
     * @param gallerySettings the settings object used to configure gallery management behavior
     */
    public GalleryModel(@NotNull ComparerSettingsAccess comparerSettings, @NotNull GallerySettingsAccess gallerySettings) {
        this.galleryData = FXCollections.observableArrayList();

        this.comparerSettings = comparerSettings;
        this.gallerySettings = gallerySettings;
    }

    /**
     * Returns the list of all gallery items (images) currently stored in the model.
     * <p>
     * This list is observable, meaning that any changes made to the list (e.g., adding or removing images) will be
     * reflected in UI components bound to it.
     * </p>
     *
     * @return an observable list of all gallery items
     */
    public ObservableList<GalleryTableModel> getGalleryData() {
        return galleryData;
    }

    /**
     * Returns a list of gallery items that are currently selected.
     * <p>
     * The selection state is determined by the {@code selectedProperty()} of each item.
     * </p>
     *
     * @return a list of selected gallery items
     */
    public List<GalleryTableModel> getSelectedData() {
        return galleryData.filtered(el -> el.selectedProperty().get());
    }

    /**
     * Returns whether the comparison process should be recursive, as specified in the comparer settings.
     *
     * @return {@code true} if recursive comparison is enabled, {@code false} otherwise
     */
    public boolean isRecursiveMode() {
        return comparerSettings.isRecursiveMode();
    }

    /**
     * Returns whether perceptual hashing should be used for comparing images, as specified in the comparer settings.
     *
     * @return {@code true} if perceptual hashing is enabled, {@code false} otherwise
     */
    public boolean isPerceptualHash() {
        return comparerSettings.isPerceptualHash();
    }

    /**
     * Returns whether a pixel-by-pixel comparison should be performed, as specified in the comparer settings.
     *
     * @return {@code true} if pixel-by-pixel comparison is enabled, {@code false} otherwise
     */
    public boolean isPixelByPixel() {
        return comparerSettings.isPixelByPixel();
    }

    /**
     * Returns the name prefix to be used when renaming gallery items, as specified in the gallery settings.
     *
     * @return the name prefix
     */
    public String getNamePrefix() {
        return gallerySettings.getNamePrefix();
    }

    /**
     * Returns whether file extensions should be converted to lowercase when renaming files, as specified in the gallery settings.
     *
     * @return {@code true} if file extensions should be lowercase, {@code false} otherwise
     */
    public boolean isLowercaseExtension() {
        return gallerySettings.isLowercaseExtension();
    }
}
