package eu.goodlike.twitch.download;

import eu.goodlike.io.FileUtils;
import eu.goodlike.io.log.CustomizedLogger;
import eu.goodlike.neat.Null;
import eu.goodlike.twitch.CompletableFutureErrorHandler;
import eu.goodlike.twitch.download.configurations.policy.OutputPolicy;
import eu.goodlike.twitch.download.http.filename.FilenameResolver;
import eu.goodlike.twitch.m3u8.media.MediaPlaylist;
import eu.goodlike.twitch.m3u8.media.TwitchStreamPart;
import eu.goodlike.twitch.vod.VideoDownloader;
import okhttp3.HttpUrl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Handles manual download of files
 */
public final class ManualDownloader {

    /**
     * @return all downloaded files from media playlist; uses vodId for file output formatting
     * @throws NullPointerException if media playlist is null
     */
    public List<CompletableFuture<File>> download(MediaPlaylist mediaPlaylist, int vodId) {
        Null.check(mediaPlaylist).ifAny("Media playlist cannot be null");

        String outputFolder = outputPolicy.getOutputFolderFormat();
        Optional<String> outputName = filenameResolver.resolveOutputName(outputFolder, vodId)
                .map(FileUtils::findAvailableName);
        if (!outputName.isPresent()) {
            debugLogger.logMessage("Cannot resolve name for output folder: " + outputFolder);
            return Collections.emptyList();
        }

        Optional<Path> pathOptional = outputName.flatMap(FileUtils::getPath);
        if (!pathOptional.isPresent()) {
            outputName
                    .ifPresent(name -> debugLogger.logMessage("Output folder is not a valid path: " + name));
            return Collections.emptyList();
        }

        Path path = pathOptional.get();
        try {
            Files.createDirectory(path);
        } catch (IOException e) {
            debugLogger.logMessage("Cannot create directory at: " + path);
            return Collections.emptyList();
        }

        return downloadFilesInto(path, mediaPlaylist);
    }

    // CONSTRUCTORS

    public ManualDownloader(VideoDownloader videoDownloader, OutputPolicy outputPolicy,
                            FilenameResolver filenameResolver, CustomizedLogger debugLogger,
                            CompletableFutureErrorHandler errorHandler) {

        Null.check(videoDownloader, outputPolicy, filenameResolver, debugLogger, errorHandler)
                .ifAny("Video downloader, output policy, filename resolver, logger and error handler cannot be null");

        this.videoDownloader = videoDownloader;
        this.outputPolicy = outputPolicy;
        this.filenameResolver = filenameResolver;
        this.debugLogger = debugLogger;
        this.errorHandler = errorHandler;
    }

    // PRIVATE

    private final VideoDownloader videoDownloader;
    private final OutputPolicy outputPolicy;
    private final FilenameResolver filenameResolver;
    private final CustomizedLogger debugLogger;
    private final CompletableFutureErrorHandler errorHandler;

    private List<CompletableFuture<File>> downloadFilesInto(Path folder, MediaPlaylist mediaPlaylist) {
        List<CompletableFuture<File>> processes = new ArrayList<>();
        for (TwitchStreamPart part : mediaPlaylist.getStreamParts()) {
            Optional<HttpUrl> locationUrlOptional = part.getLocationUrl();
            if (!locationUrlOptional.isPresent()) {
                debugLogger.logMessage("Stream segment is not a valid url: " + part.getFullLocation());
                return null;
            }
            HttpUrl locationUrl = locationUrlOptional.get();
            CompletableFuture<File> fileFuture = videoDownloader.download(folder.resolve(part.getLocationName()), locationUrl)
                    .whenComplete(errorHandler.logOnError("Could not download file from: " + locationUrl));

            processes.add(fileFuture);
        }
        return processes;
    }

}
