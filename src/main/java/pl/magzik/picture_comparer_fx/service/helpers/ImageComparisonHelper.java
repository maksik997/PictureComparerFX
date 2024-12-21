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

/* TODO: ADD JAVADOC */

public class ImageComparisonHelper {

    private static final Logger log = LoggerFactory.getLogger(ImageComparisonHelper.class);

    private final FileOperator fileOperator;

    private final Processor processor;

    public ImageComparisonHelper(@NotNull FileOperator fileOperator, @NotNull Processor processor) {
        this.fileOperator = fileOperator;
        this.processor = processor;
    }

    @Contract("_,_ -> new")
    public static @NotNull Processor buildProcessor(boolean perceptualHash, boolean pixelByPixel) {
        Grouper grouper = new CRC32Grouper();

        List<Algorithm<?>> algorithms = new ArrayList<>();

        if (perceptualHash) algorithms.add(new PerceptualHash());
        if (pixelByPixel) algorithms.add(new PixelByPixel());

        return new Processor(grouper, algorithms);
    }

    @Contract("_ -> new")
    public static @NotNull FileOperator buildFileOperator(boolean recursiveMode) {
        return new FileOperator(
            new ImageFilePredicate(),
            recursiveMode ? Integer.MAX_VALUE : 1
        );
    }

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

    public @NotNull Map<File, Set<File>> compare(@NotNull List<File> files) throws IOException {
        if (files.isEmpty()) {
            log.warn("No files to compare.");
            return Collections.emptyMap();
        }

        return processor.process(files);
    }

    public @NotNull List<File> flatten(@NotNull Map<File, Set<File>> map) {
        map.forEach((k, v) -> v.remove(k));
        return map.values().stream().flatMap(Set::stream).toList();
    }

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
