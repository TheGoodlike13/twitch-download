package eu.goodlike.twitch.m3u8;

/**
 * Various constants pertaining to m3u8 format
 */
public final class M3U8Defaults {

    public static final String M3U8_TAG_START = "#";

    public static final String M3U8_FILE_START = "#EXTM3U";

    public static final String M3U8_MASTER_MEDIA_TAG_PREFIX = "#EXT-X-MEDIA:";
    public static final String M3U8_MASTER_MEDIA_NAME_ATTRIBUTE = "NAME=";

    public static final String M3U8_MEDIA_TARGET_DURATION_TAG_PREFIX = "#EXT-X-TARGETDURATION:";
    public static final String M3U8_MEDIA_PREFIX = "#EXTINF:";
    public static final String TWITCH_M3U8_MEDIA_TAG_FORMAT = M3U8_MEDIA_PREFIX + "{},{}";
    public static final String M3U8_MEDIA_END_OF_FILE = "#EXT-X-ENDLIST";

    // PRIVATE

    private M3U8Defaults() {
        throw new AssertionError("Do not instantiate, use static methods!");
    }

}
