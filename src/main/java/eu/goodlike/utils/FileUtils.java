package eu.goodlike.utils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class FileUtils {

    public static String findNonTakenName(String possiblyTakenName) {
        String justFilename = com.google.common.io.Files.getNameWithoutExtension(possiblyTakenName);
        String extension = com.google.common.io.Files.getFileExtension(possiblyTakenName);
        String workingFilename = possiblyTakenName;
        int copyCount = 0;
        Path path = Paths.get(workingFilename);
        while (Files.exists(path)) {
            workingFilename = justFilename + " (" + ++copyCount + ")";
            if (!extension.isEmpty())
                workingFilename += "." + extension;
            path = Paths.get(workingFilename);
        }
        return workingFilename;
    }

    // PRIVATE

    private FileUtils() {
        throw new AssertionError("Do not instantiate, use static methods!");
    }

}
