package eu.goodlike.twitch.m3u8.master;

import com.google.common.collect.ImmutableMap;
import eu.goodlike.neat.Null;

import java.util.Map;
import java.util.Optional;

/**
 * Contains stream sources, accessible by name
 */
public final class MasterPlaylist {

    /**
     * @return playlist source url for given quality, Optional::empty if such quality was not found
     */
    public Optional<String> getStreamPlaylistUrlForQuality(String qualityName) {
        return Optional.ofNullable(streamSources.get(qualityName));
    }

    // CONSTRUCTORS

    public MasterPlaylist(Map<String, String> streamSources) {
        Null.check(streamSources).ifAny("Stream sources cannot be null");
        this.streamSources = ImmutableMap.copyOf(streamSources);
    }

    // PRIVATE

    private final Map<String, String> streamSources;

}
