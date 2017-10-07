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

import static eu.goodlike.UsefulValidators.INTEGER_STRING_VALIDATOR;

/**
 * Parses actual VoD ids from given VoD ids; the given ids can be http links, files to ids, or actual ids (with or
 * without 'v' prefix
 */
public final class VodParser {

    /**
     * @return set of actual VoD ids that are result of parsing all VoD ids given to the parser
     */
    public Set<Integer> getVodIds() {
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

    private Set<Integer> getVodIds(Set<Integer> parsedIds, Set<Path> visitedPaths) {
        for (String vodId : vodIds)
            parseVodId(vodId)
                    .ifLeft(parsedIds::add)
                    .filterRight(visitedPaths::add)
                    .mapRight(this::parseFile)
                    .mapRight(ids -> new VodParser(ids, debugLogger))
                    .mapRight(parser -> parser.getVodIds(parsedIds, visitedPaths))
                    .ifRight(parsedIds::addAll);

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

    private Either<Integer, Path> parseVodId(String vodId) {
        if (vodId.startsWith("http")) {
            int idStartIndex = vodId.lastIndexOf('/') + 1;
            return parseIntegerId(vodId.substring(idStartIndex));
        }

        Optional<Path> path = FileUtils.getPath(vodId)
                .filter(Files::isReadable);

        if (path.isPresent())
            return Either.right(path);

        if (vodId.startsWith("v"))
            vodId = vodId.substring(1);

        return parseIntegerId(vodId);
    }

    private Either<Integer, Path> parseIntegerId(String vodId) {
        if (INTEGER_STRING_VALIDATOR.isValid(vodId)) {
            int id = Integer.parseInt(vodId);
            if (id > 0)
                return Either.left(id);
        }
        debugLogger.logMessage("Invalid VoD id: " + vodId);
        return Either.neither();
    }

}
