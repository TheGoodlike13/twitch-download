package eu.goodlike.twitch.download.configurations.options;

import eu.goodlike.misc.SpecialUtils;

public final class DefaultOptions {

    public static final int DEFAULT_MINIMUM_CORES_ACCEPTED_VALUE = 4;
    public static final int DEFAULT_MAX_NUMBER_OF_THREADS =
            SpecialUtils.getCoreCountWithMin(DEFAULT_MINIMUM_CORES_ACCEPTED_VALUE);

    public static final String DEFAULT_QUALITY_LEVEL = "source";
    public static final String DEFAULT_QUALITY_LEVEL_FALLBACK = "1080p";

    // PRIVATE

    private DefaultOptions() {
        throw new AssertionError("Do not instantiate, use static methods!");
    }

}
