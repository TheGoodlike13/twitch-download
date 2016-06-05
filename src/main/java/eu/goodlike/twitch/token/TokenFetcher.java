package eu.goodlike.twitch.token;

import eu.goodlike.okhttp.JsonCallback;
import eu.goodlike.utils.StringFormatter;
import okhttp3.*;

import java.util.concurrent.CompletableFuture;

public final class TokenFetcher {

    public CompletableFuture<Token> generateNewToken(String vodId) {
        String finalUrl = StringFormatter.format(TOKEN_URL, vodId);
        Request request = new Request.Builder()
                .url(finalUrl)
                .build();

        return JsonCallback.asFuture(client.newCall(request), Token.class);
    }

    // CONSTRUCTORS

    public TokenFetcher(OkHttpClient client) {
        this.client = client;
    }

    // PRIVATE

    private final OkHttpClient client;

    private static final String TOKEN_URL = "https://api.twitch.tv/api/vods/{}/access_token";

}
