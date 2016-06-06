package eu.goodlike;

import com.google.common.primitives.Ints;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class VideoIdParser {

    public Set<Integer> getVodIds() {
        return vodIds;
    }

    // CONSTRUCTORS

    public static VideoIdParser parse(String... args) {
        if (args == null || args.length < 1)
            return null;

        List<String> allParameters = new ArrayList<>();
        for (int i = 0; i < args.length; i++) {
            String nextArg = args[i];
            if ("-help".equals(nextArg)) {
                if (i != 0 || args.length > 1)
                    System.out.println("Using -help cancels ALL other options");

                printHelpDialogue();
                return null;
            } else if ("-f".equals(nextArg)) {
                if (++i >= args.length)
                    System.out.println("Dangling file option '-f' has been ignored.");
                else {
                    String filename = args[i];
                    Path path = Paths.get(filename);
                    if (!Files.exists(path))
                        System.out.println("File not found: " + filename);
                    else if (!Files.isReadable(path))
                        System.out.println("File cannot be read: " + filename);
                    else {
                        List<String> fileLines;
                        try {
                            fileLines = Files.readAllLines(path, StandardCharsets.UTF_8);
                        } catch (IOException e) {
                            throw new RuntimeException("File could not be read: " + filename);
                        }
                        allParameters.addAll(fileLines);
                    }
                }
            } else
                allParameters.add(nextArg);
        }

        Set<Integer> vodIds = new HashSet<>();
        for (String param : allParameters) {
            Integer vodId = parseId(param);
            if (vodId == null)
                System.out.println("Rejected arg because cannot parse VoD id from it: " + param);
            else if (!vodIds.add(vodId)) {
                System.out.println("Duplicate VoD id skipped: " + vodId);
            }
        }
        return new VideoIdParser(vodIds);
    }

    private VideoIdParser(Set<Integer> vodIds) {
        this.vodIds = vodIds;
    }

    // PRIVATE

    private final Set<Integer> vodIds;

    private static void printHelpDialogue() {
        System.out.println();
        System.out.println("Usage: twitchVoD vodId [vodId ...]");
        System.out.println();
        System.out.println("You can specify a vodId in 3 different ways:");
        System.out.println("1) as a number, i.e.: 123456");
        System.out.println("2) as a link, i.e. https://www.twitch.tv/some_guy/v/123456");
        System.out.println("3) as a video id, i.e.: v123456");
        System.out.println();
        System.out.println("You can also specify text files containing lines of vodIds, live above");
        System.out.println("Simply use '-f filename' instead of a vodId");
        System.out.println("All vodIds will be used, including the ones in the files and parameters");
        System.out.println();
        System.out.println("The files will be downloaded into the working directory");
        System.out.println("Once downloaded, the file names will have the following form:");
        System.out.println("(time_of_recording) streamer stream_title.mp4");
        System.out.println();
    }

    private static Integer parseId(String arg) {
        String formattedArg = arg.startsWith("http")
                ? arg.substring(arg.lastIndexOf("/") + 1)
                : arg.startsWith("v") ? arg.substring(1) : arg;

        return Ints.tryParse(formattedArg);
    }

}
