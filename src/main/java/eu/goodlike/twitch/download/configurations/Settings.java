package eu.goodlike.twitch.download.configurations;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Optional;

public final class Settings {

    public String getApplicationName() {
        return applicationName;
    }

    public List<String> getDefaultFfmpegOptions() {
        return ffmpegOptions;
    }

    public String getDefaultOutputFormat() {
        return outputFormat;
    }

    public String getClientId() {
        return clientId;
    }

    public Optional<String> getOauthKey() {
        return Optional.ofNullable(oauthKey);
    }

    // CONSTRUCTORS

    public Settings() {
        PropertiesParser propertiesParser = new PropertiesParser(DEFAULT_PROPERTIES_FILE_PATH);
        this.applicationName = propertiesParser.getProperty(APPLICATION_NAME_KEY)
                .filter(str -> !str.isEmpty())
                .orElse(DEFAULT_APPLICATION_NAME_VALUE);
        this.ffmpegOptions = propertiesParser.getProperty(FFMPEG_OPTIONS_KEY)
                .filter(str -> !str.isEmpty())
                .map(str -> str.split(" "))
                .map(ImmutableList::copyOf)
                .orElse(DEFAULT_FFMPEG_OPTIONS_VALUE);
        this.outputFormat = propertiesParser.getProperty(OUTPUT_FORMAT_KEY)
                .filter(str -> !str.isEmpty())
                .orElse(DEFAULT_OUTPUT_FORMAT_VALUE);
        this.clientId = propertiesParser.getProperty(CLIENT_ID_KEY)
                .filter(str -> !str.isEmpty())
                .orElse(DEFAULT_CLIENT_ID_VALUE);
        this.oauthKey = propertiesParser.getProperty(OAUTH_KEY)
                .filter(str -> !str.isEmpty())
                .orElse(DEFAULT_OAUTH_VALUE);
    }

    // PRIVATE

    private final String applicationName;
    private final List<String> ffmpegOptions;
    private final String outputFormat;
    private final String clientId;
    private final String oauthKey;

    private static final String APPLICATION_NAME_KEY = "app_name";
    private static final String FFMPEG_OPTIONS_KEY = "ffmpeg_options";
    private static final String OUTPUT_FORMAT_KEY = "output_format";
    private static final String CLIENT_ID_KEY = "client_id";
    private static final String OAUTH_KEY = "oauth";



}
