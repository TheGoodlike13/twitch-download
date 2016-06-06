package eu.goodlike.twitch.m3u8;

import com.google.common.base.MoreObjects;
import eu.goodlike.utils.StringFormatter;

import java.util.Objects;

public final class StreamPart {

    public String getFormattedUpperLine() {
        int adjustedDuration = exactDurationInMillis < 1000 ? exactDurationInMillis + 1000 : exactDurationInMillis;
        StringBuilder formattedDuration = new StringBuilder().append(adjustedDuration);
        if (exactDurationInMillis < 1000)
            formattedDuration.setCharAt(0, '0');
        formattedDuration.insert(formattedDuration.length() - 3, '.');

        return StringFormatter.format("#EXTINF:{},", formattedDuration.toString());
    }

    public String getFormattedLowerLine(String urlPrefix) {
        return StringFormatter.format("{}?start_offset={}&end_offset={}",
                urlPrefix + fileName, startOffset, endOffset);
    }

    // CONSTRUCTORS

    public StreamPart(String fileName, int startOffset, int endOffset, int exactDurationInMillis) {
        this.fileName = fileName;
        this.startOffset = startOffset;
        this.endOffset = endOffset;
        this.exactDurationInMillis = exactDurationInMillis;
    }

    // PRIVATE

    private final String fileName;
    private final int startOffset;
    private final int endOffset;
    private final int exactDurationInMillis;

    // OBJECT OVERRIDES

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("fileName", fileName)
                .add("startOffset", startOffset)
                .add("endOffset", endOffset)
                .add("exactDuration", exactDurationInMillis)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StreamPart)) return false;
        StreamPart that = (StreamPart) o;
        return Objects.equals(startOffset, that.startOffset) &&
                Objects.equals(endOffset, that.endOffset) &&
                Objects.equals(fileName, that.fileName) &&
                Objects.equals(exactDurationInMillis, that.exactDurationInMillis);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileName, startOffset, endOffset, exactDurationInMillis);
    }

}