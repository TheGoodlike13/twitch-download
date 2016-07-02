package eu.goodlike.twitch.m3u8.media;

import com.google.common.collect.ImmutableList;
import eu.goodlike.functional.ImmutableCollectors;
import eu.goodlike.neat.Null;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;

/**
 * Contains stream VoD fragments
 */
public final class MediaPlaylist {

    /**
     * @return list of stream VoD parts
     */
    public List<TwitchStreamPart> getStreamParts() {
        return streamParts;
    }

    /**
     * @return this media playlist with all of its stream parts having given location prefix
     * @throws NullPointerException if location prefix is null
     */
    public MediaPlaylist prependLocationPrefix(String locationPrefix) {
        Null.check(locationPrefix).ifAny("Location prefix cannot be null");
        return new MediaPlaylist(streamParts.stream()
                .map(part -> part.setLocationPrefix(locationPrefix))
                .collect(ImmutableCollectors.toList()));
    }

    /**
     * @return target duration of this media playlist; this will be (largest duration + 1) rounded down, or 0 if
     * there are no parts
     */
    public BigInteger getTargetDuration() {
        return streamParts.stream()
                .map(TwitchStreamPart::getDuration)
                .max(BigDecimal::compareTo)
                .map(BigDecimal::toBigInteger)
                .map(big -> big.add(ONE))
                .orElse(ZERO);
    }

    // CONSTRUCTORS

    public MediaPlaylist(List<TwitchStreamPart> streamParts) {
        Null.checkList(streamParts).ifAny("Stream sources cannot be null");
        this.streamParts = ImmutableList.copyOf(streamParts);
    }

    // PRIVATE

    private final List<TwitchStreamPart> streamParts;

}
