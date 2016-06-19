package eu.goodlike.twitch.token;

import eu.goodlike.libraries.okhttp.JacksonCallback;
import eu.goodlike.neat.Str;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.util.concurrent.CompletableFuture;

public final class TokenFetcher {

    public CompletableFuture<Token> generateNewToken(int vodId) {
        String finalUrl = Str.format(TOKEN_URL, vodId);
        System.out.println("Requesting token at: " + finalUrl);
        Request request = new Request.Builder()
                .url(finalUrl)
                .build();

        return JacksonCallback.asFuture(client.newCall(request), Token.class);
    }

    // CONSTRUCTORS

    public TokenFetcher(OkHttpClient client) {
        this.client = client;
    }

    // PRIVATE

    private final OkHttpClient client;

    private static final String TOKEN_URL = "https://api.twitch.tv/api/vods/{}/access_token";

}
