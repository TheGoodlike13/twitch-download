package eu.goodlike.twitch.vod;

import eu.goodlike.functional.Futures;
import eu.goodlike.io.log.CustomizedLogger;
import eu.goodlike.neat.Null;
import eu.goodlike.twitch.CompletableFutureErrorHandler;
import eu.goodlike.twitch.download.configurations.policy.ConcurrencyPolicy;
import eu.goodlike.twitch.download.http.TwitchRequestMaker;
import okhttp3.HttpUrl;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Semaphore;

/**
 * Downloads twitch stream files, limited by concurrency policy
 */
public final class VideoDownloader {

    /**
     * @return file downloaded from url, at location; on failure, error will logger to debug
     */
    public CompletableFuture<File> download(Path location, HttpUrl url) {
        Null.check(location, url).ifAny("Location and url cannot be null");

        try {
            parallelExecutionLimiter.acquire();
        } catch (InterruptedException e) {
            return Futures.<File>failedFuture(e)
                    .whenComplete(errorHandler.logOnError("Unexpected interruption while waiting to download file at: " + url));
        }

        debugLogger.logMessage("Downloading file from: " + url);
        return twitchRequestMaker.makeRawRequest(url)
                .whenComplete(errorHandler.logOnError("Couldn't download file at: " + url))
                .thenApply(Response::body)
                .thenApply(ResponseBody::byteStream)
                .thenCompose(inputStream -> writeInputStreamToFile(inputStream, location))
                .whenComplete(errorHandler.logOnError("Couldn't write file into: " + location))
                .whenComplete((any, e) -> parallelExecutionLimiter.release());
    }

    // CONSTRUCTORS

    public VideoDownloader(ConcurrencyPolicy concurrencyPolicy, TwitchRequestMaker twitchRequestMaker,
                           CompletableFutureErrorHandler errorHandler, CustomizedLogger debugLogger) {
        this.parallelExecutionLimiter = new Semaphore(concurrencyPolicy.getMaxConcurrentThreads());
        this.twitchRequestMaker = twitchRequestMaker;
        this.errorHandler = errorHandler;
        this.debugLogger = debugLogger;
    }

    // PRIVATE

    private final Semaphore parallelExecutionLimiter;
    private final TwitchRequestMaker twitchRequestMaker;
    private final CompletableFutureErrorHandler errorHandler;
    private final CustomizedLogger debugLogger;

    private CompletableFuture<File> writeInputStreamToFile(InputStream inputStream, Path location) {
        try {
            Files.createFile(location);
            Files.copy(inputStream, location);
        } catch (IOException e) {
            return Futures.failedFuture(e);
        }
        return CompletableFuture.completedFuture(location.toFile());
    }

}
