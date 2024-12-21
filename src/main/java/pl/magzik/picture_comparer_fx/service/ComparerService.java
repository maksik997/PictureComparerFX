package pl.magzik.picture_comparer_fx.service;

import org.jetbrains.annotations.NotNull;
import pl.magzik.picture_comparer_fx.model.ComparerModel;
import pl.magzik.picture_comparer_fx.service.helpers.ImageComparisonHelper;
import pl.magzik.picture_comparer_fx.base.async.AsyncTaskSupport;

import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/* TODO: ADD JAVADOC */

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

    public @NotNull CompletableFuture<List<File>> validateFiles(@NotNull File file) {
        return supplyAsyncTask(() -> comparisonHelper.validate(List.of(file)));
    }

    public @NotNull CompletableFuture<List<File>> compareFiles(@NotNull List<@NotNull File> files) {
        return supplyAsyncTask(() -> comparisonHelper.flatten(comparisonHelper.compare(files)));
    }

    public @NotNull CompletableFuture<Void> moveDuplicates() {
        return runAsyncTask(() -> {
            comparisonHelper.move(moveDestination, model.getDuplicateFiles());
            return null;
        });
    }

    public @NotNull CompletableFuture<Void> removeDuplicates() {
        return runAsyncTask(() -> {
            comparisonHelper.delete(model.getDuplicateFiles());
            return null;
        });
    }
}
