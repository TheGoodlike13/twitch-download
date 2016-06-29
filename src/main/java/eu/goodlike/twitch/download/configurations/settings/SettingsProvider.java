package eu.goodlike.twitch.download.configurations.settings;

import java.util.Optional;

/**
 * <pre>
 * Provides settings values
 *
 * These values only represent the settings, but may not be actually used (i.e. when an option overrides a setting),
 * hence why no transformation occurs yet
 * </pre>
 */
public interface SettingsProvider {

    /**
     * @return application name setting, using default value if necessary
     */
    String getApplicationNameSetting();

    /**
     * @return ffmpeg options setting, using default value if necessary
     */
    String getFfmpegOptionsSetting();

    /**
     * @return output format setting, using default value if necessary
     */
    String getOutputFormatSetting();

    /**
     * @return client id setting, using default value if necessary
     */
    String getClientIdSetting();

    /**
     * @return oauth setting as found on the properties file, Optional::empty if none was found
     */
    Optional<String> getOauthSetting();

}
