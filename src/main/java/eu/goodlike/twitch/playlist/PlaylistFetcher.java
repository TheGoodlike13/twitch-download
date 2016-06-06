package eu.goodlike.twitch.playlist;

import eu.goodlike.okhttp.ResponseCallback;
import eu.goodlike.twitch.m3u8.CustomM3U8Parser;
import eu.goodlike.twitch.m3u8.StreamPart;
import eu.goodlike.twitch.token.Token;
import eu.goodlike.utils.Futures;
import eu.goodlike.utils.StringFormatter;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class PlaylistFetcher {

    public CompletableFuture<File> fetchStreamPlaylist(Token token, int vodId) {
        String finalUrl = StringFormatter.format(PLAYLIST_URL, vodId, token.getSig(), token.getToken());
        System.out.println("Downloading stream playlists at: " + finalUrl);
        Request request = new Request.Builder()
                .url(finalUrl)
                .build();

        return ResponseCallback.asFuture(client.newCall(request))
                .thenApply(Response::body)
                .thenApply(ResponseBody::byteStream)
                .thenCompose(this::handlePlaylistUrlResponse);
    }

    // CONSTRUCTORS

    public PlaylistFetcher(OkHttpClient client) {
        this.client = client;
    }

    // PRIVATE

    private final OkHttpClient client;

    private CompletableFuture<File> handlePlaylistUrlResponse(InputStream response) {
        String streamSourcePlaylistUrl;
        try (CustomM3U8Parser parser = new CustomM3U8Parser(response)) {
            streamSourcePlaylistUrl = parser.getStreamSourcePlaylistUrl();
        } catch (Exception e) {
            return Futures.failedFuture(e);
        }

        System.out.println("Requesting playlist at: " + streamSourcePlaylistUrl);
        Request request = new Request.Builder()
                .url(streamSourcePlaylistUrl)
                .build();

        return ResponseCallback.asFuture(client.newCall(request))
                .thenApply(Response::body)
                .thenApply(ResponseBody::byteStream)
                .thenCompose(inputStream -> handlePlaylistFileResponse(inputStream, streamSourcePlaylistUrl));
    }

    private CompletableFuture<File> handlePlaylistFileResponse(InputStream inputStream, String url) {
        int finalDelimiter = url.lastIndexOf("/") + 1;
        String urlPrefix = url.substring(0, finalDelimiter);
        List<String> fileLines;
        try (CustomM3U8Parser parser = new CustomM3U8Parser(inputStream)) {
            fileLines = parser.getPlaylistPrefix();
            for (StreamPart streamPart : parser.getPlaylistParts()) {
                fileLines.add(streamPart.getFormattedUpperLine());
                fileLines.add(streamPart.getFormattedLowerLine(urlPrefix));
            }
            fileLines.addAll(parser.getPlaylistSuffix());
        } catch (Exception e) {
            return Futures.failedFuture(e);
        }
        String filename = url.substring(finalDelimiter);
        Path path = ensureNonExistentPath(filename);
        try {
            Files.write(path, fileLines, StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW);
        } catch (IOException e) {
            return Futures.failedFuture(e);
        }
        return CompletableFuture.completedFuture(path.toFile());
    }

    private Path ensureNonExistentPath(String filename) {
        String workingFilename = filename;
        int copyCount = 0;
        Path path = Paths.get(workingFilename);
        while (Files.exists(path)) {
            workingFilename = filename + " (" + ++copyCount + ")";
            path = Paths.get(workingFilename);
        }
        return path;
    }

    private static final String PLAYLIST_URL =
            "http://usher.twitch.tv/vod/{}?allow_source=true&nauthsig={}&nauth={}";

}
