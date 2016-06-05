package eu.goodlike;

import eu.goodlike.cmd.CommandLineRunner;
import eu.goodlike.twitch.token.TokenFetcher;
import okhttp3.OkHttpClient;

public final class TwitchVodDownloader {

    public static void main(String... args) throws Exception {
        if (args.length < 1) {
            zeroArgsFallback();
            return;
        }

        OkHttpClient client = new OkHttpClient();
        TokenFetcher tokenFetcher = new TokenFetcher(client);
        try (CommandLineRunner commandLineRunner = new CommandLineRunner()) {
            for (String vodId : args) {
                tokenFetcher.generateNewToken(vodId).thenAccept(System.out::println);
                commandLineRunner.execute("ping", "localhost");
            }
        }
        client.dispatcher().executorService().shutdown();
    }

    // PRIVATE

    private TwitchVodDownloader() {
        throw new AssertionError("Do not instantiate this class, it is only used for 'main' method!");
    }

    private static void zeroArgsFallback() {
        System.out.println("Please add at least one vodId for download");
    }

}
