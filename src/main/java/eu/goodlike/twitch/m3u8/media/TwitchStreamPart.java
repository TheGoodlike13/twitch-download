package eu.goodlike.twitch.m3u8.media;

import com.google.common.base.MoreObjects;
import eu.goodlike.libraries.okhttp.HttpUrls;
import eu.goodlike.misc.BigDecimals;
import eu.goodlike.neat.Null;
import eu.goodlike.str.Str;
import eu.goodlike.validate.Validate;
import okhttp3.HttpUrl;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

import static eu.goodlike.twitch.TwitchDefaults.*;

/**
 * Represents one part of a twitch stream VoD
 */
public final class TwitchStreamPart {

    /**
     * @return true if this twitch stream part is from the same file and follows the other part immediately
     */
    public boolean canBeAppendedTo(TwitchStreamPart other) {
        return this.location.equals(other.location)
                && this.startOffset == other.endOffset;
    }

    /**
     * @return appends this twitch part to the given one
     * @throws IllegalArgumentException if these parts come from different files
     * @throws IllegalArgumentException if this part is supposed to come before the other part, not after
     */
    public TwitchStreamPart appendTo(TwitchStreamPart other) {
        if (!this.location.equals(other.location))
            throw new IllegalArgumentException("These parts cannot be appended forcibly: " +
                    "stream part location mismatch");

        if (this.endOffset < other.startOffset)
            throw new IllegalArgumentException("These parts cannot be appended forcibly: " +
                    "this stream part end before the other begins");

        return new TwitchStreamPart(other.duration.add(this.duration), other.location,
                other.startOffset, this.endOffset, combineNames(other.name, this.name),
                other.locationPrefix == null ? this.locationPrefix : other.locationPrefix);
    }

    /**
     * @return String representation of this stream part in an m3u8 file
     */
    public String getMediaPlaylistString() {
        return Str.format(TWITCH_M3U8_MEDIA_PART_FORMAT,
                duration, name, locationPrefix, location, startOffset, endOffset);
    }

    /**
     * @return this TwitchStreamPart with a location prefix prepended
     */
    public TwitchStreamPart setLocationPrefix(String locationPrefix) {
        return new TwitchStreamPart(duration, location, startOffset, endOffset, name, locationPrefix);
    }

    /**
     * @return String representing the name of the location
     */
    public String getLocationName() {
        return location;
    }

    /**
     * @return location String, without the locationPrefix
     */
    public String getAbsoluteLocation() {
        return Str.format(TWITCH_M3U8_MEDIA_LOCATION_FORMAT, location, startOffset, endOffset);
    }

    /**
     * @return location String, including locationPrefix
     */
    public String getFullLocation() {
        return Str.format(TWITCH_M3U8_MEDIA_FULL_LOCATION_FORMAT, locationPrefix, location, startOffset, endOffset);
    }

    /**
     * @return HttpUrl of this stream segment location, Optional:;empty if it is not a valid http url
     */
    public Optional<HttpUrl> getLocationUrl() {
        return HttpUrls.parse(getFullLocation());
    }

    // CONSTRUCTORS

    public TwitchStreamPart(BigDecimal duration, String location, int startOffset, int endOffset, String name, String locationPrefix) {
        Null.check(duration, location).ifAny("Duration and location cannot be null");

        Validate.bigDecimal().not().isNegative().ifInvalid(duration)
                .thenThrow(() -> new IllegalArgumentException("Duration cannot be negative: " + duration));

        if (startOffset < 0)
            throw new IllegalArgumentException("Start offset cannot be negative: " + startOffset);

        if (endOffset < startOffset)
            throw new IllegalArgumentException("End offset cannot be less than start offset: " +
                    endOffset + " < " + startOffset);

        this.duration = duration;
        this.location = location;
        this.startOffset = startOffset;
        this.endOffset = endOffset;

        this.name = name == null ? "" : name;
        this.locationPrefix = locationPrefix == null ? "" : locationPrefix;
    }

    // PRIVATE

    private final BigDecimal duration;
    private final String location;
    private final int startOffset;
    private final int endOffset;

    private final String name;
    private final String locationPrefix;

    private static String combineNames(String first, String second) {
        return first.equals(second) || first.contains(second)
                ? first
                : second.contains(first) ? second : first + " " + second;
    }

    // OBJECT OVERRIDES

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("duration", duration)
                .add("location", location)
                .add("startOffset", startOffset)
                .add("endOffset", endOffset)
                .add("name", name)
                .add("locationPrefix", locationPrefix)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TwitchStreamPart)) return false;
        TwitchStreamPart that = (TwitchStreamPart) o;
        return Objects.equals(startOffset, that.startOffset) &&
                Objects.equals(endOffset, that.endOffset) &&
                BigDecimals.equalsIgnoreScale(duration, that.duration) &&
                Objects.equals(location, that.location) &&
                Objects.equals(name, that.name) &&
                Objects.equals(locationPrefix, that.locationPrefix);
    }

    @Override
    public int hashCode() {
        return Objects.hash(BigDecimals.hashCode(duration), location, startOffset, endOffset, name, locationPrefix);
    }

}
