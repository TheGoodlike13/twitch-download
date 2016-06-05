package eu.goodlike.okhttp;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public final class ResponseCallback implements Callback {

    @Override
    public void onFailure(Call call, IOException e) {
        future.completeExceptionally(e);
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        future.complete(response);
    }

    public CompletableFuture<Response> enqueueFor(Call call) {
        call.enqueue(this);
        return future;
    }

    // CONSTRUCTORS

    public static CompletableFuture<Response> asFuture(Call call) {
        return new ResponseCallback().enqueueFor(call);
    }

    public ResponseCallback() {
        this.future = new CompletableFuture<>();
    }

    // PRIVATE

    private final CompletableFuture<Response> future;

}
