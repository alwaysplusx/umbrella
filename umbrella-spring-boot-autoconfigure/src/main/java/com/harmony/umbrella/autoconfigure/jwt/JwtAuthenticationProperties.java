package com.harmony.umbrella.autoconfigure.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * @author wuxii
 */
@ConfigurationProperties(prefix = "harmony.jwt")
public class JwtAuthenticationProperties {

    private String signature;
    private Duration expiresIn = Duration.ofSeconds(7200);
    private boolean strictMode;
    private String issuer;

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public Duration getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Duration expiresIn) {
        this.expiresIn = expiresIn;
    }

    public boolean isStrictMode() {
        return strictMode;
    }

    public void setStrictMode(boolean strictMode) {
        this.strictMode = strictMode;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

}
