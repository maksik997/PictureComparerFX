package pl.magzik.picture_comparer_fx.model.access;

/**
 * Interface that provides access to gallery-related settings for the application.
 * <p>
 * This interface allows retrieval of settings related to the image gallery, including
 * the prefix used for naming images and whether the file extensions should be lowercase.
 * </p>
 */
public interface GallerySettingsAccess {

    /**
     * Returns the prefix used for naming images in the gallery.
     * <p>
     * The prefix is used when generating or renaming image files in the gallery.
     * </p>
     *
     * @return the name prefix
     */
    String getNamePrefix();

    /**
     * Returns whether the file extensions in the gallery should be lowercase.
     * <p>
     * If enabled, the extensions of all image files in the gallery will be converted to lowercase.
     * </p>
     *
     * @return true if lowercase extensions are enabled, false otherwise
     */
    boolean isLowercaseExtension();
}
