package eu.goodlike.twitch.download.configurations.policy;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import eu.goodlike.neat.Null;
import eu.goodlike.twitch.download.configurations.options.OptionsProvider;
import eu.goodlike.twitch.download.configurations.settings.SettingsProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Defines configurations for ffmpeg usage
 */
public final class FfmpegPolicy {

    /**
     * @return true if ffmpeg should be used, false otherwise
     */
    public boolean isFfmpegEnabled() {
        return ffmpegEnabled;
    }

    /**
     * @return list of options to use with ffmpeg,
     */
    public List<String> getFfmpegOptions() {
        return ffmpegOptions;
    }

    // CONSTRUCTORS

    public static FfmpegPolicy from(SettingsProvider settingsProvider, OptionsProvider optionsProvider) {
        Null.check(settingsProvider, optionsProvider).ifAny("Settings and options providers cannot be null");

        if (!optionsProvider.isFfmpegEnabled())
            return FfmpegPolicy.ffmpegDisabled();

        List<String> options = new ArrayList<>();
        optionsProvider.getAdditionalFfmpegOptions()
                .map(FfmpegPolicy::parseOptions)
                .ifPresent(options::addAll);

        if (optionsProvider.isFfmpegAppendEnabled() || options.isEmpty())
            options.addAll(parseOptions(settingsProvider.getFfmpegOptionsSetting()));

        return new FfmpegPolicy(true, options);
    }

    public static FfmpegPolicy ffmpegDisabled() {
        return new FfmpegPolicy(false, Collections.emptyList());
    }

    public FfmpegPolicy(boolean ffmpegEnabled, List<String> ffmpegOptions) {
        Null.checkList(ffmpegOptions).ifAny("Ffmpeg options cannot be or contain null");

        this.ffmpegEnabled = ffmpegEnabled;
        this.ffmpegOptions = ImmutableList.copyOf(ffmpegOptions);
    }

    // PRIVATE

    private final boolean ffmpegEnabled;
    private final List<String> ffmpegOptions;

    private static List<String> parseOptions(String optionString) {
        optionString = optionString.trim();
        return optionString.isEmpty()
                ? Collections.emptyList()
                : Lists.newArrayList(optionString.split(" "));
    }

}
