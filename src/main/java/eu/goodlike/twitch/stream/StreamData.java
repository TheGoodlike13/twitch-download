package eu.goodlike.twitch.stream;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

import java.time.Instant;
import java.util.Objects;
import java.util.regex.Pattern;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class StreamData {

    public String getFullFileName() {
        String preEncodedString = "(" + timeOfRecording + ") " + streamerName + " " + streamTitle;
        return SANE_FILE_NAME_PATTERN.matcher(preEncodedString).replaceAll("_");
    }

    // CONSTRUCTORS

    @JsonCreator
    public StreamData(@JsonProperty("recorded_at") String recordedAt,
                      @JsonProperty("channel") Channel channel,
                      @JsonProperty("title") String streamTitle) {
        this.timeOfRecording = Instant.parse(recordedAt);
        this.streamerName = channel.getDisplayName();
        this.streamTitle = streamTitle;
    }

    // PRIVATE

    private final Instant timeOfRecording;
    private final String streamerName;
    private final String streamTitle;

    private static final Pattern SANE_FILE_NAME_PATTERN = Pattern.compile("(?![A-Za-z0-9 ,'_+!@#$%^&();=\\-\\.]).");

    // OBJECT OVERRIDES

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("timeOfRecording", timeOfRecording)
                .add("streamerName", streamerName)
                .add("streamTitle", streamTitle)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StreamData)) return false;
        StreamData stream = (StreamData) o;
        return Objects.equals(timeOfRecording, stream.timeOfRecording) &&
                Objects.equals(streamerName, stream.streamerName) &&
                Objects.equals(streamTitle, stream.streamTitle);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timeOfRecording, streamerName, streamTitle);
    }

}
