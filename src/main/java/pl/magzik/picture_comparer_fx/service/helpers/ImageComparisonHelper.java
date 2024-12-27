package pl.magzik.picture_comparer_fx.service.helpers;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.magzik.Processor;
import pl.magzik.algorithms.Algorithm;
import pl.magzik.algorithms.PerceptualHash;
import pl.magzik.algorithms.PixelByPixel;
import pl.magzik.grouping.CRC32Grouper;
import pl.magzik.grouping.Grouper;
import pl.magzik.io.FileOperator;
import pl.magzik.predicates.ImageFilePredicate;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * A helper class that facilitates the comparison of image files.
 * It integrates file loading, image comparison algorithms, and file management operations.
 * <p>
 * This class provides various methods to validate, compare, flatten, move, and delete image files.
 * It allows configuring the comparison behavior using perceptual hash and pixel-by-pixel comparison algorithms.
 * </p>
 *
 * @see FileOperator
 * @see Processor
 */
public class ImageComparisonHelper {

    private static final Logger log = LoggerFactory.getLogger(ImageComparisonHelper.class);

    private final FileOperator fileOperator;

    private final Processor processor;

    /**
     * Constructs an {@link ImageComparisonHelper} instance with specified file operator and processor.
     *
     * @param fileOperator the {@link FileOperator} used for managing files
     * @param processor the {@link Processor} used for processing image comparison
     */
    public ImageComparisonHelper(@NotNull FileOperator fileOperator, @NotNull Processor processor) {
        this.fileOperator = fileOperator;
        this.processor = processor;
    }

    /**
     * Builds a {@link Processor} that is capable of comparing images using the specified algorithms.
     *
     * @param perceptualHash whether to use perceptual hashing for comparison
     * @param pixelByPixel whether to use pixel-by-pixel comparison
     * @return a configured {@link Processor} instance
     */
    @Contract("_,_ -> new")
    public static @NotNull Processor buildProcessor(boolean perceptualHash, boolean pixelByPixel) {
        Grouper grouper = new CRC32Grouper();

        List<Algorithm<?>> algorithms = new ArrayList<>();

        if (perceptualHash) algorithms.add(new PerceptualHash());
        if (pixelByPixel) algorithms.add(new PixelByPixel());

        return new Processor(grouper, algorithms);
    }

    /**
     * Builds a {@link FileOperator} for loading files, with an option for recursive file loading.
     *
     * @param recursiveMode whether to enable recursive mode for loading files
     * @return a configured {@link FileOperator} instance
     */
    @Contract("_ -> new")
    public static @NotNull FileOperator buildFileOperator(boolean recursiveMode) {
        return new FileOperator(
            new ImageFilePredicate(),
            recursiveMode ? Integer.MAX_VALUE : 1
        );
    }

    /**
     * Validates a collection of image files by loading them.
     * If the collection is empty, a warning is logged.
     *
     * @param files the collection of image files to validate
     * @return a list of validated image files
     * @throws IOException if an error occurs while loading files
     */
    public @NotNull List<File> validate(@NotNull Collection<@NotNull File> files) throws IOException {
        if (files.isEmpty()) {
            log.warn("No files to validate.");
            return List.of();
        }

        try {
            return fileOperator.load(files);
        } catch (IOException e) {
            log.error("Failed to validate {} files.", files.size());
            throw e;
        }
    }

    /**
     * Compares a list of validated image files using the configured {@link Processor}.
     * If the list is empty, a warning is logged.
     *
     * @param files the list of validated image files to compare
     * @return a map where the key is a file, and the value is a set of files that are considered duplicates of the key file
     * @throws IOException if an error occurs while comparing files
     */
    public @NotNull Map<File, Set<File>> compare(@NotNull List<File> files) throws IOException {
        if (files.isEmpty()) {
            log.warn("No files to compare.");
            return Collections.emptyMap();
        }

        return processor.process(files);
    }

    /**
     * Flattens a map of image comparisons by removing self-references (files compared to themselves) and
     * returning a flat list of all compared files.
     *
     * @param map a map of files and their corresponding duplicate files
     * @return a list of all compared files, excluding self-references
     */
    public @NotNull List<File> flatten(@NotNull Map<File, Set<File>> map) {
        map.forEach((k, v) -> v.remove(k));
        return map.values().stream().flatMap(Set::stream).toList();
    }

    /**
     * Moves a list of image files to a specified destination folder.
     * If the list is empty or the destination is invalid, a warning or error is logged.
     *
     * @param destination the destination folder to move the files to
     * @param data the list of image files to move
     * @throws IOException if an error occurs while moving the files
     */
    public void move(@NotNull File destination, @NotNull List<File> data) throws IOException {
        if (data.isEmpty()) {
            log.warn("No files to move to {}", destination.getPath());
            return;
        }

        if (!destination.exists() || !destination.isDirectory()) {
            log.error("Destination folder does not exist or is not a directory: {}", destination);
            throw new IllegalArgumentException("Invalid destination folder.");
        }

        try {
            fileOperator.move(destination, data);
            log.info("Successfully moved {} files to {}", data.size(), destination.getPath());
        } catch (IOException e) {
            log.error("Failed to move files to {} : {}", destination.getPath(), e.getMessage());
            throw e;
        }
    }

    /**
     * Deletes a list of image files.
     * If the list is empty, a warning is logged. If an error occurs during deletion, it is logged and thrown.
     *
     * @param data the list of image files to delete
     * @throws IOException if an error occurs while deleting the files
     */
    public void delete(@NotNull List<File> data) throws IOException {
        if (data.isEmpty()) {
            log.warn("No files to delete.");
            return;
        }

        try {
            fileOperator.delete(data);
            log.info("Successfully deleted {} files.", data.size());
        } catch (IOException e) {
            log.error("Failed to delete files: {}", e.getMessage());
            throw e;
        }
    }
}
