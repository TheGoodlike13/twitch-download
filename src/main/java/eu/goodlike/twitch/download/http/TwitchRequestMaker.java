package eu.goodlike.twitch.download.http;

import com.fasterxml.jackson.core.type.TypeReference;
import eu.goodlike.libraries.okhttp.JacksonCallback;
import eu.goodlike.neat.Null;
import eu.goodlike.twitch.download.configurations.policy.HttpRequestPolicy;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.util.concurrent.CompletableFuture;

import static eu.goodlike.twitch.TwitchDefaults.*;

/**
 * Makes HTTP request to twitch
 */
public final class TwitchRequestMaker {

    /**
     * @return result of parsing JSON from given httpUrl as given class
     * @throws NullPointerException if httpUrl or clazz is null
     */
    public <T> CompletableFuture<T> makeRequest(HttpUrl httpUrl, Class<T> clazz) {
        Null.check(httpUrl, clazz).ifAny("Http url and class cannot be null");
        return JacksonCallback.asFuture(client.newCall(buildRequest(httpUrl)), clazz);
    }

    /**
     * @return result of parsing JSON from given httpUrl as given type
     * @throws NullPointerException if httpUrl or type is null
     */
    public <T> CompletableFuture<T> makeRequest(HttpUrl httpUrl, TypeReference<T> type) {
        Null.check(httpUrl, type).ifAny("Http url and type cannot be null");
        return JacksonCallback.asFuture(client.newCall(buildRequest(httpUrl)), type);
    }

    // CONSTRUCTORS

    public TwitchRequestMaker(HttpRequestPolicy httpRequestPolicy, OkHttpClient client) {
        Null.check(httpRequestPolicy, client).ifAny("Http request policy and http client cannot be null");

        this.httpRequestPolicy = httpRequestPolicy;
        this.client = client;
    }

    // PRIVATE

    private final HttpRequestPolicy httpRequestPolicy;
    private final OkHttpClient client;

    private Request buildRequest(HttpUrl httpUrl) {
        Request.Builder builder = new Request.Builder()
                .url(httpUrl)
                .addHeader(VERSION_3_HEADER_NAME, VERSION_3_HEADER_VALUE)
                .addHeader(CLIENT_ID_HEADER_NAME, httpRequestPolicy.getClientId());

        httpRequestPolicy.getOauth()
                .ifPresent(oauth -> builder.addHeader(OAUTH_HEADER_NAME, OAUTH_HEADER_VALUE_PREFIX + oauth));

        return builder.build();
    }

}
