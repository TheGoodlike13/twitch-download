package eu.goodlike.twitch.download.configurations.options;

import java.util.List;
import java.util.Optional;

/**
 * <pre>
 * Provides options, usually passed as command line parameters
 *
 * These values will be used over settings where appropriate
 * </pre>
 */
public interface OptionsProvider {

    /**
     * @return true if ffmpeg will be used, false if VoDs will be downloaded in parts instead
     */
    boolean isFfmpegEnabled();

    /**
     * @return true if additional ffmpeg options will be appended, false if they will replace the settings instead
     */
    boolean isFfmpegAppendEnabled();

    /**
     * @return true if debug output should be logged to console, false otherwise
     */
    boolean isDebugOutputEnabled();

    /**
     * @return true if process output should be logged to console, false otherwise
     */
    boolean isProcessOutputEnabled();

    /**
     * @return true if playlist file should be deleted afterwards, false otherwise
     */
    boolean isPlaylistCleanEnabled();

    /**
     * @return true if playlist parts should be combined when possible, false otherwise
     */
    boolean isPlaylistOptimizationEnabled();

    /**
     * @return true if missing qualities should default to source, false if they should just be skipped instead
     */
    boolean isDefaultToSourceEnabled();

    /**
     * @return additional ffmpeg options, Optional::empty if none were given
     */
    Optional<String> getAdditionalFfmpegOptions();

    /**
     * @return debug file location, Optional::empty if none was given
     */
    Optional<String> getLogFileLocation();

    /**
     * @return custom output file format, Optional::empty if none was given
     */
    Optional<String> getOutputFormatOverride();

    /**
     * @return quality level for downloads
     */
    String getQualityLevel();

    /**
     * @return max concurrent ffmpeg/download executions, using default value if necessary
     */
    int getMaxConcurrentThreads();

    /**
     * @return VoD ids to download, including links, files or ids themselves
     */
    List<String> getVodIds();

}
