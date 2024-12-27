package pl.magzik.picture_comparer_fx.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.jetbrains.annotations.NotNull;
import pl.magzik.picture_comparer_fx.model.access.IOComparerSettingsAccess;

import java.io.File;
import java.util.Collection;

/**
 * The {@code ComparerModel} class represents the model responsible for managing image files,
 * specifically the loaded files and identified duplicates.
 * <p>
 * This model interacts with the application's settings through the {@link IOComparerSettingsAccess}
 * interface to retrieve configuration options for file comparison, such as recursion, perceptual hashing,
 * and pixel-by-pixel comparison. It also provides methods for managing lists of loaded and duplicate files.
 * </p>
 */
public class ComparerModel {

    private final ObservableList<File> loadedFiles;

    private final ObservableList<File> duplicateFiles;

    private final IOComparerSettingsAccess comparerSettings;

    /**
     * Constructs a new {@code ComparerModel} instance, initializing the lists for loaded and duplicate files
     * and injecting the settings required for image comparison.
     *
     * @param comparerSettings the settings object used to configure the comparison behavior
     */
    public ComparerModel(IOComparerSettingsAccess comparerSettings) {
        this.loadedFiles = FXCollections.observableArrayList();
        this.duplicateFiles = FXCollections.observableArrayList();

        this.comparerSettings = comparerSettings;
    }

    /**
     * Returns the list of files currently loaded for comparison.
     * <p>
     * The list is observable, meaning that changes to the list (e.g., adding or removing files) will
     * be reflected in any UI components bound to it.
     * </p>
     *
     * @return an observable list of loaded files
     */
    public ObservableList<File> getLoadedFiles() {
        return loadedFiles;
    }

    /**
     * Returns the list of files that have been identified as duplicates during the comparison.
     * <p>
     * The list is observable, so UI components bound to this list will be updated when duplicates are found.
     * </p>
     *
     * @return an observable list of duplicate files
     */
    public ObservableList<File> getDuplicateFiles() {
        return duplicateFiles;
    }

    /**
     * Returns the destination directory where duplicate files should be moved, as specified in the application settings.
     *
     * @return the move destination directory
     */
    public String getMoveDestination() {
        return comparerSettings.getMoveDestination();
    }

    /**
     * Returns whether the comparison should be performed recursively within subdirectories.
     *
     * @return {@code true} if recursive comparison is enabled, {@code false} otherwise
     */
    public boolean isRecursiveMode() {
        return comparerSettings.isRecursiveMode();
    }

    /**
     * Returns whether the comparison should use perceptual hashing for image comparison.
     *
     * @return {@code true} if perceptual hashing is enabled, {@code false} otherwise
     */
    public boolean isPerceptualHash() {
        return comparerSettings.isPerceptualHash();
    }

    /**
     * Returns whether the comparison should be performed pixel-by-pixel.
     *
     * @return {@code true} if pixel-by-pixel comparison is enabled, {@code false} otherwise
     */
    public boolean isPixelByPixel() {
        return comparerSettings.isPixelByPixel();
    }

    /**
     * Clears both the loaded files and duplicate files lists.
     */
    public void clearLists() {
        loadedFiles.clear();
        duplicateFiles.clear();
    }

    /**
     * Clears the specified list and adds all files from the given collection to it.
     * This method is static and can be used to replace the contents of any list.
     *
     * @param list the list to clear and add files to
     * @param files the collection of files to add to the list
     */
    public static void clearAndAddAll(@NotNull ObservableList<File> list, @NotNull Collection<File> files) {
        list.clear();
        list.addAll(files);
    }
}
