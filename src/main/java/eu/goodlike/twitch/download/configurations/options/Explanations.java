package eu.goodlike.twitch.download.configurations.options;

public final class Explanations {

    public static final String DEFAULT_TO_BEST_EXPLANATION =
            "do not default to source quality when custom quality is missing" + System.lineSeparator() +
                    "If you choose a custom quality, but a VoD does not have it for some reason, best " +
                    "available quality will be used instead. Use this option to skip the VoD instead.";

    public static final String PLAYLIST_OPTIMIZATION_EXPLANATION =
            "disable playlist optimization" + System.lineSeparator() +
                    "The stream VoDs are sometimes stored in segments larger than the playlist reports. " +
                    "These segments can be automatically concatenated. If you do not wish to do this for " +
                    "some reason, use this option.";

    public static final String DOWNLOAD_EXPLANATION =
            "don't use ffmpeg, download parts instead" + System.lineSeparator() +
                    "If you wish to keep the parts separate, or use your own method to combine them, use " +
                    "this method. The parts will keep their filename (it is sufficient to determine " +
                    "chronological order) and will be downloaded into a separate folder, which will use " +
                    "the output filename structure, minus extension, for the name.";

    public static final String PLAYLIST_CLEAN_EXPLANATION =
            "keep playlists after combining" + System.lineSeparator() +
                    "Normally, the playlist file will be created for use with ffmpeg. If you wish to keep " +
                    "this file (i.e. debug purposes), use this option.";

    public static final String PROCESS_OUTPUT_EXPLANATION =
            "hide ffmpeg output" + System.lineSeparator() +
                    "During the combination stage, if you allow ffmpeg usage, the output will be shown on " +
                    "the command line. Use this option to hide it. Beware, that in the case where the VoD " +
                    "is large, it may take quite some time to finish downloading it, and hiding the output " +
                    "will leave you without any information as to the progress of the work.";

    public static final String DEBUG_OUTPUT_EXPLANATION =
            "hide debug messages" + System.lineSeparator() +
                    "During various stages of the process, information will be printed to the command " +
                    "line, such as http requests that are made. Use this option to hide it.";

    public static final String MAX_THREAD_EXPLANATION =
            "max number of concurrent ffmpeg instances" + System.lineSeparator() +
                    "Multiple VoDs will be downloaded in parallel, by default limited by amount of " +
                    "cores in the system. Use this option to limit or unlock amount of threads created. " +
                    "Invalid values (N <= 0) will be ignored.";

    public static final String LOG_FILE_EXPLANATION =
            "file to log everything into" + System.lineSeparator() +
                    "Normally, all the output will only be printed into the console. Use this option if " +
                    "you want to also keep all the output in a file as well. This option IGNORES -hd and " +
                    "-hpo. In other words, the log file will contain ALL output, even if it was hidden on " +
                    "console.";

    public static final String CUSTOM_FFMPEG_MODE_EXPLANATION =
            "make additional ffmpeg options replace instead of append" + System.lineSeparator() +
                    "If you are going to use ffmpeg, the options passed into it can be specified in " +
                    "twitch-download.properties. You can append additional options using -fo. Use this " +
                    "option to ignore the properties file and only use the options with -fo. If no valid " +
                    "-fo option is specified, this option will be ignored.";

    public static final String CUSTOM_FFMPEG_EXPLANATION =
            "adjust default ffmpeg commands" + System.lineSeparator() +
                    "If you are going to use ffmpeg, the options passed into it can be specified in " +
                    "twitch-download.properties. However, you may want to override or append additional " +
                    "options (i.e. cutting). Use this option to define these ffmpeg options.";

    public static final String CUSTOM_QUALITY_EXPLANATION =
            "custom quality level" + System.lineSeparator() +
                    "By default, source quality VoDs will be downloaded. Use this option to download " +
                    "other quality VoDs instead. If this quality is missing, best available quality " +
                    "will be used, unless -smq was used, in which case the VoD will be skipped instead.";

    public static final String CUSTOM_FILE_FORMAT_EXPLANATION =
            "custom output file format" + System.lineSeparator() +
                    "The default file format is defined in twitch-download.properties. The default value " +
                    "of it is:" + System.lineSeparator() +
                    "(<recorded_at>) <channel:name> <title>.mp4" + System.lineSeparator() +
                    "The values in <> represent VoD information, found at:" + System.lineSeparator() +
                    "https://api.twitch.tv/kraken/videos/v{vodId}?api_version=3" + System.lineSeparator() +
                    "Also, all characters except these will be replaced with '_' to avoid filename issues: " + System.lineSeparator() +
                    "A-Za-z0-9 ,'_+!@#$%^&();=-.";

    public static final String VOD_ID_EXPLANATION =
            "id of the VoDs you wish to download" + System.lineSeparator() +
                    "The video id can be specified in one of four ways, checked in order:" + System.lineSeparator() +
                    "1) http link to the VoD, " +
                    "i.e. https://www.twitch.tv/davidangel64/v/73595705" + System.lineSeparator() +
                    "2) file, that has lines of video ids (links and other files allowed), " +
                    "i.e. vods.txt" + System.lineSeparator() +
                    "3) video id with the 'v' prefix, i.e. v73595705" + System.lineSeparator() +
                    "4) video id without the 'v' prefix, i.e. 73595705" + System.lineSeparator() +
                    "All ids are processed linearly and all duplicates are automatically ignored, " +
                    "regardless of how they are specified.";

    // PRIVATE

    private Explanations() {
        throw new AssertionError("Do not instantiate, use static methods!");
    }

}
