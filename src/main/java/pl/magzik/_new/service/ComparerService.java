package pl.magzik._new.service;

import javafx.collections.ObservableList;
import pl.magzik.Processor;
import pl.magzik._new.model.ComparerModel;
import pl.magzik.algorithms.Algorithm;
import pl.magzik.algorithms.PerceptualHash;
import pl.magzik.algorithms.PixelByPixel;
import pl.magzik.base.async.AsyncTaskSupport;
import pl.magzik.grouping.CRC32Grouper;
import pl.magzik.grouping.Grouper;
import pl.magzik.io.FileOperator;
import pl.magzik.predicates.ImageFilePredicate;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletionException;

public class ComparerService implements AsyncTaskSupport {

    private final Processor processor;

    private final FileOperator fileOperator;

    private final ComparerModel model;

    private final File moveDestination; // TODO CONNECT SOMEHOW WITH SETTINGS

    public ComparerService(ComparerModel model) {
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

        this.model = model;

        this.moveDestination = new File("/dev/null"); // TODO HANDLE SETTINGS ETC...
    }

    public void loadFiles(Collection<File> inputFiles) {
        try {
            List<File> files = fileOperator.load(inputFiles);
            ObservableList<File> list = model.getLoadedFiles();
            ComparerModel.clearAndAddAll(list, files);
        } catch (IOException e) {
            throw new CompletionException(e);
        }
    }

    public void loadFiles(File... inputFiles) {
        loadFiles(Arrays.asList(inputFiles));
    }


    public void compareFiles() {
        try {
            List<File> loadedFiles = model.getLoadedFiles();
            ObservableList<File> duplicateFiles = model.getDuplicateFiles();

            Map<File, Set<File>> duplicates = processor.process(loadedFiles);
            duplicates.forEach((k, v) -> v.remove(k));

            ComparerModel.clearAndAddAll(
                duplicateFiles,
                duplicates.values().stream().flatMap(Set::stream).toList()
            );
        } catch (IOException e) {
            throw new CompletionException(e);
        }
    }

    public void moveDuplicates() {
        try {
            List<File> files = model.getDuplicateFiles();
            fileOperator.move(moveDestination, files);
        } catch (IOException e) {
            throw new CompletionException(e);
        }
    }

    public void removeDuplicates() {
        try {
            List<File> files = model.getDuplicateFiles();
            fileOperator.delete(files);
        } catch (IOException e) {
            throw new CompletionException(e);
        }
    }
}
