package com.harmony.umbrella.autoconfigure.team;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author wuxii
 */
@ConfigurationProperties(prefix = "harmony.team.git")
public class GitVersionProperties {

    private String path = "/git/version";
    private String location = "META-INF/git.properties";

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
