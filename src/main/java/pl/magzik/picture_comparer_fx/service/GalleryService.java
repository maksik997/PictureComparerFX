package pl.magzik.picture_comparer_fx.service;

import javafx.application.Platform;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.magzik.picture_comparer_fx.base.PathResolver;
import pl.magzik.picture_comparer_fx.model.GalleryModel;
import pl.magzik.picture_comparer_fx.model.GalleryTableModel;
import pl.magzik.picture_comparer_fx.service.helpers.ImageComparisonHelper;
import pl.magzik.picture_comparer_fx.base.async.AsyncTaskSupport;

import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A service responsible for managing the gallery of images, including loading, adding, removing, renaming,
 * and deleting images. It also supports removing duplicates and opening images.
 * <p>
 * This service interacts with the gallery model, validates and processes images with the {@link ImageComparisonHelper},
 * and provides asynchronous operations to handle image files.
 * </p>
 *
 * @see AsyncTaskSupport
 * @see ImageComparisonHelper
 */
public class GalleryService implements AsyncTaskSupport {

    private static final Logger log = LoggerFactory.getLogger(GalleryService.class);

    private static final String DATA_FILE_NAME = "gallery.pfx";

    private final Path dataFile;

    private final GalleryModel model;

    private final ImageComparisonHelper comparisonHelper;

    /**
     * Constructs a {@link GalleryService} instance with the given {@link GalleryModel}.
     * <p>
     * This constructor initializes the gallery service with a path for saving and loading gallery data,
     * and sets up the image comparison helper with the appropriate file operator and processor.
     * </p>
     *
     * @param model the {@link GalleryModel} that holds the gallery data and configurations
     */
    public GalleryService(@NotNull GalleryModel model) {
        this.dataFile = PathResolver.getInstance().getDataDirectory().resolve(DATA_FILE_NAME);

        this.model = model;

        this.comparisonHelper = new ImageComparisonHelper(
            ImageComparisonHelper.buildFileOperator(model.isRecursiveMode()),
            ImageComparisonHelper.buildProcessor(model.isPerceptualHash(), model.isPixelByPixel())
        );
    }

    /**
     * Loads the gallery images from the data file into the model's gallery data.
     * <p>
     * If the data file exists, the method reads all lines from the file, converts them into {@link GalleryTableModel}
     * objects, and adds them to the gallery model.
     * </p>
     *
     * @throws IOException if an error occurs while reading the data file or transforming the files
     */
    public void loadFiles() throws IOException {
        if (Files.notExists(dataFile)) return;

        try {
            List<GalleryTableModel> images = Files.readAllLines(dataFile)
                .stream()
                .map(File::new)
                .map(this::transformFile)
                .filter(Objects::nonNull)
                .toList();

            model.getGalleryData().addAll(images);
            log.info("Gallery images loaded. Found: {}", model.getGalleryData().size());
        } catch (UncheckedIOException e) {
            log.error("Couldn't load images from: {}", dataFile, e);
            throw e.getCause();
        }

        saveFiles();
    }

    /**
     * Saves the current gallery images to the data file.
     * <p>
     * This method writes the absolute paths of the gallery images to the data file, one per line.
     * </p>
     *
     * @throws IOException if an error occurs while writing to the data file
     */
    private void saveFiles() throws IOException {
        List<File> data = model.getGalleryData().stream().map(GalleryTableModel::getFile).toList();

        try (BufferedWriter writer = Files.newBufferedWriter(dataFile)) {
            for (File f : data) {
                writer.write(f.getAbsolutePath());
                writer.newLine();
            }
        }

        log.info("Gallery saved successfully.");
    }

    /**
     * Adds a collection of image files to the gallery.
     * <p>
     * The files are validated before being added to the gallery data, and duplicates are avoided.
     * </p>
     *
     * @param files the collection of image files to add to the gallery
     * @throws IOException if an error occurs while validating or adding the images
     */
    public void addImages(@NotNull Collection<File> files) throws IOException {
        if (files.isEmpty()) return;

        try {
            List<GalleryTableModel> images = comparisonHelper.validate(files)
                .stream()
                .map(this::transformFile)
                .filter(Objects::nonNull)
                .filter(gtm -> !model.getGalleryData().contains(gtm))
                .toList();

            model.getGalleryData().addAll(images);
            log.info("Added images successfully.");
        } catch (UncheckedIOException e) {
            log.error("Couldn't add images, due to: {}", e.getMessage(), e);
            throw e.getCause();
        }

        saveFiles();
    }

