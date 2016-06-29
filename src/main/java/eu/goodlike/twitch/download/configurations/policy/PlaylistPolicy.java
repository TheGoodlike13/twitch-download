package eu.goodlike.twitch.download.configurations.policy;

import eu.goodlike.neat.Null;
import eu.goodlike.twitch.download.configurations.options.OptionsProvider;

/**
 * Defines configurations for playlist management
 */
public final class PlaylistPolicy {

    /**
     * @return true if quality should default to source if missing, false if it should be skipped instead
     */
    public boolean isDefaultToSourceEnabled() {
        return defaultToSourceEnabled;
    }

    /**
     * @return true if playlist optimization is enabled, false otherwise
     */
    public boolean isCombinePlaylistPartsEnabled() {
        return combinePlaylistPartsEnabled;
    }

    /**
     * @return true if playlist should be deleted afterwards, false otherwise
     */
    public boolean isCleanupPlaylistEnabled() {
        return cleanupPlaylistEnabled;
    }

    /**
     * @return quality level that should be downloaded
     */
    public String getQualityLevel() {
        return qualityLevel;
    }

    // CONSTRUCTORS

    public static PlaylistPolicy from(OptionsProvider optionsProvider) {
        Null.check(optionsProvider).ifAny("Options provider cannot be null");
        return new PlaylistPolicy(
                optionsProvider.isDefaultToSourceEnabled(),
                optionsProvider.isPlaylistOptimizationEnabled(),
                optionsProvider.isPlaylistCleanEnabled(),
                optionsProvider.getQualityLevel()
        );
    }

    public PlaylistPolicy(boolean defaultToSourceEnabled, boolean combinePlaylistPartsEnabled,
                          boolean cleanupPlaylistEnabled, String qualityLevel) {
        Null.check(qualityLevel).ifAny("Quality level cannot be null");

        this.defaultToSourceEnabled = defaultToSourceEnabled;
        this.combinePlaylistPartsEnabled = combinePlaylistPartsEnabled;
        this.cleanupPlaylistEnabled = cleanupPlaylistEnabled;
        this.qualityLevel = qualityLevel;
    }

    // PRIVATE

    private final boolean defaultToSourceEnabled;
    private final boolean combinePlaylistPartsEnabled;
    private final boolean cleanupPlaylistEnabled;
    private final String qualityLevel;

}
