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

import static eu.goodlike.twitch.m3u8.M3U8Defaults.TWITCH_M3U8_MEDIA_TAG_FORMAT;

public final class SimpleStreamPart implements StreamPart {

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
        return false;
    }

    @Override
    public StreamPart appendTo(StreamPart other) {
        Null.check(other).ifAny("Stream part cannot be null");
        throw new IllegalArgumentException("Simple stream part cannot be appended");
    }

    @Override
    public String getMediaPlaylistString() {
        return Str.format(MEDIA_STRING_FORMAT, duration, name) + getFullLocation();
    }

    @Override
    public StreamPart setLocationPrefix(String locationPrefix) {
        return new SimpleStreamPart(duration, location, name, locationPrefix);
    }

    @Override
    public String getAbsoluteLocation() {
        return location;
    }

    @Override
    public String getFullLocation() {
        return locationPrefix + location;
    }

    @Override
    public Optional<HttpUrl> getLocationUrl() {
        return HttpUrls.parse(getFullLocation());
    }

    // CONSTRUCTORS

    public SimpleStreamPart(BigDecimal duration, String location, String name, String locationPrefix) {
        Null.check(duration, location).ifAny("Duration and location cannot be null");

        Validate.bigDecimal().not().isNegative().ifInvalid(duration)
                .thenThrow(() -> new IllegalArgumentException("Duration cannot be negative: " + duration));

        this.duration = duration;
        this.location = location;

        this.name = name == null ? "" : name;
        this.locationPrefix = locationPrefix == null ? "" : locationPrefix;
    }

    // PRIVATE

    private final BigDecimal duration;
    private final String location;

    private final String name;
    private final String locationPrefix;

    private static final String MEDIA_STRING_FORMAT = TWITCH_M3U8_MEDIA_TAG_FORMAT
            + System.lineSeparator();

    // OBJECT OVERRIDES

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("duration", duration)
                .add("location", location)
                .add("name", name)
                .add("locationPrefix", locationPrefix)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SimpleStreamPart)) return false;
        SimpleStreamPart that = (SimpleStreamPart) o;
        return BigDecimals.equalsIgnoreScale(duration, that.duration) &&
                Objects.equals(location, that.location) &&
                Objects.equals(name, that.name) &&
                Objects.equals(locationPrefix, that.locationPrefix);
    }

    @Override
    public int hashCode() {
        return Objects.hash(BigDecimals.hashCode(duration), location, name, locationPrefix);
    }

}
