package eu.goodlike.twitch.m3u8.media;

import okhttp3.HttpUrl;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Represents one part of a stream VoD
 */
public interface StreamPart {

    /**
     * @return location of this stream part, without offsets
     */
    String getLocation();

    /**
     * @return duration of this stream part
     */
    BigDecimal getDuration();

    /**
     * @return true if this stream part is from the same file and follows the other part immediately
     * @throws NullPointerException if other is null
     */
    boolean canBeAppendedTo(StreamPart other);

    /**
     * @return appends this stream part to the given one
     * @throws NullPointerException if other is null
     * @throws IllegalArgumentException if these parts cannot be appended forcibly, i.e. due to being from different
     * files
     */
    StreamPart appendTo(StreamPart other);

    /**
     * @return String representation of this stream part in an m3u8 file
     */
    String getMediaPlaylistString();

    /**
     * @return this stream part with a location prefix prepended
     */
    StreamPart setLocationPrefix(String locationPrefix);

    /**
     * @return location String, without the locationPrefix
     */
    String getAbsoluteLocation();

    /**
     * @return location String, including locationPrefix
     */
    String getFullLocation();

    /**
     * @return HttpUrl of this stream segment location, Optional:;empty if it is not a valid http url
     */
    Optional<HttpUrl> getLocationUrl();

}
