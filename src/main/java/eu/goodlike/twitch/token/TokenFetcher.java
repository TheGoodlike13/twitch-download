package eu.goodlike.twitch.token;

import eu.goodlike.libraries.jackson.Json;
import eu.goodlike.utils.StringFormatter;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public final class TokenFetcher {

    public Token generateNewToken(String vodId) {
        String finalUrl = StringFormatter.format(tokenUrl, vodId);
        Request request = new Request.Builder()
                .url(finalUrl)
                .build();

        Response response;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            throw new RuntimeException("HTTP request to Twitch failed: " + finalUrl, e);
        }
        Token token;
        try {
            token = Json.from(response.body().byteStream()).to(Token.class);
        } catch (IOException e) {
            throw new RuntimeException("Parsing token response failed", e);
        }
        return token;
    }

    // CONSTRUCTORS

    public TokenFetcher(OkHttpClient client) {
        this.client = client;
    }

    // PRIVATE

    private final OkHttpClient client;

    private static final String tokenUrl = "https://api.twitch.tv/api/vods/{}/access_token";

}
