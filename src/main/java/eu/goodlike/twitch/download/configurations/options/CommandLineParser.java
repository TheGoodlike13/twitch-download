package eu.goodlike.twitch.download.configurations.options;

import eu.goodlike.neat.Null;
import eu.goodlike.twitch.download.configurations.settings.SettingsProvider;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

import java.util.Optional;

import static eu.goodlike.twitch.download.configurations.options.CommandLineKeys.*;
import static eu.goodlike.twitch.download.configurations.options.Explanations.*;

/**
 * Parses command line arguments using given ArgumentParser
 */
public final class CommandLineParser {

    /**
     * @return Namespace filled with given arguments, Optional::empty if parsing failed
     */
    public Optional<Namespace> parse(String... args) {
        Namespace parsedArgs;
        try {
            parsedArgs = argumentParser.parseArgs(args);
        } catch (ArgumentParserException e) {
            argumentParser.handleError(e);
            return Optional.empty();
        }
        return Optional.of(parsedArgs);
    }

    // CONSTRUCTORS

    public static CommandLineParser newInstance(SettingsProvider settingsProvider) {
        ArgumentParser argumentParser = ArgumentParsers.newArgumentParser(settingsProvider.getApplicationNameSetting())
                .defaultHelp(true)
                .description("Downloads twitch VoDs and combines them using ffmpeg.")
                .epilog("For additional questions and help, refer to https://github.com/TheGoodlike13/twitch-download");

        argumentParser.addArgument("-dl", "--download")
                .dest(DOWNLOAD_KEY)
                .action(Arguments.storeTrue())
                .help(DOWNLOAD_EXPLANATION);
        argumentParser.addArgument("-fr", "-ffmpeg_replace")
                .dest(FFMPEG_REPLACE_KEY)
                .action(Arguments.storeTrue())
                .help(CUSTOM_FFMPEG_MODE_EXPLANATION);
        argumentParser.addArgument("-hd", "--hide_debug")
                .dest(HIDE_DEBUG_KEY)
                .action(Arguments.storeTrue())
                .help(DEBUG_OUTPUT_EXPLANATION);
        argumentParser.addArgument("-hpo", "--hide_process_output")
                .dest(HIDE_PROCESS_OUTPUT_KEY)
                .action(Arguments.storeTrue())
                .help(PROCESS_OUTPUT_EXPLANATION);
        argumentParser.addArgument("-npc", "--no_playlist_clean")
                .dest(NO_PLAYLIST_CLEAN_KEY)
                .action(Arguments.storeTrue())
                .help(PLAYLIST_CLEAN_EXPLANATION);
        argumentParser.addArgument("-po", "--playlist_optimization")
                .dest(PLAYLIST_OPTIMIZATION_KEY)
                .action(Arguments.storeTrue())
                .help(PLAYLIST_OPTIMIZATION_EXPLANATION);
        argumentParser.addArgument("-smq", "--skip_missing_quality")
                .dest(SKIP_MISSING_QUALITY_KEY)
                .action(Arguments.storeTrue())
                .help(DEFAULT_TO_BEST_EXPLANATION);

        argumentParser.addArgument("-fo", "--ffmpeg_options")
                .dest(FFMPEG_OPTIONS_KEY)
                .help(CUSTOM_FFMPEG_EXPLANATION);
        argumentParser.addArgument("-l", "--log_file")
                .dest(LOG_FILE_KEY)
                .help(LOG_FILE_EXPLANATION);
        argumentParser.addArgument("-o", "--output")
                .dest(OUTPUT_KEY)
                .help(CUSTOM_FILE_FORMAT_EXPLANATION);
        argumentParser.addArgument("-q", "--quality")
                .dest(QUALITY_KEY)
                .help(CUSTOM_QUALITY_EXPLANATION);
        argumentParser.addArgument("-tm", "--threads_max")
                .dest(THREAD_MAX_KEY)
                .type(Integer.class)
                .help(MAX_THREAD_EXPLANATION);

        argumentParser.addArgument("vodId").nargs("+")
                .dest(VOD_ID_KEY)
                .help(VOD_ID_EXPLANATION);

        return new CommandLineParser(argumentParser);
    }

    public CommandLineParser(ArgumentParser argumentParser) {
        Null.check(argumentParser).ifAny("Argument parser cannot be null");
        this.argumentParser = argumentParser;
    }

    // PRIVATE

    private final ArgumentParser argumentParser;

}
