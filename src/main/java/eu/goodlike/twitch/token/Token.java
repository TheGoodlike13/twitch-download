package eu.goodlike.twitch.token;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class Token {

    public String getToken() {
        return token;
    }

    public String getSig() {
        return sig;
    }

    @JsonCreator
    public Token(@JsonProperty("token") String token,
                 @JsonProperty("sig") String sig) {
        this.token = token;
        this.sig = sig;
    }

    // PRIVATE

    private final String token;
    private final String sig;

    // OBJECT OVERRIDES

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("token", token)
                .add("sig", sig)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Token)) return false;
        Token token1 = (Token) o;
        return Objects.equals(token, token1.token) &&
                Objects.equals(sig, token1.sig);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, sig);
    }

}
