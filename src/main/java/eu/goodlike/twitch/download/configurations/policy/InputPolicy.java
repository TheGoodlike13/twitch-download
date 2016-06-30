package eu.goodlike.twitch.download.configurations.policy;

import com.google.common.collect.ImmutableSet;
import eu.goodlike.neat.Null;
import eu.goodlike.twitch.download.configurations.VodParser;
import eu.goodlike.twitch.download.configurations.options.OptionsProvider;

import java.util.Set;

/**
 * Defines configurations for input
 */
public final class InputPolicy {

    /**
     * @return list of vod ids to download
     */
    public Set<Integer> getVodIds() {
        return vodIds;
    }

    // CONSTRUCTORS

    public static InputPolicy from(OptionsProvider optionsProvider, LogPolicy logPolicy) {
        return from(VodParser.from(optionsProvider, logPolicy));
    }

    public static InputPolicy from(VodParser vodParser) {
        Null.check(vodParser).ifAny("VoD parser cannot be null");
        return new InputPolicy(vodParser.getVodIds());
    }

    public InputPolicy(Set<Integer> vodIds) {
        Null.checkCollection(vodIds).ifAny("Vod id list cannot be null");

        this.vodIds = ImmutableSet.copyOf(vodIds);
    }

    // PRIVATE

    private final Set<Integer> vodIds;

}
