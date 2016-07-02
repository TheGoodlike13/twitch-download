package eu.goodlike.twitch.playlist;

import com.google.common.collect.ImmutableMap;
import eu.goodlike.functional.Futures;
import eu.goodlike.io.log.CustomizedLogger;
import eu.goodlike.libraries.okhttp.HttpUrls;
import eu.goodlike.twitch.CompletableFutureErrorHandler;
import eu.goodlike.twitch.download.http.TwitchRequestMaker;
import eu.goodlike.twitch.m3u8.TwitchM3U8Parser;
import eu.goodlike.twitch.m3u8.TwitchM3U8ParserFactory;
import eu.goodlike.twitch.m3u8.master.MasterPlaylist;
import eu.goodlike.twitch.token.Token;
import eu.goodlike.twitch.token.TokenFetcher;
import okhttp3.HttpUrl;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Fetches master playlist for given VoD id
 */
public final class TwitchMasterPlaylistFetcher {

    /**
     * @return master playlist for given VoD id; in case of failure, it will be logged to debug
     */
    public CompletableFuture<MasterPlaylist> fetchMasterPlaylistForVodId(int vodId) {
        return tokenFetcher.generateNewToken(vodId)
                .whenComplete(errorHandler.logOnError("Failed to generate token for VoD with id: " + vodId))
                .thenCompose(token -> getPlaylistWithToken(token, vodId));
    }

    // CONSTRUCTORS

    public TwitchMasterPlaylistFetcher(TokenFetcher tokenFetcher, TwitchRequestMaker twitchRequestMaker,
                                       TwitchM3U8ParserFactory twitchM3U8ParserFactory, CustomizedLogger debugLogger,
                                       CompletableFutureErrorHandler errorHandler) {
        this.tokenFetcher = tokenFetcher;
        this.twitchRequestMaker = twitchRequestMaker;
        this.twitchM3U8ParserFactory = twitchM3U8ParserFactory;
        this.debugLogger = debugLogger;
        this.errorHandler = errorHandler;
    }

    // PRIVATE

    private final TokenFetcher tokenFetcher;
    private final TwitchRequestMaker twitchRequestMaker;
    private final TwitchM3U8ParserFactory twitchM3U8ParserFactory;
    private final CustomizedLogger debugLogger;
    private final CompletableFutureErrorHandler errorHandler;

    private CompletableFuture<MasterPlaylist> getPlaylistWithToken(Token token, int vodId) {
        HttpUrl playlistUrl = getUrlForVodId(token, vodId);
        debugLogger.logMessage("Requesting master playlist at: " + playlistUrl);

        return twitchRequestMaker.makeRawRequest(playlistUrl)
                .whenComplete(errorHandler.logOnError("Failed to generate token for VoD with id: " + vodId))
                .thenApply(Response::body)
                .thenApply(ResponseBody::byteStream)
                .thenCompose(this::getPlaylistFromRemoteInputStream)
                .whenComplete(errorHandler.logOnError("Failed to parse master playlist at: " + playlistUrl));
    }

    private CompletableFuture<MasterPlaylist> getPlaylistFromRemoteInputStream(InputStream inputStream) {
        Optional<MasterPlaylist> masterPlaylist;
        try (TwitchM3U8Parser parser = twitchM3U8ParserFactory.newInstance(inputStream)) {
            masterPlaylist = parser.parseMasterPlaylist();
        } catch (Exception e) {
            return Futures.failedFuture(e);
        }
        return Futures.fromOptional(masterPlaylist, () -> new IOException("Could not parse the input stream"));
    }

    private static final String VOD_ID_VARIABLE = ":video_id";
    private static final String PLAYLIST_URL = "http://usher.twitch.tv/vod/" + VOD_ID_VARIABLE;
    private static final HttpUrl PLAYLIST_HTTP_URL = HttpUrls.parse(PLAYLIST_URL)
            .orElseThrow(() -> new AssertionError("This url should be valid: " + PLAYLIST_URL));

    private static final String ALLOW_SOURCE_QUERY = "allow_source";
    private static final String AUTH_SIG_QUERY = "nauthsig";
    private static final String AUTH_QUERY = "nauth";

    private static HttpUrl getUrlForVodId(Token token, int vodId) {
        return HttpUrls.insertPathVariables(PLAYLIST_HTTP_URL, ImmutableMap.of(VOD_ID_VARIABLE, String.valueOf(vodId)))
                .newBuilder()
                .addQueryParameter(ALLOW_SOURCE_QUERY, "true")
                .addQueryParameter(AUTH_SIG_QUERY, token.getSig())
                .addQueryParameter(AUTH_QUERY, token.getToken())
                .build();
    }

}
