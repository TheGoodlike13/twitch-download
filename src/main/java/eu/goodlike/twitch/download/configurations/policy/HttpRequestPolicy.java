package eu.goodlike.twitch.download.configurations.policy;

import eu.goodlike.neat.Null;
import eu.goodlike.twitch.download.configurations.settings.SettingsProvider;

import java.util.Optional;

/**
 * Defines configurations for certain http requests
 */
public final class HttpRequestPolicy {

    /**
     * @return clientId to use for Twitch requests
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * @return oauth token to use for Twitch requests, Optional::empty to send anonymous requests instead
     */
    public Optional<String> getOauth() {
        return oauth;
    }

    // CONSTRUCTORS

    public static HttpRequestPolicy from(SettingsProvider settingsProvider) {
        Null.check(settingsProvider).ifAny("Settings cannot be null");
        return new HttpRequestPolicy(settingsProvider.getClientIdSetting(), settingsProvider.getOauthSetting());
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public HttpRequestPolicy(String clientId, Optional<String> oauth) {
        Null.check(clientId, oauth).ifAny("Cliend id and oauth Optional cannot be null");

        this.clientId = clientId;
        this.oauth = oauth;
    }

    // PRIVATE

    private final String clientId;
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private final Optional<String> oauth;

}
