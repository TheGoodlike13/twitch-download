package eu.goodlike.twitch.download.configurations;

import eu.goodlike.neat.Null;
import eu.goodlike.twitch.download.configurations.options.OptionsProvider;
import eu.goodlike.twitch.download.configurations.policy.*;
import eu.goodlike.twitch.download.configurations.settings.SettingsProvider;

/**
 * Holds all policies for this application, which in turn contain all the configurations
 */
public final class Policies {

    public ConcurrencyPolicy getConcurrencyPolicy() {
        return concurrencyPolicy;
    }

    public FfmpegPolicy getFfmpegPolicy() {
        return ffmpegPolicy;
    }

    public HttpRequestPolicy getHttpRequestPolicy() {
        return httpRequestPolicy;
    }

    public InputPolicy getInputPolicy() {
        return inputPolicy;
    }

    public LogPolicy getLogPolicy() {
        return logPolicy;
    }

    public OutputPolicy getOutputPolicy() {
        return outputPolicy;
    }

    public PlaylistPolicy getPlaylistPolicy() {
        return playlistPolicy;
    }

    // CONSTRUCTORS

    public static Policies from(SettingsProvider settingsProvider, OptionsProvider optionsProvider) {
        LogPolicy logPolicy = LogPolicy.from(optionsProvider);
        return new Policies(
                ConcurrencyPolicy.from(optionsProvider),
                FfmpegPolicy.from(settingsProvider, optionsProvider),
                HttpRequestPolicy.from(settingsProvider),
                InputPolicy.from(optionsProvider, logPolicy),
                logPolicy,
                OutputPolicy.from(settingsProvider, optionsProvider),
                PlaylistPolicy.from(optionsProvider)
        );
    }

    public Policies(ConcurrencyPolicy concurrencyPolicy, FfmpegPolicy ffmpegPolicy, HttpRequestPolicy httpRequestPolicy,
                    InputPolicy inputPolicy, LogPolicy logPolicy, OutputPolicy outputPolicy, PlaylistPolicy playlistPolicy) {

        Null.check(concurrencyPolicy, ffmpegPolicy, httpRequestPolicy, inputPolicy, logPolicy, outputPolicy, playlistPolicy)
                .ifAny("Policies cannot be null");

        this.concurrencyPolicy = concurrencyPolicy;
        this.ffmpegPolicy = ffmpegPolicy;
        this.httpRequestPolicy = httpRequestPolicy;
        this.inputPolicy = inputPolicy;
        this.logPolicy = logPolicy;
        this.outputPolicy = outputPolicy;
        this.playlistPolicy = playlistPolicy;
    }

    // PRIVATE

    private final ConcurrencyPolicy concurrencyPolicy;
    private final FfmpegPolicy ffmpegPolicy;
    private final HttpRequestPolicy httpRequestPolicy;
    private final InputPolicy inputPolicy;
    private final LogPolicy logPolicy;
    private final OutputPolicy outputPolicy;
    private final PlaylistPolicy playlistPolicy;

}
