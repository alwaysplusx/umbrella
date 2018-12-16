package com.harmony.umbrella.autoconfigure.team;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.Servlet;

/**
 * @author wuxii
 */
@Configuration
@ConditionalOnWebApplication
@ConditionalOnClass({Servlet.class, DispatcherServlet.class})
@ConditionalOnResource(resources = "${harmony.team.git.location:/META-INF/git.properties}")
@AutoConfigureBefore(WebMvcAutoConfiguration.class)
@EnableConfigurationProperties(GitVersionProperties.class)
public class GitVersionAutoConfiguration {

    private final GitVersionProperties gitVersionProperties;

    public GitVersionAutoConfiguration(GitVersionProperties gitVersionProperties) {
        this.gitVersionProperties = gitVersionProperties;
    }

    @Bean
    @ConditionalOnMissingBean(VersionController.class)
    public VersionController versionController() {
        return new GitVersionController(gitVersionProperties);
    }

}
