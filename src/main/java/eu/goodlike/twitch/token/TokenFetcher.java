package eu.goodlike.twitch.token;

import com.google.common.collect.ImmutableMap;
import eu.goodlike.io.log.CustomizedLogger;
import eu.goodlike.libraries.okhttp.HttpUrls;
import eu.goodlike.libraries.okhttp.JacksonCallback;
import eu.goodlike.twitch.download.configurations.policy.HttpRequestPolicy;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.util.concurrent.CompletableFuture;

import static eu.goodlike.twitch.TwitchDefaults.*;

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

        Request.Builder builder = new Request.Builder()
                .url(finalUrl)
                .addHeader(VERSION_3_HEADER_NAME, VERSION_3_HEADER_VALUE)
                .addHeader(CLIENT_ID_HEADER_NAME, httpRequestPolicy.getClientId());

        httpRequestPolicy.getOauth()
                .ifPresent(oauth -> builder.addHeader(OAUTH_HEADER_NAME, OAUTH_HEADER_VALUE_PREFIX + oauth));

        Request request = builder.build();
        return JacksonCallback.asFuture(client.newCall(request), Token.class);
    }

    // CONSTRUCTORS

    public TokenFetcher(HttpRequestPolicy httpRequestPolicy, OkHttpClient client, CustomizedLogger debugLogger) {
        this.httpRequestPolicy = httpRequestPolicy;
        this.client = client;
        this.debugLogger = debugLogger;
    }

    // PRIVATE

    private final HttpRequestPolicy httpRequestPolicy;

    private final OkHttpClient client;
    private final CustomizedLogger debugLogger;

    private static final String VOD_ID_VARIABLE = ":video_id";
    private static final String TOKEN_URL = "https://api.twitch.tv/api/vods/" + VOD_ID_VARIABLE + "/access_token";
    private static final HttpUrl TOKEN_HTTP_URL = HttpUrls.parse(TOKEN_URL)
            .orElseThrow(() -> new AssertionError("This url should be valid: " + TOKEN_URL));

    private static HttpUrl getUrlForVodId(int vodId) {
        return HttpUrls.insertPathVariables(TOKEN_HTTP_URL, ImmutableMap.of(VOD_ID_VARIABLE, String.valueOf(vodId)));
    }

}
