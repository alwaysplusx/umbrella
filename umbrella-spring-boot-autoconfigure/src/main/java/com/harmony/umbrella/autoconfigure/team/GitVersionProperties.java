package com.harmony.umbrella.autoconfigure.team;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author wuxii
 */
@ConfigurationProperties(prefix = "harmony.team.git")
public class GitVersionProperties {

    private String urlPattern = "/git/version";
    private String location = "/META-INF/git.properties";

    public String getUrlPattern() {
        return urlPattern;
    }

    public void setUrlPattern(String urlPattern) {
        this.urlPattern = urlPattern;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}
