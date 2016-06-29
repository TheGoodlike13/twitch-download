package eu.goodlike.twitch.download.configurations.settings;

import eu.goodlike.functional.Optionals;
import eu.goodlike.io.PropertiesUtils;

import java.io.File;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import static eu.goodlike.twitch.download.configurations.settings.DefaultSettings.*;

/**
 * <pre>
 * Parses setting values from a properties file
 *
 * These values only represent the settings, but may not be actually used (i.e. when an option overrides a setting),
 * hence why no transformation occurs yet
 * </pre>
 */
public final class SettingsParser implements SettingsProvider {

    @Override
    public String getApplicationNameSetting() {
        return getValue(APPLICATION_NAME_KEY).orElse(DEFAULT_SETTINGS_PROVIDER.getApplicationNameSetting());
    }

    @Override
    public String getFfmpegOptionsSetting() {
        return getValue(FFMPEG_OPTIONS_KEY).orElse(DEFAULT_SETTINGS_PROVIDER.getFfmpegOptionsSetting());
    }

    @Override
    public String getOutputFormatSetting() {
        return getValue(OUTPUT_FORMAT_KEY).orElse(DEFAULT_SETTINGS_PROVIDER.getOutputFormatSetting());
    }

    @Override
    public String getClientIdSetting() {
        return getValue(CLIENT_ID_KEY).orElse(DEFAULT_SETTINGS_PROVIDER.getClientIdSetting());
    }

    @Override
    public Optional<String> getOauthSetting() {
        return Optionals.firstNotEmpty(getValue(OAUTH_KEY), DEFAULT_SETTINGS_PROVIDER.getOauthSetting());
    }

    // CONSTRUCTORS

    /**
     * @return settings provider using given properties file; missing settings are replaced with default ones
     * @throws NullPointerException if settingsFilePath is null
     */
    public static SettingsProvider fromFile(Path settingsFilePath) {
        return PropertiesUtils.fileToProperties(settingsFilePath)
                .<SettingsProvider>map(SettingsParser::new)
                .orElse(DEFAULT_SETTINGS_PROVIDER);
    }

    /**
     * @return settings provider using given properties file; missing settings are replaced with default ones
     * @throws NullPointerException if settingsFile is null
     */
    public static SettingsProvider fromFile(File settingsFile) {
        return PropertiesUtils.fileToProperties(settingsFile)
                .<SettingsProvider>map(SettingsParser::new)
                .orElse(DEFAULT_SETTINGS_PROVIDER);
    }

    /**
     * @return settings provider using given properties file; missing settings are replaced with default ones
     * @throws NullPointerException if settingsFilePath is null
     */
    public static SettingsProvider fromFile(String settingsFilePath) {
        return PropertiesUtils.fileToProperties(settingsFilePath)
                .<SettingsProvider>map(SettingsParser::new)
                .orElse(DEFAULT_SETTINGS_PROVIDER);
    }

    public SettingsParser(Properties properties) {
        this.properties = PropertiesUtils.propertiesToMap(properties);
    }

    // PRIVATE

    private final Map<String, String> properties;

    private Optional<String> getValue(String key) {
        return Optional.ofNullable(properties.get(key));
    }

    private static final String APPLICATION_NAME_KEY = "app_name";
    private static final String FFMPEG_OPTIONS_KEY = "ffmpeg_options";
    private static final String OUTPUT_FORMAT_KEY = "output_format";
    private static final String CLIENT_ID_KEY = "client_id";
    private static final String OAUTH_KEY = "oauth";

    private static final SettingsProvider DEFAULT_SETTINGS_PROVIDER = new DefaultSettingsProvider();

    private static final class DefaultSettingsProvider implements SettingsProvider {
        @Override
        public String getApplicationNameSetting() {
            return DEFAULT_APPLICATION_NAME_VALUE;
        }

        @Override
        public String getFfmpegOptionsSetting() {
            return DEFAULT_FFMPEG_OPTIONS_VALUE;
        }

        @Override
        public String getOutputFormatSetting() {
            return DEFAULT_OUTPUT_FORMAT_VALUE;
        }

        @Override
        public String getClientIdSetting() {
            return DEFAULT_CLIENT_ID_VALUE;
        }

        @Override
        public Optional<String> getOauthSetting() {
            return Optional.empty();
        }
    }

}
