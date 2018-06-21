package com.harmony.umbrella.autoconfigure.web;

import java.util.Set;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "harmony.web")
public class WebProperties {

    private String ipHeader;
    private CurrentFilter currentFilter;
    private boolean bundle;

    public WebProperties() {
    }

    public String getIpHeader() {
        return ipHeader;
    }

    public void setIpHeader(String ipHeader) {
        this.ipHeader = ipHeader;
    }

    public CurrentFilter getCurrentFilter() {
        return currentFilter;
    }

    public void setCurrentFilter(CurrentFilter currentFilter) {
        this.currentFilter = currentFilter;
    }

    public boolean isBundle() {
        return bundle;
    }

    public void setBundle(boolean bundle) {
        this.bundle = bundle;
    }

    public static class CurrentFilter {

        private boolean enabled;
        private Set<String> urlPatterns;
        private Set<String> excludes;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public Set<String> getUrlPatterns() {
            return urlPatterns;
        }

        public void setUrlPatterns(Set<String> urlPatterns) {
            this.urlPatterns = urlPatterns;
        }

        public Set<String> getExcludes() {
            return excludes;
        }

        public void setExcludes(Set<String> excludes) {
            this.excludes = excludes;
        }

    }

}