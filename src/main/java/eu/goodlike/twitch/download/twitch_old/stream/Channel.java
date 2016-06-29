package eu.goodlike.twitch.download.twitch_old.stream;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class Channel {

    public String getDisplayName() {
        return displayName;
    }

    // CONSTRUCTORS

    @JsonCreator
    public Channel(@JsonProperty("display_name") String displayName) {
        this.displayName = displayName;
    }

    // PRIVATE

    private final String displayName;

    // OBJECT OVERRIDES

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("displayName", displayName)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Channel)) return false;
        Channel channel = (Channel) o;
        return Objects.equals(displayName, channel.displayName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(displayName);
    }

}
