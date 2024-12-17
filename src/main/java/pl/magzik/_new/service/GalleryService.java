package pl.magzik._new.service;

import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.magzik._new.model.GalleryModel;
import pl.magzik._new.model.GalleryTableModel;
import pl.magzik.base.async.AsyncTaskSupport;
import pl.magzik.io.FileOperator;
import pl.magzik.predicates.ImageFilePredicate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GalleryService implements AsyncTaskSupport {

    private static final Logger log = LoggerFactory.getLogger(GalleryService.class);

    private final FileOperator fileOperator;

    private final GalleryModel model;

    public GalleryService(GalleryModel model) {
        this.fileOperator = new FileOperator(new ImageFilePredicate(), 1);

        this.model = model;
    }

    public void addImages(Collection<File> files) throws IOException {
        List<File> valid = fileOperator.load(files);
        List<GalleryTableModel> images = new ArrayList<>();

        for (File file : valid) images.add(transformFile(file));

        model.getGalleryData().addAll(images);
        log.info("Added image successfully.");
    }

    public void loadFiles() throws IOException {
        List<GalleryTableModel> images = new ArrayList<>();

        for (File file : new ArrayList<>(List.of(new File("/home/magzik/Downloads/TestSet (1)/img_1_1734463171877.png")))) { // TODO: CHANGE FOR MODEL ONE
            images.add(transformFile(file));
        }

        model.getGalleryData().addAll(images);
        log.info("Gallery images loaded. Found: {}",model.getGalleryData().size());
    }

    private @NotNull GalleryTableModel transformFile(@NotNull File file) throws IOException {
        BasicFileAttributes attrs = Files.readAttributes(file.toPath(), BasicFileAttributes.class);

        Image i = new Image("file:" + file.getAbsolutePath());
        String name = file.getName();
        String size = formatFileSize(Files.size(file.toPath()));
        String date = new SimpleDateFormat().format(attrs.lastModifiedTime().toMillis());

        return new GalleryTableModel(i, name, size, date);
    }


    private @NotNull String formatFileSize(long size) {
        if (size >= 1 << 30) return String.format("%.1f GB", size / (double)(1 << 30));
        if (size >= 1 << 20) return String.format("%.1f MB", size / (double)(1 << 20));
        if (size >= 1 << 10) return String.format("%.1f KB", size / (double)(1 << 10));
        return size + " B";
    }
}
