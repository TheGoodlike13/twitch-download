package eu.goodlike.twitch.m3u8;

import eu.goodlike.io.log.CustomizedLogger;
import eu.goodlike.neat.Null;
import eu.goodlike.twitch.download.configurations.policy.PlaylistPolicy;

import java.io.InputStream;
import java.util.Scanner;

/**
 * TwitchM3U8Parser factory, which eliminates the need to pass logger/policy when constructing a new instance
 */
public final class TwitchM3U8ParserFactory {

    /**
     * @return TwitchM3U8Parser for given input stream
     * @throws NullPointerException if input stream is null
     */
    public TwitchM3U8Parser newInstance(InputStream inputStream) {
        Null.check(inputStream).ifAny("Input stream cannot be null");
        return new TwitchM3U8Parser(new Scanner(inputStream), debugLogger, playlistPolicy);
    }

    // CONSTRUCTORS

    public TwitchM3U8ParserFactory(CustomizedLogger debugLogger, PlaylistPolicy playlistPolicy) {
        Null.check(debugLogger, playlistPolicy).ifAny("Logger and playlist policy cannot be null");

        this.debugLogger = debugLogger;
        this.playlistPolicy = playlistPolicy;
    }

    // PRIVATE

    private final CustomizedLogger debugLogger;
    private final PlaylistPolicy playlistPolicy;

}
