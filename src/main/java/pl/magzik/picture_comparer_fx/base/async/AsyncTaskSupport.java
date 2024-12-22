package pl.magzik.picture_comparer_fx.base.async;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;

/* TODO: JAVADOC REFACTOR */

/**
 * An interface for asynchronously executing a sequence of commands using an {@link ExecutorService}.
 * It provides a default method to chain execution of multiple commands asynchronously.
 * <p>
 * The commands are executed in the order they are provided, each asynchronously after the previous one completes.
 */
public interface AsyncTaskSupport {

    /**
     * The {@link ExecutorService} used to execute commands asynchronously.
     * It is obtained from the {@link ExecutorServiceManager} singleton instance.
     */
    ExecutorService executor = ExecutorServiceManager.getInstance().getExecutorService();

    /* TODO: JAVADOC */
    default <T> @NotNull CompletableFuture<T> supplyAsyncTask(@NotNull Callable<@NotNull T> task) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return task.call();
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }, executor);
    }

    /* TODO: JAVADOC */
    default @NotNull CompletableFuture<Void> runAsyncTask(@NotNull Callable<Void> task) {
        return CompletableFuture.runAsync(() -> {
            try {
                task.call();
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        });
    }
}