    /**
     * Removes images from the gallery.
     * <p>
     * This method removes the specified images from the gallery data and saves the updated gallery.
     * </p>
     *
     * @param entries the list of {@link GalleryTableModel} entries to remove
     * @throws IOException if an error occurs while removing images or saving the gallery
     */
    public void removeImages(@NotNull List<GalleryTableModel> entries) throws IOException {
        if (entries.isEmpty()) return;

        model.getGalleryData().removeAll(entries);
        log.info("Images removed from gallery successfully.");

        saveFiles();
    }

    /**
     * Deletes the specified images from disk and removes them from the gallery.
     * <p>
     * This method deletes the images from the file system and removes them from the gallery model.
     * </p>
     *
     * @param entries the list of {@link GalleryTableModel} entries to delete
     * @throws IOException if an error occurs while deleting the images or saving the updated gallery
     */
    public void deleteImagesFromDisk(@NotNull List<GalleryTableModel> entries) throws IOException {
        List<File> files = entries.stream()
            .map(GalleryTableModel::getFile)
            .toList();
        comparisonHelper.delete(files);

        removeImages(entries);
        log.info("Images deleted from disk successfully.");

        saveFiles();
    }

    /**
     * Removes duplicates from the gallery by comparing the specified images and deleting duplicates.
     * <p>
     * This method asynchronously validates the images, compares them to detect duplicates, and deletes the duplicates.
     * </p>
     *
     * @param entries the list of {@link GalleryTableModel} entries to check for duplicates
     * @return a {@link CompletableFuture} that resolves when the duplicate removal process is complete
     */
    public CompletableFuture<Void> removeDuplicates(@NotNull List<GalleryTableModel> entries) {
        List<File> files = entries.stream()
            .map(GalleryTableModel::getFile)
            .toList();

        return supplyAsyncTask(() -> comparisonHelper.validate(files))
            .thenApply(this::compareAndFlatten)
            .thenAccept(this::deleteFlattenedFiles);
    }

    /**
     * Renames all selected images according to the specified naming convention.
     * <p>
     * The images are renamed with a prefix and an index. Optionally, the file extensions can be changed to lowercase.
     * </p>
     *
     * @param entries the list of {@link GalleryTableModel} entries to rename
     * @return a {@link CompletableFuture} that resolves when the renaming process is complete
     */
    public CompletableFuture<Void> renameAll(@NotNull List<GalleryTableModel> entries) {
        return supplyAsyncTask(() -> entries)
        .thenApply(this::removeImagesAndReturnFiles)
        .thenApply(this::renameFiles)
        .thenAccept(f -> Platform.runLater(() -> {
            try {
                addImages(f);
            } catch (IOException e) {
                throw new CompletionException(e);
            }
        }));
    }

    /**
     * Opens the selected images using the system's default image viewer.
     * <p>
     * This method asynchronously opens each image in the list using {@link Desktop#getDesktop().open(File)}.
     * </p>
     *
     * @param entries the list of {@link GalleryTableModel} entries to open
     * @return a {@link CompletableFuture} that resolves when the images have been opened
     */
    public CompletableFuture<Void> openImages(List<GalleryTableModel> entries) {
        return runAsyncTask(() -> {
            entries.parallelStream()
                .map(GalleryTableModel::getFile)
                .forEach(f -> {
                    try {
                        Desktop.getDesktop().open(f);
                    } catch (IOException e) {
                        throw new CompletionException(e);
                    }
                });
            return null;
        });
    }

