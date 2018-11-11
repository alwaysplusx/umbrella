package com.harmony.umbrella.autoconfigure.team;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author wuxii
 */
@Configuration
//@ConditionalOnResource(resources = "${harmony.team.git.location:/META-INF/git.properties}")
@EnableConfigurationProperties(GitVersionProperties.class)
public class GitVersionAutoConfiguration {

    private final GitVersionProperties gitVersionProperties;

    public GitVersionAutoConfiguration(GitVersionProperties gitVersionProperties) {
        this.gitVersionProperties = gitVersionProperties;
    }

    @ConditionalOnMissingBean
    VersionController gitVersionController() {
        System.err.println(gitVersionProperties);
        return null;
    }

//    @Controller
//    @RequestMapping("${meow.git.version.path:/version}")
//    public static class GitVersionController implements VersionController {
//
//        private static final Logger logger = LoggerFactory.getLogger(GitVersionController.class);
//
//        private Properties gitProperties;
//
//        private String location;
//
//        public GitVersionController(String location) {
//            this.location = location;
//        }
//
//        @Override
//        public Properties getVersionProperties() {
//            if (gitProperties == null) {
//                this.gitProperties = loadGitProperties();
//            }
//            return gitProperties;
//        }
//
//        private Properties loadGitProperties() {
//            Properties properties = new Properties();
//            ClassLoader loader = Thread.currentThread().getContextClassLoader();
//            try (InputStream is = loader.getResourceAsStream(location)) {
//                if (is != null) {
//                    properties.load(is);
//                }
//            } catch (IOException e) {
//                logger.warn("no git properties found");
//            }
//            return properties;
//        }
//    }

}
