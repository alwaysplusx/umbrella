package com.harmony.umbrella.security.jwt.configurers;

import com.harmony.umbrella.security.jwt.JwtTokenDecoder;
import com.harmony.umbrella.security.jwt.JwtTokenExtractor;
import com.harmony.umbrella.security.jwt.matcher.ExcludeRequestMatcher;
import com.harmony.umbrella.security.jwt.security.web.JwtAuthenticationFilter;
import com.harmony.umbrella.security.jwt.support.HttpHeaderJwtTokenExtractor;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.*;
import java.util.stream.Stream;

/**
 * @author wuxii
 */
public class JwtAuthenticationConfigurer<H extends HttpSecurityBuilder<H>> extends
        AbstractHttpConfigurer<JwtAuthenticationConfigurer<H>, H> {

    private Collection<JwtTokenExtractor> jwtTokenExtractors = new HashSet<>(Collections.singletonList(HttpHeaderJwtTokenExtractor.INSTANCE));

    private JwtTokenDecoder jwtTokenDecoder;

    private RequestMatcherConfigurer requestMatcherConfigurer;

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

    public ExcludeRequestMatcherConfigurer excludeRequestMatcher() {
        return applyRequestMatcherConfigurer(new ExcludeRequestMatcherConfigurer());
    }

    public JwtAuthenticationConfigurer<H> anyRequestMatcher() {
        applyRequestMatcherConfigurer(new AnyRequestMatcherConfigurer());
        return this;
    }

    public <T extends RequestMatcherConfigurer> T applyRequestMatcherConfigurer(T configurer) {
        this.requestMatcherConfigurer = configurer;
        return configurer;
    }

    @Override
    public void configure(H http) throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter();
        filter.setJwtTokenExtractors(new ArrayList<>(jwtTokenExtractors));
        filter.setJwtTokenDecoder(jwtTokenDecoder);
        filter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
        filter.setRequestMatcher(requestMatcherConfigurer.build());
        http.addFilterBefore(postProcess(filter), UsernamePasswordAuthenticationFilter.class);
    }

    public abstract class RequestMatcherConfigurer {

        public H and() {
            return JwtAuthenticationConfigurer.this.and();
        }

        protected abstract RequestMatcher build();

    }

    private class AnyRequestMatcherConfigurer extends RequestMatcherConfigurer {

        @Override
        protected RequestMatcher build() {
            return AnyRequestMatcher.INSTANCE;
        }

    }

    public class ExcludeRequestMatcherConfigurer extends RequestMatcherConfigurer {

        private Map<String, List<HttpMethod>> urls = new HashMap<>();

        public ExcludeRequestMatcherConfigurer excludeUrl(String url, HttpMethod... httpMethods) {
            urls.put(url, Arrays.asList(httpMethods));
            return this;
        }

        public ExcludeRequestMatcherConfigurer excludeUrls(String... urls) {
            Stream.of(urls).forEach(e -> excludeUrl(e, HttpMethod.values()));
            return this;
        }

        @Override
        protected RequestMatcher build() {
            return new NegatedRequestMatcher(new ExcludeRequestMatcher(urls));
        }

    }

}
