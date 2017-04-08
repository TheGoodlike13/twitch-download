package eu.goodlike.twitch.download.configurations.options;

import eu.goodlike.neat.Null;
import net.sourceforge.argparse4j.inf.Namespace;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static eu.goodlike.twitch.download.configurations.options.CommandLineKeys.*;
import static eu.goodlike.twitch.download.configurations.options.DefaultOptions.DEFAULT_MAX_NUMBER_OF_THREADS;
import static eu.goodlike.twitch.download.configurations.options.DefaultOptions.DEFAULT_QUALITY_LEVEL;

/**
 * Parses given namespace into options
 */
public final class OptionsParser implements OptionsProvider {

    @Override
    public boolean isFfmpegEnabled() {
        return !arguments.getBoolean(DOWNLOAD_KEY);
    }

    @Override
    public boolean isFfmpegAppendEnabled() {
        return !arguments.getBoolean(FFMPEG_REPLACE_KEY);
    }

    @Override
    public boolean isDebugOutputEnabled() {
        return !arguments.getBoolean(HIDE_DEBUG_KEY);
    }

    @Override
    public boolean isProcessOutputEnabled() {
        return !arguments.getBoolean(HIDE_PROCESS_OUTPUT_KEY);
    }

    @Override
    public boolean isPlaylistCleanEnabled() {
        return !arguments.getBoolean(NO_PLAYLIST_CLEAN_KEY);
    }

    @Override
    public boolean isPlaylistOptimizationEnabled() {
        return arguments.getBoolean(PLAYLIST_OPTIMIZATION_KEY);
    }

    @Override
    public boolean isDefaultToSourceEnabled() {
        return !arguments.getBoolean(SKIP_MISSING_QUALITY_KEY);
    }

    @Override
    public Optional<String> getAdditionalFfmpegOptions() {
        return Optional.ofNullable(arguments.getString(FFMPEG_OPTIONS_KEY))
                .filter(str -> !str.isEmpty());
    }

    @Override
    public Optional<String> getLogFileLocation() {
        return Optional.ofNullable(arguments.getString(LOG_FILE_KEY))
                .filter(str -> !str.isEmpty());
    }

    @Override
    public Optional<String> getOutputFormatOverride() {
        return Optional.ofNullable(arguments.getString(OUTPUT_KEY))
                .filter(str -> !str.isEmpty());
    }

    @Override
    public String getQualityLevel() {
        return Optional.ofNullable(arguments.getString(QUALITY_KEY))
                .filter(str -> !str.isEmpty())
                .map(String::toLowerCase)
                .orElse(DEFAULT_QUALITY_LEVEL);
    }

    @Override
    public int getMaxConcurrentThreads() {
        return Optional.ofNullable(arguments.getInt(THREAD_MAX_KEY))
                .filter(threads -> threads > 0)
                .orElse(DEFAULT_MAX_NUMBER_OF_THREADS);
    }

    @Override
    public List<String> getVodIds() {
        return Optional.ofNullable(arguments.<String>getList(VOD_ID_KEY))
                .orElse(Collections.emptyList());
    }

    // CONSTRUCTORS

    public static Optional<OptionsProvider> from(CommandLineParser commandLineParser, String... args) {
        Null.check(commandLineParser).ifAny("Command line parser cannot be null");
        return commandLineParser.parse(args).map(OptionsParser::new);
    }

    public OptionsParser(Namespace arguments) {
        Null.check(arguments).ifAny("Arguments cannot be null");

        this.arguments = arguments;
    }

    // PRIVATE

    private final Namespace arguments;

}
