package eu.goodlike.twitch.m3u8;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import eu.goodlike.io.log.CustomizedLogger;
import eu.goodlike.neat.Null;
import eu.goodlike.str.Str;
import eu.goodlike.twitch.download.configurations.policy.PlaylistPolicy;
import eu.goodlike.twitch.m3u8.master.MasterPlaylist;
import eu.goodlike.twitch.m3u8.media.MediaPlaylist;
import eu.goodlike.twitch.m3u8.media.TwitchStreamPart;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.Predicate;

import static eu.goodlike.twitch.m3u8.M3U8Defaults.*;

/**
 * <pre>
 * A simple M3U8 parser, which supports parsing master and media playlists for twitch streams
 *
 * Master playlist contains sources for a twitch stream, each source representing a different quality
 *
 * Media playlist is a list of fragments that combined together make the stream VoD
 * </pre>
 */
public final class TwitchM3U8Parser implements AutoCloseable {

    /**
     * @return master playlist parsed from the underlying scanner, Optional::empty if it is not parsable as a master
     * playlist
     */
    public Optional<MasterPlaylist> parseMasterPlaylist() {
        String line = scanner.nextLine();
        if (!M3U8_FILE_START.equals(line))
            return logFailure("Invalid file start, expected: " + M3U8_FILE_START + ", found: " + line);

        ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
        while (scanner.hasNextLine()) {
            line = skipUntil(nextLine -> nextLine.startsWith(M3U8_MASTER_MEDIA_TAG_PREFIX));
            if (line != null) {
                int nameTagIndex = line.indexOf(M3U8_MASTER_MEDIA_NAME_ATTRIBUTE);
                if (nameTagIndex < 0)
                    return logFailure("Invalid m3u8 media tag: no NAME attribute found");

                int nameStartIndex = line.indexOf('"', nameTagIndex) + 1;
                if (nameStartIndex < 0)
                    return logFailure("Invalid m3u8 media tag: NAME attribute value incorrectly specified");

                int nameEndIndex = line.indexOf('"', nameStartIndex);
                if (nameEndIndex < 0)
                    return logFailure("Invalid m3u8 media tag: NAME attribute value incorrectly specified");

                String name = line.substring(nameStartIndex, nameEndIndex).toLowerCase();

                line = skipUntil(nextLine -> !nextLine.startsWith(M3U8_TAG_START));
                if (line == null)
                    return logFailure("Missing link for stream source with specified name: " + name);

                builder.put(name, line);
            }
        }
        return Optional.of(new MasterPlaylist(builder.build()));
    }

    /**
     * @return media playlist parsed from the underlying scanner, Optional::empty if it is not parsable as a media
     * playlist
     */
    public Optional<MediaPlaylist> parseMediaPlaylist() {
        String line = scanner.nextLine();
        if (!M3U8_FILE_START.equals(line))
            return logFailure("Invalid file start, expected: " + M3U8_FILE_START + ", found: " + line);

        ImmutableList.Builder<TwitchStreamPart> builder = ImmutableList.builder();
        TwitchStreamPart lastPart = null;
        while (scanner.hasNextLine()) {
            line = skipUntil(nextLine -> nextLine.startsWith(M3U8_MEDIA_PREFIX));
            if (line != null) {
                line = line.substring(M3U8_MEDIA_PREFIX.length());
                List<String> parts = Str.splitIncludingEmptyAffixes(line, ",");
                if (parts.size() != 2)
                    return logFailure("Invalid m3u8 segment tag: "  + M3U8_MEDIA_PREFIX +
                            " should have one and only one ',' to separate duration and (optional) name");

                String durationString = parts.get(0);
                String name = parts.get(1);

                BigDecimal duration;
                try {
                    duration = new BigDecimal(durationString);
                } catch (NumberFormatException e) {
                    return logFailure("Invalid m3u8 segment tag: could not parse duration into BigDecimal: " + durationString);
                }

                line = skipUntil(nextLine -> !nextLine.startsWith(M3U8_TAG_START));
                if (line == null)
                    return logFailure("Missing link for stream segment with (optional) name: " + name);

                MediaPartLinkResolver resolver = MediaPartLinkResolver.forPathWithoutPrefix(line);
                if (resolver == null)
                    return logFailure("Could not parse link for stream segment as an http element: " + line);

                String location = resolver.getLocation();
                if (location == null)
                    return logFailure("Location could not be parsed from stream segment link: " + line);

                Integer startOffset = resolver.getStartOffset();
                if (startOffset == null)
                    return logFailure("Start offset could not be parsed from stream segment link: " + line);

                Integer endOffset = resolver.getEndOffset();
                if (endOffset == null)
                    return logFailure("End offset could not be parsed from stream segment link: " + line);

                TwitchStreamPart part = new TwitchStreamPart(duration, location, startOffset, endOffset, name, null);
                if (lastPart == null)
                    lastPart = part;
                else if (playlistPolicy.isCombinePlaylistPartsEnabled() && part.canBeAppendedTo(lastPart))
                    lastPart = part.appendTo(lastPart);
                else {
                    builder.add(lastPart);
                    lastPart = part;
                }
            }
        }

        if (lastPart != null)
            builder.add(lastPart);

        return Optional.of(new MediaPlaylist(builder.build()));
    }

    @Override
    public void close() throws Exception {
        scanner.close();
    }

    // CONSTRUCTORS

    public TwitchM3U8Parser(Scanner scanner, CustomizedLogger debugLogger, PlaylistPolicy playlistPolicy) {
        Null.check(scanner, debugLogger, playlistPolicy)
                .ifAny("Scanner, logger and playlist policy cannot be null");

        this.scanner = scanner;
        this.debugLogger = debugLogger;
        this.playlistPolicy = playlistPolicy;
    }

    // PRIVATE

    private final Scanner scanner;
    private final CustomizedLogger debugLogger;
    private final PlaylistPolicy playlistPolicy;

    private String skipUntil(Predicate<String> stopCondition) {
        String line;
        do {
            if (!scanner.hasNextLine())
                return null;

            line = scanner.nextLine();
        } while (!stopCondition.test(line));

        return line;
    }

    private <T> Optional<T> logFailure(String message) {
        debugLogger.logMessage(message);
        return Optional.empty();
    }

}
