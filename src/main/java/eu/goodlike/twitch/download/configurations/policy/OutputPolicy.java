package eu.goodlike.twitch.download.configurations.policy;

import com.google.common.io.Files;
import eu.goodlike.neat.Null;
import eu.goodlike.twitch.download.configurations.options.OptionsProvider;
import eu.goodlike.twitch.download.configurations.settings.SettingsProvider;

/**
 * Defines configurations for output
 */
public final class OutputPolicy {

    /**
     * @return output format to be used for output file
     */
    public String getOutputFormat() {
        return outputFormat;
    }

    /**
     * @return output format to be used for output folder (when ffmpeg is off)
     */
    public String getOutputFolderFormat() {
        return Files.getNameWithoutExtension(outputFormat);
    }

    // CONSTRUCTORS

    public static OutputPolicy from(SettingsProvider settingsProvider, OptionsProvider optionsProvider) {
        Null.check(settingsProvider, optionsProvider).ifAny("Settings and options providers cannot be null");
        return new OutputPolicy(optionsProvider.getOutputFormatOverride().orElse(settingsProvider.getOutputFormatSetting()));
    }

    public OutputPolicy(String outputFormat) {
        Null.check(outputFormat).ifAny("Output format cannot be null");

        this.outputFormat = outputFormat;
    }

    // PRIVATE

    private final String outputFormat;

}