    /**
     * Transforms a {@link File} into a {@link GalleryTableModel}, extracting information like name, size, and last modified date.
     *
     * @param file the {@link File} to transform
     * @return a {@link GalleryTableModel} containing the file information, or {@code null} if the file is invalid
     */
    private @Nullable GalleryTableModel transformFile(@NotNull File file) {
        try {
            if (!file.exists() || !file.isFile()) {
                log.error("File: {} doesn't exists or is not a file.", file);
                return null;
            }

            BasicFileAttributes attrs = Files.readAttributes(file.toPath(), BasicFileAttributes.class);

            String name = file.getName();
            String size = formatFileSize(Files.size(file.toPath()));
            String date = new SimpleDateFormat().format(attrs.lastModifiedTime().toMillis());

            return new GalleryTableModel(file, name, size, date);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Formats the file size into a human-readable string (e.g., "1.2 MB").
     *
     * @param size the size of the file in bytes
     * @return a formatted string representing the file size
     */
    private @NotNull String formatFileSize(long size) {
        if (size >= 1 << 30) return String.format("%.1f GB", size / (double)(1 << 30));
        if (size >= 1 << 20) return String.format("%.1f MB", size / (double)(1 << 20));
        if (size >= 1 << 10) return String.format("%.1f KB", size / (double)(1 << 10));
        return size + " B";
    }

    /**
     * Extracts the file extension from the file name.
     *
     * @param file the file from which to extract the extension
     * @return the file extension as a string, or an empty string if no extension is found
     */
    private @NotNull String getExtension(@NotNull File file) {
        return Optional.of(file.getName())
            .filter(name -> name.contains("."))
            .map(name -> name.substring(name.lastIndexOf('.') + 1))
            .orElse("");
    }

    /**
     * Removes the specified images from the gallery and returns the associated files.
     *
     * @param entries the list of {@link GalleryTableModel} entries to remove
     * @return a list of {@link File} objects associated with the removed entries
     */
    private @NotNull List<File> removeImagesAndReturnFiles(@NotNull List<GalleryTableModel> entries) {
        List<File> files = entries.stream()
            .map(GalleryTableModel::getFile)
            .toList();

        try {
            removeImages(entries);
        } catch (IOException e) {
            throw new CompletionException(e);
        }

        return files;
    }

    /**
     * Renames a list of files according to the configured naming convention.
     *
     * @param files the list of {@link File} objects to rename
     * @return a list of renamed {@link File} objects
     */
    private @NotNull List<File> renameFiles(@NotNull List<File> files) {
        String namePrefix = model.getNamePrefix();
        AtomicInteger i = new AtomicInteger(1);
        boolean lowercaseExtension = model.isLowercaseExtension();

        return files.stream()
            .map(file -> renameFile(file, namePrefix, i.getAndIncrement(), lowercaseExtension))
            .toList();
    }

    /**
     * Renames a single file according to the specified naming convention.
     *
     * @param file the file to rename
     * @param prefix the prefix for the new name
     * @param idx the index to append to the file name
     * @param lowercaseExtension whether to use a lowercase extension
     * @return the renamed {@link File} object
     */
    private @NotNull File renameFile(@NotNull File file, String prefix, int idx, boolean lowercaseExtension) {
        String newName = String.format("%s%d_%d.%s",
            prefix,
            idx,
            System.currentTimeMillis(),
            lowercaseExtension ? getExtension(file).toLowerCase() : getExtension(file)
        );

        try {
            Path newFile = file.toPath().resolveSibling(newName);
            Files.move(file.toPath(), newFile, StandardCopyOption.ATOMIC_MOVE);

            return newFile.toFile();
        } catch (IOException e) {
            throw new CompletionException(e);
        }
    }

    /**
     * Compares the specified list of files and flattens the result to remove duplicates.
     *
     * @param files the list of {@link File} objects to compare
     * @return a list of files after comparison and flattening
     */
    private @NotNull List<File> compareAndFlatten(@NotNull List<File> files) {
        try {
            return comparisonHelper.flatten(comparisonHelper.compare(files));
        } catch (IOException e) {
            throw new CompletionException(e);
        }
    }

    /**
     * Deletes the specified files from disk.
     *
     * @param files the list of {@link File} objects to delete
     */
    private void deleteFlattenedFiles(@NotNull List<File> files) {
        Platform.runLater(() -> {
            try {
                deleteImagesFromDisk(
                    model.getGalleryData()
                        .filtered(el -> files.contains(el.getFile()))
                );
            } catch (IOException e) {
                throw new CompletionException(e);
            }
        });
    }
}
