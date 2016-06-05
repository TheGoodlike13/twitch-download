package eu.goodlike.okhttp;

import okhttp3.OkHttpClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class ClientHandler implements AutoCloseable {

    public OkHttpClient getClient() {
        return client;
    }

    public void ensureCompletion(CompletableFuture<?> completableFuture) {
        listOfFutures.add(completableFuture);
    }

    @Override
    public void close() throws Exception {
        CompletableFuture<?>[] futures = listOfFutures.toArray(new CompletableFuture[listOfFutures.size()]);
        CompletableFuture.allOf(futures)
                .whenComplete((any, ex) -> client.dispatcher().executorService().shutdown());
    }

    // CONSTRUCTORS

    public ClientHandler(OkHttpClient client) {
        this.client = client;
        this.listOfFutures = new ArrayList<>();
    }

    // PRIVATE

    private final OkHttpClient client;
    private final List<CompletableFuture<?>> listOfFutures;

}
