package eu.goodlike.twitch.download.configurations.policy;

import eu.goodlike.neat.Null;
import eu.goodlike.twitch.download.configurations.options.OptionsProvider;

/**
 * Defines configurations for concurrent execution
 */
public final class ConcurrencyPolicy {

    /**
     * @return number of concurrent ffmpeg executions/downloads
     */
    public int getMaxConcurrentThreads() {
        return maxConcurrentThreads;
    }

    // CONSTRUCTORS

    public static ConcurrencyPolicy from(OptionsProvider optionsProvider) {
        Null.check(optionsProvider).ifAny("Options provider cannot be null");
        return new ConcurrencyPolicy(optionsProvider.getMaxConcurrentThreads());
    }

    public ConcurrencyPolicy(int maxConcurrentThreads) {
        if (maxConcurrentThreads < 1)
            throw new IllegalArgumentException("Thread count can only be positive, not: " + maxConcurrentThreads);

        this.maxConcurrentThreads = maxConcurrentThreads;
    }

    // PRIVATE

    private final int maxConcurrentThreads;

}
