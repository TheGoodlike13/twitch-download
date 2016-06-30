package eu.goodlike.twitch.download.http.filename;

import com.fasterxml.jackson.databind.JsonNode;
import eu.goodlike.io.log.CustomizedLogger;
import eu.goodlike.misc.Singleton;
import eu.goodlike.neat.Null;
import eu.goodlike.str.format.JsonTraversable;
import eu.goodlike.str.format.Traversable;

import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

/**
 * JsonTraversable which gets its value lazily (essentially avoiding network calls if they are not needed)
 */
public final class LazyJsonTraversable implements Traversable {

    @Override
    public Optional<String> getValueAt(String firstStep, String... otherSteps) {
        return lazilyGetJsonTraversable().flatMap(jsonTraversable -> jsonTraversable.getValueAt(firstStep, otherSteps));
    }

    // CONSTRUCTORS

    public static LazyJsonTraversable from(Supplier<CompletableFuture<JsonNode>> jsonNodeSupplier, CustomizedLogger debugLogger) {
        Null.check(jsonNodeSupplier).ifAny("Json node supplier cannot be null");
        return new LazyJsonTraversable(() -> jsonNodeSupplier.get().thenApply(JsonTraversable::new), debugLogger);
    }

    public LazyJsonTraversable(Supplier<CompletableFuture<JsonTraversable>> traversableSupplier, CustomizedLogger debugLogger) {
        Null.check(debugLogger).ifAny("Debug logger cannot be null");

        this.lazyTraversable = Singleton.lazy(traversableSupplier);
        this.debugLogger = debugLogger;
    }

    // PRIVATE

    private final Singleton<CompletableFuture<JsonTraversable>> lazyTraversable;
    private final CustomizedLogger debugLogger;

    private Optional<JsonTraversable> lazilyGetJsonTraversable() {
        JsonTraversable jsonTraversable;
        try {
            jsonTraversable = lazyTraversable.get().get();
        } catch (InterruptedException e) {
            debugLogger.logMessage("Unexpected interruption occurred when retrieving stream data");
            return Optional.empty();
        } catch (CancellationException e) {
            debugLogger.logMessage("Retrieval of stream data was cancelled unexpectedly");
            return Optional.empty();
        } catch (ExecutionException e) {
            debugLogger.logMessage("Could not return stream data");
            return Optional.empty();
        }
        return Optional.of(jsonTraversable);
    }

}
