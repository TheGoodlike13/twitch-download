package eu.goodlike;

import eu.goodlike.cmd.CommandLineRunner;
import eu.goodlike.twitch.playlist.PlaylistFetcher;
import eu.goodlike.twitch.stream.StreamData;
import eu.goodlike.twitch.stream.StreamDataFetcher;
import eu.goodlike.twitch.token.TokenFetcher;
import eu.goodlike.utils.FileUtils;
import okhttp3.OkHttpClient;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public final class TwitchVodDownloader {

    public static void main(String... args) {
        VideoIdParser parser = VideoIdParser.parse(args);
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
                .whenComplete((any, ex) -> cleanup(client, commandLineRunner, cleanupFutures));
    }

    // PRIVATE

    private TwitchVodDownloader() {
        throw new AssertionError("Do not instantiate this class, it is only used for 'main' method!");
    }

    private static Process ffmpegExecution(CommandLineRunner commandLineRunner, String playlistFilename, String outputFilename) {
        return commandLineRunner.execute("ffmpeg",
                "-i", inQuotes(playlistFilename),
                "-bsf:a", "aac_adtstoasc",
                "-c", "copy", inQuotes(FileUtils.findNonTakenName(outputFilename + ".mp4")));
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
    }

}
