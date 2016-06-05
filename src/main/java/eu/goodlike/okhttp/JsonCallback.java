package eu.goodlike.okhttp;

import eu.goodlike.libraries.jackson.Json;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public final class JsonCallback<T> implements Callback {

    @Override
    public void onFailure(Call call, IOException e) {
        future.completeExceptionally(e);
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        T result;
        try {
            result = Json.from(response.body().byteStream()).to(clazz);
        } catch (IOException e) {
            future.completeExceptionally(e);
            return;
        }

        future.complete(result);
    }

    public CompletableFuture<T> enqueueFor(Call call) {
        call.enqueue(this);
        return future;
    }

    // CONSTRUCTORS

    public static <T> CompletableFuture<T> asFuture(Call call, Class<T> clazz) {
        return new JsonCallback<>(clazz).enqueueFor(call);
    }

    // PRIVATE

    private JsonCallback(Class<T> clazz) {
        this.future = new CompletableFuture<>();
        this.clazz = clazz;
    }

    private final CompletableFuture<T> future;
    private final Class<T> clazz;

}
