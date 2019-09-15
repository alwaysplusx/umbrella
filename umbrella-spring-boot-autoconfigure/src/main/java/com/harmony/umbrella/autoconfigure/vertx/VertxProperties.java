package com.harmony.umbrella.autoconfigure.vertx;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.VertxOptions;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "harmony.vertx")
public class VertxProperties {

    private Options options;

    private boolean autoDeploy = true;

    private Map<String, DeployOptions> deployOptions = new HashMap<>();

    public Options getOptions() {
        return options;
    }

    public void setOptions(Options options) {
        this.options = options;
    }

    public boolean isAutoDeploy() {
        return autoDeploy;
    }

    public void setAutoDeploy(boolean autoDeploy) {
        this.autoDeploy = autoDeploy;
    }

    public Map<String, DeployOptions> getDeployOptions() {
        return deployOptions;
    }

    public void setDeployOptions(Map<String, DeployOptions> deployOptions) {
        this.deployOptions = deployOptions;
    }

    public static class DeployOptions extends DeploymentOptions {

    }

    public static class Options extends VertxOptions {

    }
}
