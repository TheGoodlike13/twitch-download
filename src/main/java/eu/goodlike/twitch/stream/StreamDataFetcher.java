package eu.goodlike.twitch.stream;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import eu.goodlike.io.log.CustomizedLogger;
import eu.goodlike.libraries.okhttp.HttpUrls;
import eu.goodlike.neat.Null;
import eu.goodlike.twitch.download.http.TwitchRequestMaker;
import okhttp3.HttpUrl;

import java.util.concurrent.CompletableFuture;

/**
 * Fetches access tokens for given VoD id
 */
public final class StreamDataFetcher {

    /**
     * @return JSON containing stream data of given VoD id
     */
    public CompletableFuture<JsonNode> fetchStreamDataForVodId(int vodId) {
        HttpUrl finalUrl = getUrlForVodId(vodId);
        debugLogger.logMessage("Requesting stream data from: " + finalUrl);

        return twitchRequestMaker.makeRequest(finalUrl, JsonNode.class);
    }

    // CONSTRUCTORS

    public StreamDataFetcher(TwitchRequestMaker twitchRequestMaker, CustomizedLogger debugLogger) {
        Null.check(twitchRequestMaker, debugLogger).ifAny("Twitch request maker and logger cannot be null");

        this.twitchRequestMaker = twitchRequestMaker;
        this.debugLogger = debugLogger;
    }

    // PRIVATE

    private final TwitchRequestMaker twitchRequestMaker;
    private final CustomizedLogger debugLogger;

    private static final String VOD_ID_VARIABLE = ":id";
    private static final String STREAM_URL = "https://api.twitch.tv/kraken/videos/" + VOD_ID_VARIABLE;
    private static final HttpUrl STREAM_HTTP_URL = HttpUrls.parse(STREAM_URL)
            .orElseThrow(() -> new AssertionError("This url should be valid: " + STREAM_URL));

    private static HttpUrl getUrlForVodId(int vodId) {
        return HttpUrls.insertPathVariables(STREAM_HTTP_URL, ImmutableMap.of(VOD_ID_VARIABLE, "v" + vodId));
    }

}
