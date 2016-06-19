package eu.goodlike.twitch.stream;

import eu.goodlike.libraries.okhttp.JacksonCallback;
import eu.goodlike.neat.Str;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.util.concurrent.CompletableFuture;

public final class StreamDataFetcher {

    public CompletableFuture<StreamData> fetchStreamDataForVodId(int vodId) {
        String finalUrl = Str.format(STREAM_URL, vodId);
        System.out.println("Requesting stream data from: " + finalUrl);
        Request request = new Request.Builder()
                .url(finalUrl)
                .addHeader(TWITCH_ACCEPT_HEADER, TWITCH_ACCEPT_FORMAT)
                .addHeader(TWITCH_CLIENT_ID_HEADER, TWITCH_CLIENT_ID)
                .build();

        return JacksonCallback.asFuture(client.newCall(request), StreamData.class);
    }

    // CONSTRUCTORS

    public StreamDataFetcher(OkHttpClient client) {
        this.client = client;
    }

    // PRIVATE

    private final OkHttpClient client;

    private static final String STREAM_URL = "https://api.twitch.tv/kraken/videos/v{}";
    private static final String TWITCH_CLIENT_ID_HEADER = "Client-ID";
    private static final String TWITCH_CLIENT_ID = "nb79liikla455omvka8k0ck8z8x9fr8";
    private static final String TWITCH_ACCEPT_HEADER = "Accept";
    private static final String TWITCH_ACCEPT_FORMAT = "application/vnd.twitchtv.v3+json";

}
