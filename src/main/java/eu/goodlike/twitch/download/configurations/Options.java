package eu.goodlike.twitch.download.configurations;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import eu.goodlike.misc.Singleton;
import eu.goodlike.neat.Null;
import eu.goodlike.twitch.download.log.CustomizedLogger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public final class Options {

    public CustomizedLogger getLoggerForDebug() {
        List<CustomizedLogger> customizedLoggers = new ArrayList<>();
        if (debugEnabled)
            customizedLoggers.add(CustomizedLogger.forConsole());

        fileLogger.get().ifPresent(customizedLoggers::add);

        return CustomizedLogger.combine(customizedLoggers);
    }

    public CustomizedLogger getLoggerForProcess() {
        List<CustomizedLogger> customizedLoggers = new ArrayList<>();
        if (processPrintingEnabled)
            customizedLoggers.add(CustomizedLogger.forConsole());

        fileLogger.get().ifPresent(customizedLoggers::add);

        return CustomizedLogger.combine(customizedLoggers);
    }

    public List<String> getFullFfmpegOptions(Settings settings) {
        return !ffmpegAppendEnabled
                ? additionalFfmpegOptions.isEmpty() ? settings.getDefaultFfmpegOptions() : additionalFfmpegOptions
                : Stream.concat(settings.getDefaultFfmpegOptions().stream(),
                additionalFfmpegOptions.stream())
                .collect(toList());
    }

    public String getOutputFileFormat(Settings settings) {
        return Optional.ofNullable(customOutputFileFormat).orElse(settings.getDefaultOutputFormat());
    }

    public boolean isDefaultingToSourceWhenMissingEnabled() {
        return defaultToSourceWhenMissingEnabled;
    }

    public boolean isPlaylistPartCombiningEnabled() {
        return combinePlaylistPartsEnabled;
    }

    public boolean isFFMPEGEnabled() {
        return ffmpegEnabled;
    }

    public boolean isPlaylistCleanupEnabled() {
        return playlistCleanupEnabled;
    }

    public int getMaxNumberOfThreads() {
        return maxNumberOfThreads;
    }

    public Optional<String> getCustomQualityLevel() {
        return Optional.ofNullable(customQualityLevel);
    }

    public List<String> getVodIds() {
        return vodIds;
    }

    // CONSTRUCTORS

    public static Builder builder() {
        return new Builder();
    }

    private Options(boolean ffmpegAppendEnabled, boolean defaultToSourceWhenMissingEnabled,
                    boolean combinePlaylistPartsEnabled, boolean ffmpegEnabled, boolean playlistCleanupEnabled,
                    boolean processPrintingEnabled, boolean debugEnabled, int maxNumberOfThreads,
                    String logFile, String customQualityLevel, String customOutputFileFormat,
                    List<String> additionalFfmpegOptions, List<String> vodIds) {
        this.ffmpegAppendEnabled = ffmpegAppendEnabled;
        this.defaultToSourceWhenMissingEnabled = defaultToSourceWhenMissingEnabled;
        this.combinePlaylistPartsEnabled = combinePlaylistPartsEnabled;
        this.ffmpegEnabled = ffmpegEnabled;
        this.playlistCleanupEnabled = playlistCleanupEnabled;
        this.processPrintingEnabled = processPrintingEnabled;
        this.debugEnabled = debugEnabled;

        this.maxNumberOfThreads = maxNumberOfThreads;
        this.customQualityLevel = customQualityLevel;
        this.customOutputFileFormat = customOutputFileFormat;
        this.additionalFfmpegOptions = additionalFfmpegOptions;

        this.logFile = logFile;
        this.fileLogger = Singleton.lazy(() -> Optional.ofNullable(logFile).map(CustomizedLogger::forFile));

        this.vodIds = vodIds;
    }

    // PRIVATE

    private final boolean ffmpegAppendEnabled;
    private final boolean defaultToSourceWhenMissingEnabled;
    private final boolean combinePlaylistPartsEnabled;
    private final boolean ffmpegEnabled;
    private final boolean playlistCleanupEnabled;
    private final boolean processPrintingEnabled;
    private final boolean debugEnabled;

    private final int maxNumberOfThreads;
    private final String customQualityLevel;
    private final String customOutputFileFormat;
    private final List<String> additionalFfmpegOptions;

    private final String logFile;
    private final Singleton<Optional<CustomizedLogger>> fileLogger;

    private final List<String> vodIds;

    private static final boolean DEFAULT_DEFAULT_FFMPEG_APPEND_VALUE = true;
    private static final boolean DEFAULT_DEFAULT_TO_SOURCE_VALUE = true;
    private static final boolean DEFAULT_COMBINE_PLAYLIST_PARTS_VALUE = true;
    private static final boolean DEFAULT_FFMPEG_ENABLED_VALUE = true;
    private static final boolean DEFAULT_PLAYLIST_CLEANUP_VALUE = true;
    private static final boolean DEFAULT_PROCESS_PRINTING_VALUE = true;
    private static final boolean DEFAULT_DEBUG_ENABLED_VALUE = true;

    private static final int DEFAULT_MAX_NUMBER_OF_THREADS;
    static {
        int availableCores = Runtime.getRuntime().availableProcessors();
        DEFAULT_MAX_NUMBER_OF_THREADS = availableCores > 4 ? availableCores : 4;
    }

    public static class Builder {
        private boolean ffmpegAppendEnabled = DEFAULT_DEFAULT_FFMPEG_APPEND_VALUE;
        private boolean defaultToSourceWhenMissingEnabled = DEFAULT_DEFAULT_TO_SOURCE_VALUE;
        private boolean combinePlaylistPartsEnabled = DEFAULT_COMBINE_PLAYLIST_PARTS_VALUE;
        private boolean ffmpegEnabled = DEFAULT_FFMPEG_ENABLED_VALUE;
        private boolean playlistCleanupEnabled = DEFAULT_PLAYLIST_CLEANUP_VALUE;
        private boolean processPrintingEnabled = DEFAULT_PROCESS_PRINTING_VALUE;
        private boolean debugEnabled = DEFAULT_DEBUG_ENABLED_VALUE;
        private int maxNumberOfThreads = DEFAULT_MAX_NUMBER_OF_THREADS;
        private String logFile;
        private String customQualityLevel;
        private String customOutputFileFormat;
        private List<String> additionalFfmpegOptions = Collections.emptyList();
        private List<String> vodIds = Collections.emptyList();

        public Builder setFfmpegAppendEnabled(final boolean ffmpegAppendEnabled) {
            this.ffmpegAppendEnabled = ffmpegAppendEnabled;
            return this;
        }

        public Builder setDefaultToSourceWhenMissingEnabled(final boolean defaultToSourceWhenMissingEnabled) {
            this.defaultToSourceWhenMissingEnabled = defaultToSourceWhenMissingEnabled;
            return this;
        }

        public Builder setCombinePlaylistPartsEnabled(final boolean combinePlaylistPartsEnabled) {
            this.combinePlaylistPartsEnabled = combinePlaylistPartsEnabled;
            return this;
        }

        public Builder setFfmpegEnabled(final boolean ffmpegEnabled) {
            this.ffmpegEnabled = ffmpegEnabled;
            return this;
        }

        public Builder setPlaylistCleanupEnabled(final boolean playlistCleanupEnabled) {
            this.playlistCleanupEnabled = playlistCleanupEnabled;
            return this;
        }

        public Builder setProcessPrintingEnabled(final boolean processPrintingEnabled) {
            this.processPrintingEnabled = processPrintingEnabled;
            return this;
        }

        public Builder setDebugEnabled(final boolean debugEnabled) {
            this.debugEnabled = debugEnabled;
            return this;
        }

        public Builder setMaxNumberOfThreads(final int maxNumberOfThreads) {
            this.maxNumberOfThreads = maxNumberOfThreads;
            return this;
        }

        public Builder setCustomOutputFileFormat(final String customOutputFileFormat) {
            Null.check(customOutputFileFormat).ifAny("Custom output file format cannot be null");
            this.customOutputFileFormat = customOutputFileFormat;
            return this;
        }

        public Builder setLogFile(final String logFile) {
            Null.check(logFile).ifAny("Log file cannot be null");
            this.logFile = logFile;
            return this;
        }

        public Builder setCustomQualityLevel(final String customQualityLevel) {
            Null.check(customQualityLevel).ifAny("Custom quality level cannot be null");
            this.customQualityLevel = customQualityLevel;
            return this;
        }

        public Builder setVodIds(final List<String> vodIds) {
            Null.checkList(vodIds).ifAny("Vod ids cannot be or contain null");
            this.vodIds = ImmutableList.copyOf(vodIds);
            return this;
        }

        public Builder setAdditionalFfmpegOptions(final List<String> additionalFfmpegOptions) {
            Null.check(additionalFfmpegOptions).ifAny("Additional ffmpeg options cannot be null");
            this.additionalFfmpegOptions = ImmutableList.copyOf(additionalFfmpegOptions);
            return this;
        }

        public Options build() {
            return new Options(this.ffmpegAppendEnabled, this.defaultToSourceWhenMissingEnabled, this.combinePlaylistPartsEnabled, this.ffmpegEnabled, this.playlistCleanupEnabled, this.processPrintingEnabled, this.debugEnabled, this.maxNumberOfThreads, this.logFile, this.customQualityLevel, this.customOutputFileFormat, this.additionalFfmpegOptions, this.vodIds);
        }
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("ffmpegAppendEnabled", ffmpegAppendEnabled)
                .add("defaultToSourceWhenMissingEnabled", defaultToSourceWhenMissingEnabled)
                .add("combinePlaylistPartsEnabled", combinePlaylistPartsEnabled)
                .add("ffmpegEnabled", ffmpegEnabled)
                .add("playlistCleanupEnabled", playlistCleanupEnabled)
                .add("processPrintingEnabled", processPrintingEnabled)
                .add("debugEnabled", debugEnabled)
                .add("maxNumberOfThreads", maxNumberOfThreads)
                .add("logFile", logFile)
                .add("customQualityLevel", customQualityLevel)
                .add("customOutputFileFormat", customOutputFileFormat)
                .add("additionalFfmpegOptions", additionalFfmpegOptions)
                .add("vodIds", vodIds)
                .toString();
    }
}
