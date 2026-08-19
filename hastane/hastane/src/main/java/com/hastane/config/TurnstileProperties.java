package com.hastane.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.turnstile")
public class TurnstileProperties {

    private boolean enabled;
    private String secretKey;
    private String expectedHostname;
    private String siteverifyUrl =
            "https://challenges.cloudflare.com/turnstile/v0/siteverify";

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getExpectedHostname() {
        return expectedHostname;
    }

    public void setExpectedHostname(String expectedHostname) {
        this.expectedHostname = expectedHostname;
    }

    public String getSiteverifyUrl() {
        return siteverifyUrl;
    }

    public void setSiteverifyUrl(String siteverifyUrl) {
        this.siteverifyUrl = siteverifyUrl;
    }
}
