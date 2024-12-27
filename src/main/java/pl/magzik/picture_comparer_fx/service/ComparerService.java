package pl.magzik.picture_comparer_fx.service;

import org.jetbrains.annotations.NotNull;
import pl.magzik.picture_comparer_fx.model.ComparerModel;
import pl.magzik.picture_comparer_fx.service.helpers.ImageComparisonHelper;
import pl.magzik.picture_comparer_fx.base.async.AsyncTaskSupport;

import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * A service responsible for handling the comparison of image files and managing duplicates.
 * <p>
 * This service leverages {@link ImageComparisonHelper} to validate, compare, move, and delete duplicate image files asynchronously.
 * The service provides methods to handle tasks such as validating files, comparing images, and managing the resulting duplicates (moving or removing them).
 * </p>
 *
 * @see AsyncTaskSupport
 * @see ImageComparisonHelper
 */
public class ComparerService implements AsyncTaskSupport {

    private final ComparerModel model;

    private final File moveDestination;

    private final ImageComparisonHelper comparisonHelper;

    /**
     * Constructs a {@link ComparerService} instance using the provided model.
     * <p>
     * This constructor initializes the service with the model containing configurations like the destination for moving duplicates,
     * the recursive mode, and the selected comparison algorithms.
     * </p>
     *
     * @param model the {@link ComparerModel} containing the settings and data for the comparison process
     */
    public ComparerService(@NotNull ComparerModel model) {
        this.model = model;
        this.moveDestination = new File(model.getMoveDestination());
        this.comparisonHelper = new ImageComparisonHelper(
            ImageComparisonHelper.buildFileOperator(model.isRecursiveMode()),
            ImageComparisonHelper.buildProcessor(model.isPerceptualHash(), model.isPixelByPixel())
        );
    }

    /**
     * Validates a single image file by loading it asynchronously.
     * <p>
     * The file is validated to ensure it can be processed in the image comparison pipeline. This method runs asynchronously.
     * </p>
     *
     * @param file the image file to validate
     * @return a {@link CompletableFuture} that resolves to a list containing the validated file
     */
    public @NotNull CompletableFuture<List<File>> validateFiles(@NotNull File file) {
        return supplyAsyncTask(() -> comparisonHelper.validate(List.of(file)));
    }

    /**
     * Compares a list of image files asynchronously.
     * <p>
     * The files are compared using the configured algorithms (e.g., perceptual hash, pixel-by-pixel).
     * This method flattens the comparison results and returns a list of duplicate files found.
     * </p>
     *
     * @param files the list of image files to compare
     * @return a {@link CompletableFuture} that resolves to a list of duplicate image files
     */
    public @NotNull CompletableFuture<List<File>> compareFiles(@NotNull List<@NotNull File> files) {
        return supplyAsyncTask(() -> comparisonHelper.flatten(comparisonHelper.compare(files)));
    }

    /**
     * Moves the duplicate image files to the specified destination folder asynchronously.
     * <p>
     * This method processes the list of duplicate files, moving them to the destination folder configured in the model.
     * It runs asynchronously to avoid blocking the main thread.
     * </p>
     *
     * @return a {@link CompletableFuture} that resolves when the move operation completes
     */
    public @NotNull CompletableFuture<Void> moveDuplicates() {
        return runAsyncTask(() -> {
            comparisonHelper.move(moveDestination, model.getDuplicateFiles());
            return null;
        });
    }

    /**
     * Removes the duplicate image files asynchronously.
     * <p>
     * This method processes the list of duplicate files, deleting them from the file system.
     * It runs asynchronously to prevent blocking the main thread.
     * </p>
     *
     * @return a {@link CompletableFuture} that resolves when the remove operation completes
     */
    public @NotNull CompletableFuture<Void> removeDuplicates() {
        return runAsyncTask(() -> {
            comparisonHelper.delete(model.getDuplicateFiles());
            return null;
        });
    }
}
