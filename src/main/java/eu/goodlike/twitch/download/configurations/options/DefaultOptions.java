package eu.goodlike.twitch.download.configurations.options;

import com.google.common.collect.ImmutableList;
import eu.goodlike.misc.SpecialUtils;

import java.util.List;

public final class DefaultOptions {

    public static final int DEFAULT_MINIMUM_CORES_ACCEPTED_VALUE = 4;
    public static final int DEFAULT_MAX_NUMBER_OF_THREADS =
            SpecialUtils.getCoreCountWithMin(DEFAULT_MINIMUM_CORES_ACCEPTED_VALUE);

    public static final List<String> POSSIBLE_QUALITY_LEVELS = ImmutableList.of(
            "audio_only", "mobile", "low", "medium", "high", "source");

    public static final String DEFAULT_QUALITY_LEVEL = "source";

    // PRIVATE

    private DefaultOptions() {
        throw new AssertionError("Do not instantiate, use static methods!");
    }

}
