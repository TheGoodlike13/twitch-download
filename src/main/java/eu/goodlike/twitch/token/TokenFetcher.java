package eu.goodlike.twitch.token;

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
public final class TokenFetcher {

    /**
     * @return token for given VoD Id
     */
    public CompletableFuture<Token> generateNewToken(int vodId) {
        HttpUrl finalUrl = getUrlForVodId(vodId);
        debugLogger.logMessage("Requesting token at: " + finalUrl);

        return twitchRequestMaker.makeRequest(finalUrl, Token.class);
    }

    // CONSTRUCTORS

    public TokenFetcher(TwitchRequestMaker twitchRequestMaker, CustomizedLogger debugLogger) {
        Null.check(twitchRequestMaker, debugLogger).ifAny("Twitch request maker and logger cannot be null");

        this.twitchRequestMaker = twitchRequestMaker;
        this.debugLogger = debugLogger;
    }

    // PRIVATE

    private final TwitchRequestMaker twitchRequestMaker;
    private final CustomizedLogger debugLogger;

    private static final String VOD_ID_VARIABLE = ":video_id";
    private static final String TOKEN_URL = "https://api.twitch.tv/api/vods/" + VOD_ID_VARIABLE + "/access_token";
    private static final HttpUrl TOKEN_HTTP_URL = HttpUrls.parse(TOKEN_URL)
            .orElseThrow(() -> new AssertionError("This url should be valid: " + TOKEN_URL));

    private static HttpUrl getUrlForVodId(int vodId) {
        return HttpUrls.insertPathVariables(TOKEN_HTTP_URL, ImmutableMap.of(VOD_ID_VARIABLE, String.valueOf(vodId)));
    }

}
