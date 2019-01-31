package com.harmony.umbrella.security.configurers;

import com.harmony.umbrella.security.JwtTokenDecoder;
import com.harmony.umbrella.security.JwtTokenExtractor;
import com.harmony.umbrella.security.authentication.web.JwtAuthenticationFilter;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * @author wuxii
 */
public class JwtAuthenticationConfigurer<H extends HttpSecurityBuilder<H>> extends
        AbstractTokenAuthenticationFilterConfigurer<H, JwtAuthenticationConfigurer<H>, JwtAuthenticationFilter> {

    private Collection<JwtTokenExtractor> jwtTokenExtractors = new HashSet<>();

    private JwtTokenDecoder jwtTokenDecoder;

    public JwtAuthenticationConfigurer<H> jwtTokenDecoder(JwtTokenDecoder jwtTokenDecoder) {
        this.jwtTokenDecoder = jwtTokenDecoder;
        return this;
    }

    public JwtAuthenticationConfigurer<H> addJwtTokenExtractor(JwtTokenExtractor jwtTokenExtractor) {
        this.jwtTokenExtractors.add(jwtTokenExtractor);
        return this;
    }

    public JwtAuthenticationConfigurer<H> jwtTokenExtractors(List<JwtTokenExtractor> extractors) {
        this.jwtTokenExtractors.clear();
        this.jwtTokenExtractors.addAll(extractors);
        return this;
    }

    @Override
    public void init(H builder) throws Exception {
        this.setAuthenticationFilter(new JwtAuthenticationFilter());
        this.setFilterBefore(UsernamePasswordAuthenticationFilter.class);
        JwtAuthenticationFilter authenticationFilter = getAuthenticationFilter();
        authenticationFilter.setJwtTokenDecoder(jwtTokenDecoder);
        authenticationFilter.setJwtTokenExtractors(new ArrayList<>(jwtTokenExtractors));
    }

}
