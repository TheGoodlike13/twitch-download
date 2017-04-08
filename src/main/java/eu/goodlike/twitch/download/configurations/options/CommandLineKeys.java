package eu.goodlike.twitch.download.configurations.options;

public final class CommandLineKeys {

    public static final String DOWNLOAD_KEY = "DL";
    public static final String FFMPEG_REPLACE_KEY = "FR";
    public static final String HIDE_DEBUG_KEY = "HD";
    public static final String HIDE_PROCESS_OUTPUT_KEY = "HPO";
    public static final String NO_PLAYLIST_CLEAN_KEY = "NPC";
    public static final String PLAYLIST_OPTIMIZATION_KEY = "PO";
    public static final String SKIP_MISSING_QUALITY_KEY = "SMQ";

    public static final String FFMPEG_OPTIONS_KEY = "OPTIONS";
    public static final String LOG_FILE_KEY = "FILE";
    public static final String OUTPUT_KEY = "FORMAT";
    public static final String QUALITY_KEY = "Q";
    public static final String THREAD_MAX_KEY = "N";

    public static final String VOD_ID_KEY = "vodId";

    // PRIVATE

    private CommandLineKeys() {
        throw new AssertionError("Do not instantiate, use static methods!");
    }

}
