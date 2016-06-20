package eu.goodlike.twitch.m3u8;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * NOT INTENDED FOR GENERAL USE! TWITCH FILES ONLY!
 */
public final class CustomM3U8Parser implements AutoCloseable {

    public String getStreamSourcePlaylistUrl() {
        String flag = scanner.findWithinHorizon(SOURCE_FLAG, 0);
        if (flag == null)
            throw new IllegalStateException("Cannot find source VoD");

        scanner.nextLine();
        return scanner.nextLine();
    }

    public List<String> getPlaylistPrefix() {
        List<String> prefix = new ArrayList<>();
        while (!scanner.hasNext(PART_PATTERN))
            prefix.add(scanner.nextLine());

        return prefix;
    }

    public List<StreamPart> getPlaylistParts() {
        List<StreamPart> streamParts = new ArrayList<>();
        String lastFileName = null;
        int lastStartOffset = 0;
        int lastEndOffset = 0;
        int lastDuration = 0;
        while (scanner.hasNext() && scanner.hasNext(PART_PATTERN)) {
            int thousands = scanner.skip(PART_FLAG).useDelimiter("\\.").nextInt();
            int subThousands = scanner.useDelimiter(",").skip("\\.").nextInt();
            int duration = thousands * 1000 + subThousands;

            scanner.nextLine();
            String filename = scanner.useDelimiter("\\?").next();

            int startOffset = scanner.useDelimiter("&").skip("\\?start_offset=").nextInt();
            int endOffset = scanner.reset().skip("\\&end_offset=").nextInt();
            scanner.nextLine();

            if (lastFileName == null) {
                lastFileName = filename;
                lastStartOffset = startOffset;
                lastEndOffset = endOffset;
                lastDuration = duration;
            } else if (lastFileName.equals(filename)) {
                if (startOffset - lastEndOffset != 1)
                    System.out.println("Potential frame loss detected and adjusted for");
                lastEndOffset = endOffset;
                lastDuration += duration;
            } else {
                StreamPart streamPart = new StreamPart(lastFileName, lastStartOffset, lastEndOffset, lastDuration);
                streamParts.add(streamPart);
                lastFileName = filename;
                lastStartOffset = startOffset;
                lastEndOffset = endOffset;
                lastDuration = duration;
            }
        }
        if (lastFileName != null) {
            StreamPart streamPart = new StreamPart(lastFileName, lastStartOffset, lastEndOffset, lastDuration);
            streamParts.add(streamPart);
        }
        return streamParts;
    }

    public List<String> getPlaylistSuffix() {
        return Arrays.asList(END_FLAG, "", "");
    }

    @Override
    public void close() throws Exception {
        scanner.close();
    }

    // CONSTRUCTORS

    public CustomM3U8Parser(InputStream inputStream) {
        this.scanner = new Scanner(inputStream);
    }

    // PRIVATE

    private final Scanner scanner;

    private static final String SOURCE_FLAG = "VIDEO=\"chunked\"";
    private static final String PART_FLAG = "#EXTINF:";
    private static final Pattern PART_PATTERN = Pattern.compile(PART_FLAG + ".*");
    private static final String END_FLAG = "#EXT-X-ENDLIST";

}
