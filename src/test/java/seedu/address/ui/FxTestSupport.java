package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;

/**
 * Helpers for tests that need the JavaFX toolkit.
 */
final class FxTestSupport {

    private FxTestSupport() {}

    /** Starts the JavaFX toolkit if it is not already running. */
    static void initialiseJavaFx() throws Exception {
        try {
            Platform.startup(() -> {
            });
        } catch (IllegalStateException e) {
            // JavaFX toolkit has already been initialized.
        }

        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(latch::countDown);
        assertTrue(latch.await(10, TimeUnit.SECONDS), "JavaFX toolkit did not initialise in time");
    }

    /** Runs {@code operation} on the JavaFX thread and returns its result. */
    static <T> T runOnFxThread(Callable<T> operation) throws Exception {
        FutureTask<T> task = new FutureTask<>(operation);
        Platform.runLater(task);
        return task.get(10, TimeUnit.SECONDS);
    }

    /** Runs {@code operation} on the JavaFX thread. */
    static void runOnFxThread(Runnable operation) throws Exception {
        runOnFxThread(() -> {
            operation.run();
            return null;
        });
    }
}
