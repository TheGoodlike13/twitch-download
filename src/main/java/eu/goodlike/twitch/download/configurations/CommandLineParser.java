package eu.goodlike.twitch.download.configurations;

import com.google.common.collect.ImmutableList;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static eu.goodlike.twitch.download.configurations.options.Explanations.*;
import static java.util.stream.Collectors.toList;

public final class CommandLineParser {

    public Optional<Options> getOptions(String... args) {
        Namespace parsedArgs;
        try {
            parsedArgs = parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            return Optional.empty();
        }

        Options.Builder builder = Options.builder()
                .setFfmpegEnabled(!parsedArgs.getBoolean(DOWNLOAD_KEY))
                .setFfmpegAppendEnabled(!parsedArgs.getBoolean(FFMPEG_REPLACE_KEY))
                .setDebugEnabled(!parsedArgs.getBoolean(HIDE_DEBUG_KEY))
                .setProcessPrintingEnabled(!parsedArgs.getBoolean(HIDE_PROCESS_OUTPUT_KEY))
                .setPlaylistCleanupEnabled(!parsedArgs.getBoolean(NO_PLAYLIST_CLEAN_KEY))
                .setCombinePlaylistPartsEnabled(!parsedArgs.getBoolean(NO_PLAYLIST_OPTIMIZATION_KEY))
                .setDefaultToSourceWhenMissingEnabled(!parsedArgs.getBoolean(SKIP_MISSING_QUALITY_KEY));

        Optional.ofNullable(parsedArgs.getString(FFMPEG_OPTIONS_KEY))
                .filter(str -> !str.isEmpty())
                .map(str -> str.split(" "))
                .map(Arrays::stream)
                .map(stream -> stream.filter(option -> !option.isEmpty()).collect(toList()))
                .filter(list -> !list.isEmpty())
                .map(ImmutableList::copyOf)
                .ifPresent(builder::setAdditionalFfmpegOptions);

        Optional.ofNullable(parsedArgs.getString(LOG_FILE_KEY))
                .filter(str -> !str.isEmpty())
                .ifPresent(builder::setLogFile);

        Optional.ofNullable(parsedArgs.getString(OUTPUT_KEY))
                .filter(str -> !str.isEmpty())
                .ifPresent(builder::setCustomOutputFileFormat);

        Optional.ofNullable(parsedArgs.getString(QUALITY_KEY))
                .filter(str -> !str.isEmpty())
                .ifPresent(builder::setCustomQualityLevel);

        Optional.ofNullable(parsedArgs.getInt(THREAD_MAX_KEY))
                .filter(threads -> threads > 0)
                .ifPresent(builder::setMaxNumberOfThreads);

        Optional.ofNullable(parsedArgs.<String>getList(VOD_ID_KEY))
                .filter(list -> !list.isEmpty())
                .ifPresent(builder::setVodIds);

        return Optional.of(builder.build());
    }

    // CONSTRUCTORS

    public CommandLineParser(String applicationName) {
        this.parser = ArgumentParsers.newArgumentParser(applicationName)
                .defaultHelp(true)
                .description("Downloads twitch VoDs and combines them using ffmpeg.")
                .epilog("For additional questions and help, refer to https://github.com/TheGoodlike13/twitch-download");

        this.parser.addArgument("-dl", "--download")
                .dest(DOWNLOAD_KEY)
                .action(Arguments.storeTrue())
                .help(DOWNLOAD_EXPLANATION);
        this.parser.addArgument("-fr", "-ffmpeg_replace")
                .dest(FFMPEG_REPLACE_KEY)
                .action(Arguments.storeTrue())
                .help(CUSTOM_FFMPEG_MODE_EXPLANATION);
        this.parser.addArgument("-hd", "--hide_debug")
                .dest(HIDE_DEBUG_KEY)
                .action(Arguments.storeTrue())
                .help(DEBUG_OUTPUT_EXPLANATION);
        this.parser.addArgument("-hpo", "--hide_process_output")
                .dest(HIDE_PROCESS_OUTPUT_KEY)
                .action(Arguments.storeTrue())
                .help(PROCESS_OUTPUT_EXPLANATION);
        this.parser.addArgument("-npc", "--no_playlist_clean")
                .dest(NO_PLAYLIST_CLEAN_KEY)
                .action(Arguments.storeTrue())
                .help(PLAYLIST_CLEAN_EXPLANATION);
        this.parser.addArgument("-npo", "--no_playlist_optimization")
                .dest(NO_PLAYLIST_OPTIMIZATION_KEY)
                .action(Arguments.storeTrue())
                .help(PLAYLIST_OPTIMIZATION_EXPLANATION);
        this.parser.addArgument("-smq", "--skip_missing_quality")
                .dest(SKIP_MISSING_QUALITY_KEY)
                .action(Arguments.storeTrue())
                .help(DEFAULT_TO_BEST_EXPLANATION);

        this.parser.addArgument("-fo", "--ffmpeg_options")
                .dest(FFMPEG_OPTIONS_KEY)
                .help(CUSTOM_FFMPEG_EXPLANATION);
        this.parser.addArgument("-l", "--log_file")
                .dest(LOG_FILE_KEY)
                .help(LOG_FILE_EXPLANATION);
        this.parser.addArgument("-o", "--output")
                .dest(OUTPUT_KEY)
                .help(CUSTOM_FILE_FORMAT_EXPLANATION);
        this.parser.addArgument("-q", "--quality")
                .dest(QUALITY_KEY)
                .choices(POSSIBLE_QUALITY_LEVELS)
                .help(CUSTOM_QUALITY_EXPLANATION);
        this.parser.addArgument("-tm", "--threads_max")
                .dest(THREAD_MAX_KEY)
                .type(Integer.class)
                .help(MAX_THREAD_EXPLANATION);

        this.parser.addArgument("vodId").nargs("+")
                .dest(VOD_ID_KEY)
                .help(VOD_ID_EXPLANATION);
    }

    // PRIVATE

    private final ArgumentParser parser;

    private static final List<String> POSSIBLE_QUALITY_LEVELS = ImmutableList.of(
            "audio_only", "mobile", "low", "medium", "high", "source");

    private static final String DOWNLOAD_KEY = "DL";
    private static final String FFMPEG_REPLACE_KEY = "FR";
    private static final String HIDE_DEBUG_KEY = "HD";
    private static final String HIDE_PROCESS_OUTPUT_KEY = "HPO";
    private static final String NO_PLAYLIST_CLEAN_KEY = "NPC";
    private static final String NO_PLAYLIST_OPTIMIZATION_KEY = "NPO";
    private static final String SKIP_MISSING_QUALITY_KEY = "SMQ";

    private static final String FFMPEG_OPTIONS_KEY = "OPTIONS";
    private static final String LOG_FILE_KEY = "FILE";
    private static final String OUTPUT_KEY = "FORMAT";
    private static final String QUALITY_KEY = "Q";
    private static final String THREAD_MAX_KEY = "N";

    private static final String VOD_ID_KEY = "vodId";

}
