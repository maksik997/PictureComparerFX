package pl.magzik.picture_comparer_fx.service.helpers;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
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

public class ImageComparisonHelper {

    private final FileOperator fileOperator;

    private final Processor processor;

    public ImageComparisonHelper(FileOperator fileOperator, Processor processor) {
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
        return fileOperator.load(files);
    }

    public @NotNull Map<File, Set<File>> compare(@NotNull List<File> files) throws IOException {
        return processor.process(files);
    }

    public List<File> organise(@NotNull Map<File, Set<File>> map) {
        map.forEach((k, v) -> v.remove(k));
        return map.values().stream().flatMap(Set::stream).toList();
    }

    public void move(File destination, List<File> data) throws IOException {
        fileOperator.move(destination, data);
    }

    public void delete(List<File> data) throws IOException {
        fileOperator.delete(data);
    }
}
