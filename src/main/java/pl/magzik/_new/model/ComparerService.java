package pl.magzik._new.model;

import pl.magzik.Processor;
import pl.magzik.algorithms.Algorithm;
import pl.magzik.algorithms.PerceptualHash;
import pl.magzik.algorithms.PixelByPixel;
import pl.magzik.base.async.AsyncTaskFactory;
import pl.magzik.grouping.CRC32Grouper;
import pl.magzik.grouping.Grouper;
import pl.magzik.io.FileOperator;
import pl.magzik.predicates.ImageFilePredicate;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletionException;

public class ComparerService implements AsyncTaskFactory {

    private final Processor processor;

    private final FileOperator fileOperator;

    private List<File> input;

    private List<File> output;

    private File moveDestination;

    public ComparerService() {
        Grouper grouper = new CRC32Grouper();

        List<Algorithm<?>> algorithms = new ArrayList<>(List.of(
            new PerceptualHash(), // TODO HANDLE SETTINGS ETC...
            new PixelByPixel() // TODO HANDLE SETTINGS ETC...
        ));

        this.processor = new Processor(grouper, algorithms);

        this.fileOperator = new FileOperator(
            new ImageFilePredicate(), // TODO HANDLE SETTINGS ETC...
            Integer.MAX_VALUE // TODO HANDLE SETTINGS ETC...
        );

        this.input = new ArrayList<>();
        this.output = new ArrayList<>();

        this.moveDestination = new File("/dev/null"); // TODO HANDLE SETTINGS ETC...
    }

    public List<File> getInput() {
        return List.copyOf(input);
    }

    public void setInput(List<File> input) {
        this.input = new ArrayList<>(input);
    }

    public void setInput(File... input) {
        setInput(Arrays.asList(input));
    }

    public List<File> getOutput() {
        return List.copyOf(output);
    }

    public void setOutput(List<File> output) {
        this.output = new ArrayList<>(output);
    }

    public void handleLoadFiles() {
        try {
            setInput(fileOperator.load(input));
        } catch (IOException e) {
            throw new CompletionException(e);
        }
    }

    public void handleCompareFiles() {
        try {
            Map<File, Set<File>> output = processor.process(input);
            output.forEach((key, value) -> value.remove(key));

            setOutput(output.values().stream()
                    .flatMap(Set::stream)
                    .toList()
            );
        } catch (IOException e) {
            throw new CompletionException(e);
        }
    }

    public void handleMoveFiles() {
        try {
            fileOperator.move(moveDestination, output);
        } catch (IOException e) {
            throw new CompletionException(e);
        }
    }

    public void handleRemoveFiles() {
        try {
            fileOperator.delete(output);
        } catch (IOException e) {
            throw new CompletionException(e);
        }
    }

    public void handleReset() {
        input = new ArrayList<>();
        output = new ArrayList<>();
    }
}
