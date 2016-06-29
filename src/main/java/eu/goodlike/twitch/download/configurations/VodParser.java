package eu.goodlike.twitch.download.configurations;

import eu.goodlike.functional.Either;
import eu.goodlike.io.FileUtils;
import eu.goodlike.io.log.CustomizedLogger;
import eu.goodlike.neat.Null;
import eu.goodlike.twitch.download.configurations.options.OptionsProvider;
import eu.goodlike.twitch.download.configurations.policy.LogPolicy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Parses actual VoD ids from given VoD ids; the given ids can be http links, files to ids, or actual ids (with or
 * without 'v' prefix
 */
public final class VodParser {

    /**
     * @return set of actual VoD ids that are result of parsing all VoD ids given to the parser
     */
    public Set<String> getVodIds() {
        return getVodIds(new LinkedHashSet<>(), new HashSet<>());
    }

    // CONSTRUCTORS

    public static VodParser from(OptionsProvider optionsProvider, LogPolicy logPolicy) {
        Null.check(optionsProvider, logPolicy).ifAny("Options provider and log policy cannot be null");
        return new VodParser(optionsProvider.getVodIds(), logPolicy.getDebugLogger());
    }

    public VodParser(List<String> vodIds, CustomizedLogger debugLogger) {
        Null.checkList(vodIds).ifAny("VoD ids cannot be or contain null");
        Null.check(debugLogger).ifAny("Debug logger cannot be null");

        this.vodIds = vodIds;
        this.debugLogger = debugLogger;
    }

    // PRIVATE

    private final List<String> vodIds;
    private final CustomizedLogger debugLogger;

    private Set<String> getVodIds(Set<String> parsedIds, Set<Path> visitedPaths) {
        for (String vodId : vodIds)
            parseVodId(vodId)
                    .ifFirstKind(parsedIds::add)
                    .filterSecondKind(o -> !visitedPaths.contains(o))
                    .ifSecondKind(visitedPaths::add)
                    .mapSecondKind(this::parseFile)
                    .mapSecondKind(ids -> new VodParser(ids, debugLogger))
                    .mapSecondKind(parser -> parser.getVodIds(parsedIds, visitedPaths))
                    .ifSecondKind(parsedIds::addAll);

        return parsedIds;
    }

    private List<String> parseFile(Path path) {
        try {
            return Files.readAllLines(path);
        } catch (IOException e) {
            debugLogger.logMessage("Couldn't read file at: " + path);
        }
        return Collections.emptyList();
    }

    private static Either<String, Path> parseVodId(String vodId) {
        if (vodId.startsWith("http")) {
            int idStartIndex = vodId.lastIndexOf('/') + 1;
            return Either.<String, Path>ofFirstKind(vodId.substring(idStartIndex))
                    .filterFirstKind(id -> !id.isEmpty());
        }

        Optional<Path> path = FileUtils.getPath(vodId)
                .filter(Files::isReadable);

        if (path.isPresent())
            return Either.ofSecondKind(path);

        if (vodId.startsWith("v"))
            vodId = vodId.substring(1);

        return Either.ofFirstKind(vodId);
    }

}
