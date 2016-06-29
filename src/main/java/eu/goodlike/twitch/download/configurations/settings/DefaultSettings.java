package eu.goodlike.twitch.download.configurations.settings;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Contains default values for settings, including the unchangeable properties file location
 */
public final class DefaultSettings {

    public static final String DEFAULT_PROPERTIES_FILE_LOCATION = "twitch-download.properties";
    public static final Path DEFAULT_PROPERTIES_FILE_PATH = Paths.get(DEFAULT_PROPERTIES_FILE_LOCATION);

    public static final String DEFAULT_APPLICATION_NAME_VALUE = "twitchVoD";
    public static final String DEFAULT_FFMPEG_OPTIONS_VALUE = "-bsf:a aac_adtstoasc -c copy";
    public static final String DEFAULT_OUTPUT_FORMAT_VALUE = "(<recorded_at>) <channel:name> <title>.mp4";
    public static final String DEFAULT_CLIENT_ID_VALUE = "nb79liikla455omvka8k0ck8z8x9fr8";

    // PRIVATE

    private DefaultSettings() {
        throw new AssertionError("Do not instantiate, use static methods!");
    }

}
