package eu.goodlike;

import com.google.common.io.Files;
import com.google.common.primitives.Ints;
import eu.goodlike.cmd.CommandLineRunner;
import eu.goodlike.twitch.playlist.PlaylistFetcher;
import eu.goodlike.twitch.token.TokenFetcher;
import okhttp3.OkHttpClient;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class TwitchVodDownloader {

    public static void main(String... args) {
        if (args.length < 1) {
            zeroArgsFallback();
            return;
        }

        List<Integer> params = validateParams(args);

        OkHttpClient client = new OkHttpClient();
        CommandLineRunner commandLineRunner = new CommandLineRunner();
        TokenFetcher tokenFetcher = new TokenFetcher(client);
        PlaylistFetcher playlistFetcher = new PlaylistFetcher(client);

        List<CompletableFuture<?>> listOfFutures = new ArrayList<>();
        List<CompletableFuture<File>> cleanupFutures = new ArrayList<>();

        for (int vodId : params) {
            CompletableFuture<File> fileFuture = tokenFetcher.generateNewToken(vodId)
                    .thenCompose(token -> playlistFetcher.fetchStreamPlaylist(token, vodId));
            cleanupFutures.add(fileFuture);

            CompletableFuture<?> future = fileFuture
                    .thenApply(File::getName)
                    .thenApply(filename -> ffmpegExecution(commandLineRunner, filename))
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

    private static void zeroArgsFallback() {
        System.out.println("Please add at least one vodId for download");
    }

    private static List<Integer> validateParams(String... args) {
        List<Integer> params = new ArrayList<>();
        for (String arg : args) {
            String formattedArg = arg.startsWith("http")
                    ? arg.substring(arg.lastIndexOf("/") + 1)
                    : arg;

            Integer vodId = Ints.tryParse(formattedArg);
            if (vodId == null)
                System.out.println("Reject arg because cannot parse VoD id from it: " + arg);
            else
                params.add(vodId);
        }
        return params;
    }

    private static Process ffmpegExecution(CommandLineRunner commandLineRunner, String filename) {
        return commandLineRunner.execute("ffmpeg",
                "-i", filename,
                "-bsf:a", "aac_adtstoasc",
                "-c", "copy", Files.getNameWithoutExtension(filename) + ".mp4");
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
