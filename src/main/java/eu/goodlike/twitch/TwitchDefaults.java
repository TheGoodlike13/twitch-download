package eu.goodlike.twitch;

import static eu.goodlike.twitch.m3u8.M3U8Defaults.M3U8_MEDIA_PREFIX;

public final class TwitchDefaults {

    public static final String VERSION_3_HEADER_NAME = "Accept";
    public static final String VERSION_3_HEADER_VALUE = "application/vnd.twitchtv.v3+json";

    public static final String CLIENT_ID_HEADER_NAME = "Client-ID";

    public static final String OAUTH_HEADER_NAME = "Authorization";
    public static final String OAUTH_HEADER_VALUE_PREFIX = "OAuth ";

    public static final String START_OFFSET_PARAM = "start_offset";
    public static final String END_OFFSET_PARAM = "end_offset";

    public static final String TWITCH_M3U8_MEDIA_LOCATION_FORMAT =
            "{}?" + START_OFFSET_PARAM + "={}&" + END_OFFSET_PARAM + "={}";

    public static final String TWITCH_M3U8_MEDIA_FULL_LOCATION_FORMAT =
            "{}" + TWITCH_M3U8_MEDIA_LOCATION_FORMAT;

    public static final String TWITCH_M3U8_MEDIA_PART_FORMAT =
            M3U8_MEDIA_PREFIX + "{},{}" + System.lineSeparator() + TWITCH_M3U8_MEDIA_FULL_LOCATION_FORMAT;


    // PRIVATE

    private TwitchDefaults() {
        throw new AssertionError("Do not instantiate, use static methods!");
    }

}
