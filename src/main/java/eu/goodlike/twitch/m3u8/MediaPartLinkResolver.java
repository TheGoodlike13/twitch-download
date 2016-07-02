package eu.goodlike.twitch.m3u8;

import eu.goodlike.neat.Null;
import okhttp3.HttpUrl;

import java.util.List;

import static eu.goodlike.UsefulValidators.INTEGER_STRING_VALIDATOR;
import static eu.goodlike.twitch.TwitchDefaults.END_OFFSET_PARAM;
import static eu.goodlike.twitch.TwitchDefaults.START_OFFSET_PARAM;

/**
 * Parses the link to a specific stream VoD segment into its location and offsets
 */
public final class MediaPartLinkResolver {

    /**
     * @return location of stream VoD segment, null if it could not be parsed
     */
    public String getLocation() {
        List<String> pathSegments = httpUrl.pathSegments();
        return pathSegments.isEmpty()
                ? null
                : pathSegments.get(0);
    }

    /**
     * @return start offset of stream VoD segment, null if it could not be parsed
     */
    public Integer getStartOffset() {
        return toValidOffset(httpUrl.queryParameter(START_OFFSET_PARAM));
    }

    /**
     * @return end offset of stream VoD segment, null if it could not be parsed
     */
    public Integer getEndOffset() {
        return toValidOffset(httpUrl.queryParameter(END_OFFSET_PARAM));
    }

    // CONSTRUCTORS

    public static MediaPartLinkResolver forPathWithoutPrefix(String path) {
        Null.check(path).ifAny("Path cannot be null");
        HttpUrl httpUrl = LOCAL_URL.resolve(path);
        return httpUrl == null
                ? null
                : new MediaPartLinkResolver(httpUrl);
    }

    public MediaPartLinkResolver(HttpUrl httpUrl) {
        Null.check(httpUrl).ifAny("Http url cannot be null");
        this.httpUrl = httpUrl;
    }

    // PRIVATE

    private final HttpUrl httpUrl;

    private Integer toValidOffset(String possibleOffset) {
        if (INTEGER_STRING_VALIDATOR.isInvalid(possibleOffset))
            return null;

        int offset = Integer.parseInt(possibleOffset);
        return offset >= 0 ? offset : null;
    }

    private static final HttpUrl LOCAL_URL = new HttpUrl.Builder()
            .scheme("http")
            .host("localhost")
            .build();

}
