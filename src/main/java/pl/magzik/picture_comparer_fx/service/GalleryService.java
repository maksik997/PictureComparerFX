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

/* TODO: JAVADOC */

public class GalleryService implements AsyncTaskSupport {

    private static final Logger log = LoggerFactory.getLogger(GalleryService.class);

    private static final String DATA_FILE_NAME = "gallery.pfx";

    private final Path dataFile;

    private final GalleryModel model;

    private final ImageComparisonHelper comparisonHelper;

    public GalleryService(@NotNull GalleryModel model) {
        this.dataFile = PathResolver.getInstance().getDataDirectory().resolve(DATA_FILE_NAME);

        this.model = model;

        this.comparisonHelper = new ImageComparisonHelper(
            ImageComparisonHelper.buildFileOperator(model.isRecursiveMode()),
            ImageComparisonHelper.buildProcessor(model.isPerceptualHash(), model.isPixelByPixel())
        );
    }

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

    public void removeImages(@NotNull List<GalleryTableModel> entries) throws IOException {
        if (entries.isEmpty()) return;

        model.getGalleryData().removeAll(entries);
        log.info("Images removed from gallery successfully.");

        saveFiles();
    }

    public void deleteImagesFromDisk(@NotNull List<GalleryTableModel> entries) throws IOException {
        List<File> files = entries.stream()
            .map(GalleryTableModel::getFile)
            .toList();
        comparisonHelper.delete(files);

        removeImages(entries);
        log.info("Images deleted from disk successfully.");

        saveFiles();
    }

    public CompletableFuture<Void> removeDuplicates(@NotNull List<GalleryTableModel> entries) {
        List<File> files = entries.stream()
            .map(GalleryTableModel::getFile)
            .toList();

        return supplyAsyncTask(() -> comparisonHelper.validate(files))
            .thenApply(this::compareAndFlatten)
            .thenAccept(this::deleteFlattenedFiles);
    }

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

    private @NotNull String formatFileSize(long size) {
        if (size >= 1 << 30) return String.format("%.1f GB", size / (double)(1 << 30));
        if (size >= 1 << 20) return String.format("%.1f MB", size / (double)(1 << 20));
        if (size >= 1 << 10) return String.format("%.1f KB", size / (double)(1 << 10));
        return size + " B";
    }

    private @NotNull String getExtension(@NotNull File file) {
        return Optional.of(file.getName())
            .filter(name -> name.contains("."))
            .map(name -> name.substring(name.lastIndexOf('.') + 1))
            .orElse("");
    }

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

    private @NotNull List<File> renameFiles(@NotNull List<File> files) {
        String namePrefix = model.getNamePrefix();
        AtomicInteger i = new AtomicInteger(1);
        boolean lowercaseExtension = model.isLowercaseExtension();

        return files.stream()
            .map(file -> renameFile(file, namePrefix, i.getAndIncrement(), lowercaseExtension))
            .toList();
    }

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

    private @NotNull List<File> compareAndFlatten(@NotNull List<File> files) {
        try {
            return comparisonHelper.flatten(comparisonHelper.compare(files));
        } catch (IOException e) {
            throw new CompletionException(e);
        }
    }

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
