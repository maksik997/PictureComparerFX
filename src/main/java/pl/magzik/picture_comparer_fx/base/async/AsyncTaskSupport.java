package pl.magzik.picture_comparer_fx.base.async;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;

/**
 * Interface for supporting asynchronous task execution using an {@link ExecutorService}.
 * <p>
 * This interface allows for executing tasks asynchronously using an {@link ExecutorService}.
 * It provides default methods to execute tasks that return a result or void, which can be chained for sequential execution.
 * The tasks are executed in the order they are submitted, with each task starting after the previous one completes.
 * </p>
 * <p>
 * The {@link ExecutorService} used to execute tasks is retrieved from the {@link ExecutorServiceManager} singleton instance.
 * </p>
 */
public interface AsyncTaskSupport {

    /**
     * The {@link ExecutorService} used to execute tasks asynchronously.
     * It is provided by the {@link ExecutorServiceManager} singleton instance.
     */
    ExecutorService executor = ExecutorServiceManager.getInstance().getExecutorService();

    /**
     * Executes a task asynchronously that returns a result.
     * <p>
     * This method runs the provided task in the background and returns a {@link CompletableFuture} representing
     * the result of the computation. If the task fails, the exception is wrapped in a {@link CompletionException}.
     * </p>
     *
     * @param task the task to be executed asynchronously, which returns a result of type {@code T}
     * @param <T> the type of the result produced by the task
     * @return a {@link CompletableFuture} representing the result of the task execution
     */
    default <T> @NotNull CompletableFuture<T> supplyAsyncTask(@NotNull Callable<@NotNull T> task) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return task.call();
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }, executor);
    }

    /**
     * Executes a task asynchronously that does not return any result.
     * <p>
     * This method runs the provided task in the background and returns a {@link CompletableFuture} representing
     * the completion of the task. If the task fails, the exception is wrapped in a {@link CompletionException}.
     * </p>
     *
     * @param task the task to be executed asynchronously, which returns no result (void)
     * @return a {@link CompletableFuture} representing the completion of the task
     */
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
