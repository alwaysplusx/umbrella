package com.harmony.umbrella.autoconfigure.team;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author wuxii
 */
@Controller
@RequestMapping("${harmony.team.git.path:/git/version}")
public class GitVersionController implements VersionController {

    private static final Logger logger = LoggerFactory.getLogger(GitVersionController.class);

    private final String path;
    private final String location;

    private Properties gitProperties;

    public GitVersionController(GitVersionProperties gitVersionProperties) {
        this.path = gitVersionProperties.getPath();
        this.location = gitVersionProperties.getLocation();
    }

    @ResponseBody
    @RequestMapping
    public ResponseEntity<Properties> version() {
        return ResponseEntity.ok(getVersionProperties());
    }

    private Properties loadGitProperties() {
        Properties properties = new Properties();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try (InputStream is = loader.getResourceAsStream(location)) {
            if (is != null) {
                properties.load(is);
            }
        } catch (IOException e) {
            logger.warn("no git properties found");
        }
        return properties;
    }

    @Override
    public Properties getVersionProperties() {
        if (gitProperties == null) {
            this.gitProperties = loadGitProperties();
        }
        return gitProperties;
    }
}