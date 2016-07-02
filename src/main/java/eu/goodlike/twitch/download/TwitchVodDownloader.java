package eu.goodlike.twitch.download;

import eu.goodlike.cmd.CommandLineRunner;
import eu.goodlike.io.log.CustomizedLogger;
import eu.goodlike.libraries.okhttp.HttpClients;
import eu.goodlike.twitch.CompletableFutureErrorHandler;
import eu.goodlike.twitch.download.configurations.Policies;
import eu.goodlike.twitch.download.configurations.options.CommandLineParser;
import eu.goodlike.twitch.download.configurations.options.OptionsParser;
import eu.goodlike.twitch.download.configurations.options.OptionsProvider;
import eu.goodlike.twitch.download.configurations.policy.*;
import eu.goodlike.twitch.download.configurations.settings.SettingsParser;
import eu.goodlike.twitch.download.configurations.settings.SettingsProvider;
import eu.goodlike.twitch.download.http.TwitchRequestMaker;
import eu.goodlike.twitch.download.http.filename.FilenameResolver;
import eu.goodlike.twitch.m3u8.TwitchM3U8ParserFactory;
import eu.goodlike.twitch.m3u8.TwitchM3U8WriterFactory;
import eu.goodlike.twitch.m3u8.media.MediaPlaylist;
import eu.goodlike.twitch.playlist.TwitchMasterPlaylistFetcher;
import eu.goodlike.twitch.playlist.TwitchMediaPlaylistFetcher;
import eu.goodlike.twitch.stream.StreamDataFetcher;
import eu.goodlike.twitch.token.TokenFetcher;
import eu.goodlike.twitch.vod.VideoDownloader;
import okhttp3.OkHttpClient;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static eu.goodlike.twitch.download.configurations.settings.DefaultSettings.DEFAULT_PROPERTIES_FILE_PATH;

public final class TwitchVodDownloader {

    public static void main(String... args) {
        SettingsProvider settingsProvider = SettingsParser.fromFile(DEFAULT_PROPERTIES_FILE_PATH);
        CommandLineParser commandLineParser = CommandLineParser.newInstance(settingsProvider);
        OptionsParser.from(commandLineParser, args)
                .ifPresent(optionsProvider -> launchApplication(settingsProvider, optionsProvider));
    }

    // PRIVATE

    private TwitchVodDownloader() {
        throw new AssertionError("Do not instantiate this class, it is only used for 'main' method!");
    }

    private static void launchApplication(SettingsProvider settingsProvider, OptionsProvider optionsProvider) {
        Policies policies = Policies.from(settingsProvider, optionsProvider);
        ConcurrencyPolicy concurrencyPolicy = policies.getConcurrencyPolicy();
        FfmpegPolicy ffmpegPolicy = policies.getFfmpegPolicy();
        HttpRequestPolicy httpRequestPolicy = policies.getHttpRequestPolicy();
        InputPolicy inputPolicy = policies.getInputPolicy();
        LogPolicy logPolicy = policies.getLogPolicy();
        OutputPolicy outputPolicy = policies.getOutputPolicy();
        PlaylistPolicy playlistPolicy = policies.getPlaylistPolicy();

        CustomizedLogger debugLogger = logPolicy.getDebugLogger();
        CustomizedLogger processLogger = logPolicy.getProcessLogger();

        OkHttpClient okHttpClient = HttpClients.newInstance();
        CompletableFutureErrorHandler errorHandler = new CompletableFutureErrorHandler(debugLogger);
        TwitchRequestMaker twitchRequestMaker = new TwitchRequestMaker(httpRequestPolicy, okHttpClient);
        TwitchM3U8ParserFactory twitchM3U8ParserFactory = new TwitchM3U8ParserFactory(debugLogger, playlistPolicy);
        TwitchM3U8WriterFactory twitchM3U8WriterFactory = new TwitchM3U8WriterFactory(debugLogger);

        TokenFetcher tokenFetcher = new TokenFetcher(twitchRequestMaker, debugLogger);
        StreamDataFetcher streamDataFetcher = new StreamDataFetcher(twitchRequestMaker, debugLogger);
        FilenameResolver filenameResolver = new FilenameResolver(streamDataFetcher, debugLogger, playlistPolicy);

        TwitchMasterPlaylistFetcher twitchMasterPlaylistFetcher = new TwitchMasterPlaylistFetcher(tokenFetcher,
                twitchRequestMaker, twitchM3U8ParserFactory, debugLogger, errorHandler);
        TwitchMediaPlaylistFetcher twitchMediaPlaylistFetcher = new TwitchMediaPlaylistFetcher(twitchRequestMaker,
                twitchM3U8ParserFactory, debugLogger, errorHandler, playlistPolicy);

        VideoDownloader videoDownloader = new VideoDownloader(concurrencyPolicy, twitchRequestMaker, errorHandler, debugLogger);
        CommandLineRunner commandLineRunner = new CommandLineRunner(concurrencyPolicy, debugLogger, processLogger);

        FfmpegDownloader ffmpegDownloader = new FfmpegDownloader(commandLineRunner, ffmpegPolicy, outputPolicy, playlistPolicy, filenameResolver, debugLogger, twitchM3U8WriterFactory);
        ManualDownloader manualDownloader = new ManualDownloader(videoDownloader, outputPolicy, filenameResolver, debugLogger, errorHandler);

        List<CompletableFuture<File>> downloadFutures = new ArrayList<>();
        for (int vodId : inputPolicy.getVodIds()) {
            CompletableFuture<MediaPlaylist> mediaPlaylist = twitchMasterPlaylistFetcher.fetchMasterPlaylistForVodId(vodId)
                    .thenCompose(twitchMediaPlaylistFetcher::fetchMediaPlaylist);

            CompletableFuture<List<CompletableFuture<File>>> fileListFuture = ffmpegPolicy.isFfmpegEnabled()
                    ? mediaPlaylist
                    .thenApply(media -> ffmpegDownloader.download(media, vodId))
                    : mediaPlaylist
                    .thenApply(media -> manualDownloader.download(media, vodId));

            fileListFuture
                    .thenAccept(downloadFutures::addAll);
        }

        CompletableFuture<?>[] futures = downloadFutures.toArray(new CompletableFuture[downloadFutures.size()]);
        CompletableFuture.allOf(futures)
                .whenComplete((any, ex) -> HttpClients.close(okHttpClient))
                .whenComplete((any, ex) -> close(commandLineRunner));
    }

    private static void close(CommandLineRunner commandLineRunner) {
        try {
            commandLineRunner.close();
        } catch (Exception e) {
            throw new RuntimeException("Unexpected failure while closing CommandLineRunner", e);
        }
    }

}
