package eu.goodlike.twitch.download.http.filename;

import eu.goodlike.io.log.CustomizedLogger;
import eu.goodlike.neat.Null;
import eu.goodlike.str.format.Traversable;
import eu.goodlike.str.format.TraversableFormatter;
import eu.goodlike.twitch.download.configurations.policy.PlaylistPolicy;
import eu.goodlike.twitch.stream.StreamDataFetcher;

import java.util.Optional;

/**
 * Resolves the output filename from twitch stream data
 */
public final class FilenameResolver {

    public Optional<String> resolveOutputName(String outputFormat, int vodId) {
        Traversable lazyJsonTraversable = LazyJsonTraversable.from(() -> streamDataFetcher.fetchStreamDataForVodId(vodId), debugLogger);
        Traversable lazyJsonTraversableOverride = new LazyJsonTraversableOverride(playlistPolicy, lazyJsonTraversable);
        TraversableFormatter traversableFormatter = new TraversableFormatter(lazyJsonTraversable, lazyJsonTraversableOverride);
        return traversableFormatter.format(outputFormat);
    }

    // CONSTRUCTORS

    public FilenameResolver(StreamDataFetcher streamDataFetcher, CustomizedLogger debugLogger, PlaylistPolicy playlistPolicy) {
        Null.check(streamDataFetcher,debugLogger, playlistPolicy)
                .ifAny("Stream data fetcher, debug logger and playlist policy cannot be null");

        this.streamDataFetcher = streamDataFetcher;
        this.debugLogger = debugLogger;
        this.playlistPolicy = playlistPolicy;
    }

    // PRIVATE

    private final StreamDataFetcher streamDataFetcher;
    private final CustomizedLogger debugLogger;
    private final PlaylistPolicy playlistPolicy;

    private static final class LazyJsonTraversableOverride implements Traversable {
        @Override
        public Optional<String> getValueAt(String firstStep, String... otherSteps) {
            if (otherSteps.length == 0) {
                if (firstStep.equals(FPS_STEP))
                    return subTraversable.getValueAt(FPS_STEP, getQualityLevel());
                else if (firstStep.equals(RESOLUTIONS_STEP))
                    return subTraversable.getValueAt(RESOLUTIONS_STEP, getQualityLevel());
            }

            return Optional.empty();
        }

        // CONSTRUCTORS

        private LazyJsonTraversableOverride(PlaylistPolicy playlistPolicy, Traversable subTraversable) {
            this.playlistPolicy = playlistPolicy;
            this.subTraversable = subTraversable;
        }

        // PRIVATE

        private final PlaylistPolicy playlistPolicy;
        private final Traversable subTraversable;

        private String getQualityLevel() {
            String quality = playlistPolicy.getQualityLevel();
            return SOURCE_QUALITY.equals(quality)
                    ? SOURCE_QUALITY_REPLACEMENT
                    : quality;
        }

        private static final String FPS_STEP = "fps";
        private static final String RESOLUTIONS_STEP = "resolutions";

        private static final String SOURCE_QUALITY = "source";
        private static final String SOURCE_QUALITY_REPLACEMENT = "chunked";
    }

}
