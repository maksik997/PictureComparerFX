package pl.magzik.picture_comparer_fx.model.access;

/**
 * Interface that provides access to comparison-related settings for the application.
 * <p>
 * This interface allows retrieval of the settings related to the comparison mode in the picture comparison process.
 * The settings include options for enabling recursive comparison, perceptual hash comparison,
 * and pixel-by-pixel comparison.
 * </p>
 */
public interface ComparerSettingsAccess {

    /**
     * Returns whether the recursive mode is enabled for file comparison.
     * <p>
     * If enabled, the comparison will include files in subdirectories.
     * </p>
     *
     * @return true if recursive mode is enabled, false otherwise
     */
    boolean isRecursiveMode();

    /**
     * Returns whether the perceptual hash comparison method is enabled.
     * <p>
     * If enabled, the comparison will be based on perceptual hashing, which allows for comparing images
     * based on visual similarity rather than exact pixel matching.
     * </p>
     *
     * @return true if perceptual hash comparison is enabled, false otherwise
     */
    boolean isPerceptualHash();

    /**
     * Returns whether pixel-by-pixel comparison is enabled.
     * <p>
     * If enabled, the comparison will be based on exact pixel-by-pixel matching between images.
     * </p>
     *
     * @return true if pixel-by-pixel comparison is enabled, false otherwise
     */
    boolean isPixelByPixel();
}
