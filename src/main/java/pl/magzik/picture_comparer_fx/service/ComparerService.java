package pl.magzik.picture_comparer_fx.service;

import javafx.application.Platform;
import org.jetbrains.annotations.NotNull;
import pl.magzik.picture_comparer_fx.model.ComparerModel;
import pl.magzik.picture_comparer_fx.service.helpers.ImageComparisonHelper;
import pl.magzik.picture_comparer_fx.base.async.AsyncTaskSupport;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CountDownLatch;

public class ComparerService implements AsyncTaskSupport {

    private final ComparerModel model;

    private final File moveDestination;

    private final ImageComparisonHelper comparisonHelper;

    public ComparerService(@NotNull ComparerModel model) {
        this.model = model;
        this.moveDestination = new File(model.getMoveDestination());
        this.comparisonHelper = new ImageComparisonHelper(
            ImageComparisonHelper.buildFileOperator(model.isRecursiveMode()),
            ImageComparisonHelper.buildProcessor(model.isPerceptualHash(), model.isPixelByPixel())
        );
    }

    public void validateFilesAsync(Collection<File> inputFiles) {
        try {
            CountDownLatch latch = new CountDownLatch(1);
            List<File> files = comparisonHelper.validate(inputFiles);

            Platform.runLater(() -> {
                ComparerModel.clearAndAddAll(
                    model.getLoadedFiles(),
                    files
                );

                latch.countDown();
            });

            try {
                latch.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        } catch (IOException e) {
            throw new CompletionException(e);
        }
    }

    public void validateFilesAsync(File... inputFiles) {
        validateFilesAsync(Arrays.asList(inputFiles));
    }


    public void compareFilesAsync() {
        try {
            CountDownLatch latch = new CountDownLatch(1);

            List<File> files = comparisonHelper.organise(
                comparisonHelper.compare(model.getLoadedFiles())
            );

            Platform.runLater(() -> {
                ComparerModel.clearAndAddAll(
                    model.getDuplicateFiles(),
                    files
                );

                latch.countDown();
            });

            try {
                latch.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        } catch (IOException e) {
            throw new CompletionException(e);
        }
    }

    public void moveDuplicatesAsync() {
        try {
            comparisonHelper.move(moveDestination, model.getDuplicateFiles());
        } catch (IOException e) {
            throw new CompletionException(e);
        }
    }

    public void removeDuplicatesAsync() {
        try {
            comparisonHelper.delete(model.getDuplicateFiles());
        } catch (IOException e) {
            throw new CompletionException(e);
        }
    }
}
