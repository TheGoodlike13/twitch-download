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

import static eu.goodlike.twitch.TwitchDefaults.END_OFFSET_PARAM;
import static eu.goodlike.twitch.TwitchDefaults.START_OFFSET_PARAM;
import static eu.goodlike.twitch.m3u8.M3U8Defaults.TWITCH_M3U8_MEDIA_TAG_FORMAT;

/**
 * Represents one part of a twitch stream VoD
 */
public final class AppendableStreamPart implements StreamPart {

    @Override
    public String getLocation() {
        return location;
    }

    @Override
    public BigDecimal getDuration() {
        return duration;
    }

    @Override
    public boolean canBeAppendedTo(StreamPart other) {
        Null.check(other).ifAny("Stream part cannot be null");
        if (!(other instanceof AppendableStreamPart))
            return false;

        AppendableStreamPart appendableStreamPart = (AppendableStreamPart) other;
        return this.location.equals(appendableStreamPart.location)
                && this.startOffset == appendableStreamPart.endOffset + 1;
    }

    @Override
    public StreamPart appendTo(StreamPart other) {
        Null.check(other).ifAny("Stream part cannot be null");
        if (!(other instanceof AppendableStreamPart))
            throw new IllegalArgumentException("These parts cannot be appended forcibly: " +
                    "given stream part is not an appendable stream part");

        AppendableStreamPart appendableStreamPart = (AppendableStreamPart) other;
        if (!this.location.equals(appendableStreamPart.location))
            throw new IllegalArgumentException("These parts cannot be appended forcibly: " +
                    "stream part location mismatch");

        if (this.endOffset < appendableStreamPart.startOffset)
            throw new IllegalArgumentException("These parts cannot be appended forcibly: " +
                    "this stream part end before the other begins");

        return new AppendableStreamPart(appendableStreamPart.duration.add(this.duration), appendableStreamPart.location,
                appendableStreamPart.startOffset, this.endOffset, combineNames(appendableStreamPart.name, this.name),
                appendableStreamPart.locationPrefix == null ? this.locationPrefix : appendableStreamPart.locationPrefix);
    }

    @Override
    public String getMediaPlaylistString() {
        return Str.format(MEDIA_STRING_FORMAT,
                duration, name, locationPrefix, location, startOffset, endOffset);
    }

    @Override
    public StreamPart setLocationPrefix(String locationPrefix) {
        return new AppendableStreamPart(duration, location, startOffset, endOffset, name, locationPrefix);
    }

    @Override
    public String getAbsoluteLocation() {
        return Str.format(LOCATION_FORMAT, location, startOffset, endOffset);
    }

    @Override
    public String getFullLocation() {
        return Str.format(FULL_LOCATION_FORMAT, locationPrefix, location, startOffset, endOffset);
    }

    @Override
    public Optional<HttpUrl> getLocationUrl() {
        return HttpUrls.parse(getFullLocation());
    }

    // CONSTRUCTORS

    public AppendableStreamPart(BigDecimal duration, String location, int startOffset, int endOffset, String name, String locationPrefix) {
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

    private static final String LOCATION_FORMAT =
            "{}?" + START_OFFSET_PARAM + "={}&" + END_OFFSET_PARAM + "={}";

    private static final String FULL_LOCATION_FORMAT = "{}" + LOCATION_FORMAT;

    private static final String MEDIA_STRING_FORMAT = TWITCH_M3U8_MEDIA_TAG_FORMAT
            + System.lineSeparator() + FULL_LOCATION_FORMAT;

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
        if (!(o instanceof AppendableStreamPart)) return false;
        AppendableStreamPart that = (AppendableStreamPart) o;
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
