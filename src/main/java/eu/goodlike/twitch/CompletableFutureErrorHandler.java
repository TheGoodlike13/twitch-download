package eu.goodlike.twitch;

import eu.goodlike.functional.Futures;
import eu.goodlike.io.log.CustomizedLogger;

import java.util.IdentityHashMap;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * Handler for CompletableFuture errors, avoids unnecessary error messages for errors that have already occurred
 */
public final class CompletableFutureErrorHandler implements AutoCloseable {

    /**
     * @return BiConsumer to be used in a whenComplete() of a CompletableFuture; this consumer ensures that error
     * message for a particular error will be executed only once, the first time it is referenced
     */
    public <Result> BiConsumer<Result, Throwable> logOnError(String errorMessage) {
        return Futures.completionHandler(
                any -> {},
                throwable -> handleErrorJustOnce(throwable, errorMessage)
        );
    }

    @Override
    public void close() throws Exception {
        synchronized (lock) {
            handledExceptions.clear();
        }
    }

    // CONSTRUCTORS

    public CompletableFutureErrorHandler(CustomizedLogger debugLogger) {
        this.debugLogger = debugLogger;

        this.handledExceptions = new IdentityHashMap<Throwable, Object>().keySet();
    }

    // PRIVATE

    private final Set<Throwable> handledExceptions;
    private final CustomizedLogger debugLogger;

    private final Object lock = new Object();

    private void handleErrorJustOnce(Throwable error, String errorMessage) {
        synchronized (lock) {
            if (handledExceptions.add(error))
                debugLogger.logMessage(errorMessage);
        }
    }

}
