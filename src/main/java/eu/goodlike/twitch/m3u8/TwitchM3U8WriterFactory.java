package eu.goodlike.twitch.m3u8;

import eu.goodlike.io.log.CustomizedLogger;
import eu.goodlike.neat.Null;
import eu.goodlike.twitch.m3u8.media.MediaPlaylist;

/**
 * TwitchM3U8Writer factory, which eliminates the need to pass logger when constructing a new instance
 */
public final class TwitchM3U8WriterFactory {

    /**
     * @return TwitchM3U8Writer for given media playlist
     * @throws NullPointerException if media playlist is null
     */
    public TwitchM3U8Writer newInstance(MediaPlaylist mediaPlaylist) {
        return new TwitchM3U8Writer(mediaPlaylist, debugLogger);
    }

    // CONSTRUCTORS

    public TwitchM3U8WriterFactory(CustomizedLogger debugLogger) {
        Null.check(debugLogger).ifAny("Logger cannot be null");

        this.debugLogger = debugLogger;
    }

    // PRIVATE

    private final CustomizedLogger debugLogger;

}
