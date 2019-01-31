package com.harmony.umbrella.security.configurers;

import com.harmony.umbrella.security.SecurityTokenExtractor;
import com.harmony.umbrella.security.authentication.web.SecurityTokenAuthenticationFilter;
import com.harmony.umbrella.security.support.SecurityTokenExtractors;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wuxii
 */
public class SecurityTokenAuthenticationConfigurer<H extends HttpSecurityBuilder<H>> extends
        AbstractTokenAuthenticationFilterConfigurer<H, SecurityTokenAuthenticationConfigurer<H>, SecurityTokenAuthenticationFilter> {

    private final List<SecurityTokenExtractor> extractors = new ArrayList<>();

    public SecurityTokenAuthenticationConfigurer<H> setSecurityTokenExtractor(SecurityTokenExtractor securityTokenExtractor) {
        extractors.clear();
        extractors.add(securityTokenExtractor);
        return this;
    }

    public SecurityTokenAuthenticationConfigurer<H> addSecurityTokenExtractor(SecurityTokenExtractor securityTokenExtractor) {
        extractors.add(securityTokenExtractor);
        return this;
    }

    @Override
    public void init(H builder) throws Exception {
        this.setAuthenticationFilter(new SecurityTokenAuthenticationFilter());
        this.setFilterAfter(UsernamePasswordAuthenticationFilter.class);
        SecurityTokenAuthenticationFilter authenticationFilter = getAuthenticationFilter();
        authenticationFilter.setSecurityTokenExtractor(new SecurityTokenExtractors(extractors));
    }

}
