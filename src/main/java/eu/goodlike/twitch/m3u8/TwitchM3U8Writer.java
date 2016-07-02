package eu.goodlike.twitch.m3u8;

import eu.goodlike.io.FileAppender;
import eu.goodlike.io.log.CustomizedLogger;
import eu.goodlike.neat.Null;
import eu.goodlike.twitch.m3u8.media.MediaPlaylist;
import eu.goodlike.twitch.m3u8.media.StreamPart;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

import static eu.goodlike.twitch.m3u8.M3U8Defaults.*;

/**
 * <pre>
 * A simple M3U8 writer, which supports writing media playlists for twitch streams
 *
 * Media playlist is a list of fragments that combined together make the stream VoD
 * </pre>
 */
public final class TwitchM3U8Writer {

    /**
     * @return file at given path if it was possible to write the playlist into it, Optional::empty otherwise
     * @throws NullPointerException if path is null
     */
    public Optional<File> writeMediaPlaylist(Path path) {
        Null.check(path).ifAny("Path cannot be null");

        Optional<FileAppender> fileAppenderOptional = FileAppender.ofFile(path);
        if (!fileAppenderOptional.isPresent())
            return logFailure("Cannot write media playlist to file at: " + path);

        try (FileAppender fileAppender = fileAppenderOptional.get()) {
            fileAppender.appendLine(M3U8_FILE_START);
            fileAppender.appendLine("");
            fileAppender.appendLine(M3U8_MEDIA_TARGET_DURATION_TAG_PREFIX + mediaPlaylist.getTargetDuration());
            fileAppender.appendLine("");
            for (StreamPart part : mediaPlaylist.getStreamParts())
                fileAppender.appendLine(part.getMediaPlaylistString());

            fileAppender.appendLine("");
            fileAppender.appendLine(M3U8_MEDIA_END_OF_FILE);
            fileAppender.appendLine("");
        } catch (Exception e) {
            return logFailure("Unexpected exception while writing media playlist file at: " + path);
        }
        return Optional.of(path.toFile());
    }

    // CONSTRUCTORS

    public TwitchM3U8Writer(MediaPlaylist mediaPlaylist, CustomizedLogger debugLogger) {
        Null.check(mediaPlaylist, debugLogger).ifAny("Media playlist and logger cannot be null");

        this.mediaPlaylist = mediaPlaylist;
        this.debugLogger = debugLogger;
    }

    // PRIVATE

    private final MediaPlaylist mediaPlaylist;
    private final CustomizedLogger debugLogger;

    private <T> Optional<T> logFailure(String message) {
        debugLogger.logMessage(message);
        return Optional.empty();
    }

}
