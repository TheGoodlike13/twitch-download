package eu.goodlike.twitch.playlist;

import eu.goodlike.functional.Futures;
import eu.goodlike.functional.Optionals;
import eu.goodlike.io.log.CustomizedLogger;
import eu.goodlike.libraries.okhttp.HttpUrls;
import eu.goodlike.twitch.CompletableFutureErrorHandler;
import eu.goodlike.twitch.download.configurations.policy.PlaylistPolicy;
import eu.goodlike.twitch.download.http.TwitchRequestMaker;
import eu.goodlike.twitch.m3u8.TwitchM3U8Parser;
import eu.goodlike.twitch.m3u8.TwitchM3U8ParserFactory;
import eu.goodlike.twitch.m3u8.master.MasterPlaylist;
import eu.goodlike.twitch.m3u8.media.MediaPlaylist;
import okhttp3.HttpUrl;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static eu.goodlike.twitch.download.configurations.options.DefaultOptions.DEFAULT_QUALITY_LEVEL;

/**
 * Fetches media playlist for selected quality
 */
public final class TwitchMediaPlaylistFetcher {

    /**
     * @return media playlist for selected quality; in case of failure, it will be logged to debug
     */
    public CompletableFuture<MediaPlaylist> fetchMediaPlaylist(MasterPlaylist masterPlaylist) {
        String qualityLevel = playlistPolicy.getQualityLevel();
        Optional<String> playlistLink = Optionals.firstNotEmpty(
                masterPlaylist.getStreamPlaylistUrlForQuality(qualityLevel),
                masterPlaylist.getStreamPlaylistUrlForQuality(DEFAULT_QUALITY_LEVEL)
                        .filter(source -> playlistPolicy.isDefaultToSourceEnabled())
        );
        return Futures.fromOptional(playlistLink,
                () -> new NoSuchElementException("Could not find requested quality VoD"))
                .whenComplete(errorHandler.logOnError(
                        "Could not find requested quality VoD (or source, if enabled): " + qualityLevel))

                .thenApply(HttpUrls::parse)
                .thenCompose(httpUrl -> Futures.fromOptional(httpUrl,
                        () -> new IllegalArgumentException("Could not parse media playlist url: " + playlistLink)))
                .whenComplete(errorHandler.logOnError(
                        "Media playlist url for VoD seems to be invalid: " + playlistLink))

                .thenCompose(this::getPlaylistFromUrl);
    }

    // CONSTRUCTORS

    public TwitchMediaPlaylistFetcher(TwitchRequestMaker twitchRequestMaker,
                                      TwitchM3U8ParserFactory twitchM3U8ParserFactory, CustomizedLogger debugLogger,
                                      CompletableFutureErrorHandler errorHandler, PlaylistPolicy playlistPolicy) {
        this.twitchRequestMaker = twitchRequestMaker;
        this.twitchM3U8ParserFactory = twitchM3U8ParserFactory;
        this.debugLogger = debugLogger;
        this.errorHandler = errorHandler;
        this.playlistPolicy = playlistPolicy;
    }


    // PRIVATE

    private final TwitchRequestMaker twitchRequestMaker;
    private final TwitchM3U8ParserFactory twitchM3U8ParserFactory;
    private final CustomizedLogger debugLogger;
    private final CompletableFutureErrorHandler errorHandler;
    private final PlaylistPolicy playlistPolicy;

    private CompletableFuture<MediaPlaylist> getPlaylistFromUrl(HttpUrl url) {
        debugLogger.logMessage("Requesting media playlist at: " + url);

        return Futures.fromOptional(findPrefix(url),
                () -> new IllegalArgumentException("Url does not specify path to playlist: " + url))
                .whenComplete(errorHandler.logOnError(
                        "Media playlist url has no path to playlist file: " + url))
                .thenCompose(prefix -> getPlaylistFromUrl(url, prefix));
    }

    private CompletableFuture<MediaPlaylist> getPlaylistFromUrl(HttpUrl url, String prefix) {
        return twitchRequestMaker.makeRawRequest(url)
                .whenComplete(errorHandler.logOnError(
                        "Failed to retrieve media playlist at: " + url))
                .thenApply(Response::body)
                .thenApply(ResponseBody::byteStream)
                .thenCompose(inputStream -> getPlaylistFromInputStream(inputStream, prefix))
                .whenComplete(errorHandler.logOnError("Failed to parse media playlist at: " + url));
    }

    private CompletableFuture<MediaPlaylist> getPlaylistFromInputStream(InputStream inputStream, String prefix) {
        Optional<MediaPlaylist> mediaPlaylist;
        try (TwitchM3U8Parser parser = twitchM3U8ParserFactory.newInstance(inputStream)) {
            mediaPlaylist = parser.parseMediaPlaylist();
        } catch (Exception e) {
            return Futures.failedFuture(e);
        }
        return Futures.fromOptional(mediaPlaylist.map(playlist -> playlist.prependLocationPrefix(prefix)),
                () -> new IOException("Could not parse the input stream"));
    }

    private Optional<String> findPrefix(HttpUrl url) {
        int pathSize = url.pathSize();
        if (pathSize < 1)
            return Optional.empty();

        Set<String> params = url.queryParameterNames();

        HttpUrl.Builder builder = url.newBuilder();
        params.forEach(builder::removeAllQueryParameters);

        builder.removePathSegment(pathSize - 1);
        return Optional.of(builder.build().toString());
    }

}
