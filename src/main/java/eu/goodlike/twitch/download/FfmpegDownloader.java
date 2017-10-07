package eu.goodlike.twitch.download;

import com.google.common.collect.ImmutableList;
import eu.goodlike.cmd.CommandLineRunner;
import eu.goodlike.functional.Futures;
import eu.goodlike.io.FileUtils;
import eu.goodlike.io.log.CustomizedLogger;
import eu.goodlike.neat.Null;
import eu.goodlike.twitch.download.configurations.policy.FfmpegPolicy;
import eu.goodlike.twitch.download.configurations.policy.OutputPolicy;
import eu.goodlike.twitch.download.configurations.policy.PlaylistPolicy;
import eu.goodlike.twitch.download.http.filename.FilenameResolver;
import eu.goodlike.twitch.m3u8.TwitchM3U8Writer;
import eu.goodlike.twitch.m3u8.TwitchM3U8WriterFactory;
import eu.goodlike.twitch.m3u8.media.MediaPlaylist;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * Handles download of files using ffmpeg
 */
public final class FfmpegDownloader {

    /**
     * @return CompletableFuture which will complete when the VoD is downloaded; this CompletableFuture
     * will wait until ffmpeg process is done and clean up afterwards if needed
     * @throws NullPointerException if media playlist is null
     */
    public CompletableFuture<?> download(MediaPlaylist mediaPlaylist, int vodId) {
        Null.check(mediaPlaylist).ifAny("Media playlist cannot be null");

        String outputFileFormat = outputPolicy.getOutputFormat();
        Optional<String> outputNameOptional = filenameResolver.resolveOutputName(outputFileFormat, vodId)
                .map(FileUtils::findAvailableName);
        if (!outputNameOptional.isPresent()) {
            debugLogger.logMessage("Cannot resolve name for output file: " + outputFileFormat);
            return CompletableFuture.completedFuture(null);
        }
        String outputName = outputNameOptional.get();

        String inputName = getInputName(outputName);
        Optional<Path> pathOptional = FileUtils.getPath(inputName)
                .map(Path::normalize);
        if (!pathOptional.isPresent()) {
            outputNameOptional
                    .ifPresent(name -> debugLogger.logMessage("Output file is not a valid path: " + name));
            return CompletableFuture.completedFuture(null);
        }
        Path path = pathOptional.get();

        TwitchM3U8Writer twitchM3U8Writer = twitchM3U8WriterFactory.newInstance(mediaPlaylist);
        Optional<File> fileOptional = twitchM3U8Writer.writeMediaPlaylist(path);
        if (!fileOptional.isPresent()) {
            debugLogger.logMessage("Could not create playlist file from stream at: " + path);
            return CompletableFuture.completedFuture(null);
        }
        CompletableFuture<File> fileFuture = Futures.fromOptional(fileOptional, () -> null);

        List<String> commandLineArguments = getFfmpegArguments(ffmpegPolicy.getFfmpegOptions(), path.toString(), outputName);
        Optional<Process> processOptional = commandLineRunner.execute(commandLineArguments);
        if (!processOptional.isPresent())
            debugLogger.logMessage("Couldn't execute process: " + commandLineArguments.stream().collect(Collectors.joining(" ")));
        else
            fileFuture = fileFuture.whenComplete(awaitExecution(processOptional.get()));

        if (playlistPolicy.isCleanupPlaylistEnabled())
            fileFuture = fileFuture.whenComplete(deletePlaylistFile(path));

        return fileFuture;
    }

    // CONSTRUCTORS

    public FfmpegDownloader(CommandLineRunner commandLineRunner, FfmpegPolicy ffmpegPolicy, OutputPolicy outputPolicy,
                            PlaylistPolicy playlistPolicy, FilenameResolver filenameResolver, CustomizedLogger debugLogger,
                            TwitchM3U8WriterFactory twitchM3U8WriterFactory) {

        Null.check(commandLineRunner, ffmpegPolicy, outputPolicy, playlistPolicy, filenameResolver, debugLogger,
                twitchM3U8WriterFactory).ifAny("Command line runned, ffmpeg policy, output policy, " +
                "playlist policy, filename resolver, logger and twitch writer factory cannot be null");

        this.commandLineRunner = commandLineRunner;
        this.ffmpegPolicy = ffmpegPolicy;
        this.outputPolicy = outputPolicy;
        this.playlistPolicy = playlistPolicy;
        this.filenameResolver = filenameResolver;
        this.debugLogger = debugLogger;
        this.twitchM3U8WriterFactory = twitchM3U8WriterFactory;
    }

    // PRIVATE

    private final CommandLineRunner commandLineRunner;
    private final FfmpegPolicy ffmpegPolicy;
    private final OutputPolicy outputPolicy;
    private final PlaylistPolicy playlistPolicy;
    private final FilenameResolver filenameResolver;
    private final CustomizedLogger debugLogger;
    private final TwitchM3U8WriterFactory twitchM3U8WriterFactory;

    private String getInputName(String outputName) {
        return com.google.common.io.Files.getNameWithoutExtension(outputName) + " playlist.m3u8";
    }

    private List<String> getFfmpegArguments(List<String> ffmpegOptions, String inputFileLocation, String outputFileLocation) {
        ImmutableList.Builder<String> builder = ImmutableList.builder();
        return builder.add("ffmpeg")
                .addAll(ENSURE_HTTP_ALLOWED_ARGS)
                .add(INPUT_ARG).add(inQuotes(inputFileLocation))
                .addAll(ffmpegOptions)
                .add(inQuotes(outputFileLocation))
                .build();
    }

    private String inQuotes(String string) {
        return "\"" + string + "\"";
    }

    private BiConsumer<File, Throwable> awaitExecution(Process process) {
        return (any, ex) -> {
            try {
                process.waitFor();
            } catch (InterruptedException e) {
                debugLogger.logMessage("Unexpected interruption while waiting for a process to end");
            }
        };
    }

    private BiConsumer<File, Throwable> deletePlaylistFile(Path path) {
        return (any, ex) -> {
            try {
                Files.delete(path);
            } catch (IOException e) {
                debugLogger.logMessage("Couldn't delete playlist file at: " + path);
            }
        };
    }

    private static final List<String> ENSURE_HTTP_ALLOWED_ARGS = ImmutableList.of("-protocol_whitelist", "file,tcp,http");
    private static final String INPUT_ARG = "-i";

}
