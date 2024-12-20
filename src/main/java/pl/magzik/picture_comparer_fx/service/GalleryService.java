package pl.magzik.picture_comparer_fx.service;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.magzik.picture_comparer_fx.base.PathResolver;
import pl.magzik.picture_comparer_fx.model.GalleryModel;
import pl.magzik.picture_comparer_fx.model.GalleryTableModel;
import pl.magzik.picture_comparer_fx.service.helpers.ImageComparisonHelper;
import pl.magzik.picture_comparer_fx.base.async.AsyncTaskSupport;

import java.awt.*;
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
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicInteger;

public class GalleryService implements AsyncTaskSupport {

    private static final Logger log = LoggerFactory.getLogger(GalleryService.class);

    private static final String DATA_FILE_NAME = "gallery.pfx";

    private final File dataFile;

    private final GalleryModel model;

    private final ImageComparisonHelper comparisonHelper;

    public GalleryService(@NotNull GalleryModel model) {
        this.dataFile = new File(PathResolver.getInstance().getDataDirectory(), DATA_FILE_NAME);

        this.model = model;

        this.comparisonHelper = new ImageComparisonHelper(
            ImageComparisonHelper.buildFileOperator(model.isRecursiveMode()),
            ImageComparisonHelper.buildProcessor(model.isPerceptualHash(), model.isPixelByPixel())
        );
    }

    public void loadFiles() throws IOException {
        if (!dataFile.exists()) return;

        List<File> files = Files.readAllLines(dataFile.toPath())
                .stream()
                .map(File::new)
                .toList();

        List<GalleryTableModel> images = new ArrayList<>();

        for (File file : files) {
            images.add(transformFile(file));
        }

        model.getGalleryData().addAll(images);
        log.info("Gallery images loaded. Found: {}", model.getGalleryData().size());

        saveFiles();
    }

    private void saveFiles() throws IOException {
        List<File> data = model.getGalleryData().stream().map(GalleryTableModel::getFile).toList();

        try (BufferedWriter writer = Files.newBufferedWriter(dataFile.toPath())) {
            for (File f : data) {
                writer.write(f.getAbsolutePath());
                writer.newLine();
            }
        }

        log.info("Gallery saved successfully.");
    }

    public void addImages(@NotNull Collection<File> files) throws IOException {
        if (files.isEmpty()) return;

        List<File> valid = comparisonHelper.validate(files);
        List<GalleryTableModel> images = new ArrayList<>();

        for (File file : valid) images.add(transformFile(file));

        model.getGalleryData().addAll(images);

        saveFiles();
        log.info("Added image successfully.");
    }

    public void removeImages(@NotNull List<GalleryTableModel> entries) throws IOException {
        if (entries.isEmpty()) return;

        model.getGalleryData().removeAll(entries);

        saveFiles();
        log.info("Images removed from gallery successfully.");
    }

    public void deleteImagesFromDisk(@NotNull List<GalleryTableModel> entries) throws IOException {
        comparisonHelper.delete(
            entries.stream()
                .map(GalleryTableModel::getFile)
                .toList()
        );
        removeImages(entries);

        saveFiles();
        log.info("Images deleted from disk successfully.");
    }

    public void removeDuplicates(@NotNull List<GalleryTableModel> entries) {
        try {
            List<File> loadedFiles = entries.stream()
                .map(GalleryTableModel::getFile)
                .toList();

            List<File> duplicates = comparisonHelper.organise(
                comparisonHelper.compare(loadedFiles)
            );

            deleteImagesFromDisk(model.getGalleryData()
                .filtered(el -> duplicates.contains(el.getFile())));

            saveFiles();
            log.info("Duplicates removed successfully.");
        } catch (IOException e) {
            throw new CompletionException(e);
        }
    }

    public void renameAll(@NotNull List<GalleryTableModel> entries) {
        try {
            List<File> files = entries.stream().map(GalleryTableModel::getFile).toList();
            removeImages(entries);

            String namePrefix = model.getNamePrefix();
            AtomicInteger i = new AtomicInteger(1);
            boolean lowercaseExtension = model.isLowercaseExtension();

            files = files.stream().map(f -> {
                try {
                    Path newFile = renameFile(f, namePrefix, i.getAndIncrement(), lowercaseExtension);
                    Files.move(f.toPath(), newFile, StandardCopyOption.ATOMIC_MOVE);

                    return newFile.toFile();
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }).toList();

            addImages(files);

            saveFiles();
            log.info("Images renamed successfully.");
        } catch (UncheckedIOException e) {
            throw new CompletionException(e.getCause());
        } catch (IOException e) {
            throw new CompletionException(e);
        }
    }

    public void openImages(List<GalleryTableModel> files) {
        try {
            if (!Desktop.isDesktopSupported()) {
                log.error("Can't open image, because Desktop is not supported");
                return;
            }

            for (File f : files.stream().map(GalleryTableModel::getFile).toList()) {
                Desktop.getDesktop().open(f);
            }
        } catch (IOException e) {
            throw new CompletionException(e);
        }
    }

    private @NotNull GalleryTableModel transformFile(@NotNull File file) throws IOException {
        if (!file.exists() || !file.isFile()) {
            log.error("File: {} is doesn't exists or is not a file.", file);
            throw new IOException("The file is invalid.");
        }

        BasicFileAttributes attrs = Files.readAttributes(file.toPath(), BasicFileAttributes.class);

        String name = file.getName();
        String size = formatFileSize(Files.size(file.toPath()));
        String date = new SimpleDateFormat().format(attrs.lastModifiedTime().toMillis());

        return new GalleryTableModel(file, name, size, date);
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

    private @NotNull Path renameFile(@NotNull File file, String prefix, int idx, boolean lowercaseExtension) {
        String newName = String.format("%s%d_%d.%s",
            prefix,
            idx,
            System.currentTimeMillis(),
            lowercaseExtension ? getExtension(file).toLowerCase() : getExtension(file)
        );

        return file.toPath().resolveSibling(newName);
    }
}
