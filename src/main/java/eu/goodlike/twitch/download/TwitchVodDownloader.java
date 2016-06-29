package eu.goodlike.twitch.download;

import eu.goodlike.twitch.download.configurations.Policies;
import eu.goodlike.twitch.download.configurations.options.CommandLineParser;
import eu.goodlike.twitch.download.configurations.options.OptionsParser;
import eu.goodlike.twitch.download.configurations.options.OptionsProvider;
import eu.goodlike.twitch.download.configurations.settings.SettingsParser;
import eu.goodlike.twitch.download.configurations.settings.SettingsProvider;

import static eu.goodlike.twitch.download.configurations.settings.DefaultSettings.DEFAULT_PROPERTIES_FILE_PATH;

public final class TwitchVodDownloader {

    public static void main(String... args) {
        SettingsProvider settingsProvider = SettingsParser.fromFile(DEFAULT_PROPERTIES_FILE_PATH);
        CommandLineParser commandLineParser = CommandLineParser.newInstance(settingsProvider);
        OptionsParser.from(commandLineParser, args)
                .ifPresent(optionsProvider -> launchApplication(settingsProvider, optionsProvider));


        /*VideoIdParser parser = VideoIdParser.parse(args);
        if (parser == null) {
            System.out.println("Please enter at least one VoD id/link (use -help to see allowed formats)");
            return;
        }

        Set<Integer> params = parser.getVodIds();
        if (params.isEmpty()) {
            System.out.println("Could not parse ANY VoD id/links (use -help to see allowed formats)");
            return;
        }

        OkHttpClient client = new OkHttpClient();
        CommandLineRunner commandLineRunner = new CommandLineRunner();
        TokenFetcher tokenFetcher = new TokenFetcher(client);
        PlaylistFetcher playlistFetcher = new PlaylistFetcher(client);
        StreamDataFetcher streamDataFetcher = new StreamDataFetcher(client);

        List<CompletableFuture<?>> listOfFutures = new ArrayList<>();
        List<CompletableFuture<File>> cleanupFutures = new ArrayList<>();

        for (int vodId : params) {
            CompletableFuture<File> fileFuture = tokenFetcher.generateNewToken(vodId)
                    .thenCompose(token -> playlistFetcher.fetchStreamPlaylist(token, vodId));
            cleanupFutures.add(fileFuture);

            CompletableFuture<?> future = streamDataFetcher.fetchStreamDataForVodId(vodId)
                    .thenApply(StreamData::getFullFileName)
                    .thenCombine(fileFuture, (filename, file) -> ffmpegExecution(commandLineRunner, file.getName(), filename))
                    .thenAccept(TwitchVodDownloader::safelyWaitForProcessToComplete)
                    .whenComplete((any, ex) -> {if (ex != null) ex.printStackTrace();});
            listOfFutures.add(future);
        }
        CompletableFuture<?>[] futures = listOfFutures.toArray(new CompletableFuture[listOfFutures.size()]);
        CompletableFuture.allOf(futures)
                .whenComplete((any, ex) -> cleanup(client, commandLineRunner, cleanupFutures));*/
    }

    // PRIVATE

    private TwitchVodDownloader() {
        throw new AssertionError("Do not instantiate this class, it is only used for 'main' method!");
    }

    private static void launchApplication(SettingsProvider settingsProvider, OptionsProvider optionsProvider) {
        Policies policies = Policies.from(settingsProvider, optionsProvider);

    }


























    /*private static Process ffmpegExecution(CommandLineRunner commandLineRunner, String playlistFilename, String outputFilename) {
        return commandLineRunner.execute("ffmpeg",
                "-i", inQuotes(playlistFilename),
                "-bsf:a", "aac_adtstoasc",
                "-c", "copy", inQuotes(FileUtils.findAvailableName(outputFilename + ".mp4")));
    }

    private static String inQuotes(String string) {
        return "\"" + string + "\"";
    }

    private static void safelyWaitForProcessToComplete(Process process) {
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            throw new RuntimeException("Process interrupted!", e);
        }
    }

    private static void cleanup(OkHttpClient client, CommandLineRunner commandLineRunner,
                                List<CompletableFuture<File>> leftoverFiles) {
        client.dispatcher().executorService().shutdown();
        try {
            commandLineRunner.close();
        } catch (Exception e) {
            throw new RuntimeException("Closing command line runner interrupted!", e);
        }
        leftoverFiles.forEach(future -> future.thenAccept(File::delete));
    }*/

}
