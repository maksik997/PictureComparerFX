package pl.magzik.picture_comparer_fx.model.access;

/**
 * Interface that extends {@link ComparerSettingsAccess} to provide additional settings
 * related to the input/output file handling for image comparison.
 * <p>
 * This interface adds functionality to retrieve the move destination for duplicate images,
 * in addition to the comparison settings defined in the {@link ComparerSettingsAccess}.
 * </p>
 */
public interface IOComparerSettingsAccess extends ComparerSettingsAccess {

    /**
     * Returns the destination directory for moving duplicate images.
     * <p>
     * If duplicate images are found during the comparison process, they will be moved
     * to this specified directory.
     * </p>
     *
     * @return the move destination directory path
     */
    String getMoveDestination();
}
