package com.harmony.umbrella.security.configurers;

import com.harmony.umbrella.security.authentication.web.AbstractTokenAuthenticationFilter;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;

/**
 * @author wuxii
 */
public class AbstractTokenAuthenticationFilterConfigurer<B extends HttpSecurityBuilder<B>, T extends AbstractTokenAuthenticationFilterConfigurer<B, T, F>, F extends AbstractTokenAuthenticationFilter>
        extends AbstractHttpConfigurer<T, B> {

    private F authFilter;

    private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource;
    private AuthenticationSuccessHandler authenticationSuccessHandler;
    private AuthenticationFailureHandler authenticationFailureHandler;
    private boolean continueFilterChainOnUnsuccessfulAuthentication = true;
    private boolean continueFilterChainOnUnsuccessfulPrepareAuthentication = true;

    private Class<? extends Filter> addFilterAfter;
    private Class<? extends Filter> addFilterBefore;

    public final T authenticationDetailsSource(AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource) {
        this.authenticationDetailsSource = authenticationDetailsSource;
        return getSelf();
    }

    public final T authenticationSuccessHandler(AuthenticationSuccessHandler authenticationSuccessHandler) {
        this.authenticationSuccessHandler = authenticationSuccessHandler;
        return getSelf();
    }

    public final T authenticationFailureHandler(AuthenticationFailureHandler authenticationFailureHandler) {
        this.authenticationFailureHandler = authenticationFailureHandler;
        return getSelf();
    }


    public T setContinueFilterChainOnUnsuccessfulAuthentication(boolean continueFilterChainOnUnsuccessfulAuthentication) {
        this.continueFilterChainOnUnsuccessfulAuthentication = continueFilterChainOnUnsuccessfulAuthentication;
        return getSelf();
    }

    public T setContinueFilterChainOnUnsuccessfulPrepareAuthentication(boolean continueFilterChainOnUnsuccessfulPrepareAuthentication) {
        this.continueFilterChainOnUnsuccessfulPrepareAuthentication = continueFilterChainOnUnsuccessfulPrepareAuthentication;
        return getSelf();
    }

    protected void setFilterBefore(Class<? extends Filter> filterClass) {
        this.addFilterBefore = filterClass;
        this.addFilterAfter = null;
    }

    protected void setFilterAfter(Class<? extends Filter> filterClass) {
        this.addFilterBefore = null;
        this.addFilterAfter = filterClass;
    }

    private T getSelf() {
        return (T) this;
    }

    protected final void setAuthenticationFilter(F authFilter) {
        this.authFilter = authFilter;
    }

    protected final F getAuthenticationFilter() {
        return authFilter;
    }

    @Override
    public void configure(B builder) throws Exception {
        authFilter.setAuthenticationManager(builder.getSharedObject(AuthenticationManager.class));
        authFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler);
        authFilter.setAuthenticationFailureHandler(authenticationFailureHandler);
        authFilter.setContinueFilterChainOnUnsuccessfulAuthentication(continueFilterChainOnUnsuccessfulAuthentication);
        authFilter.setContinueFilterChainOnUnsuccessfulPrepareAuthentication(continueFilterChainOnUnsuccessfulPrepareAuthentication);
        if (authenticationDetailsSource != null) {
            authFilter.setAuthenticationDetailsSource(authenticationDetailsSource);
        }

        F filter = postProcess(authFilter);
        if (addFilterBefore == null && addFilterAfter == null) {
            builder.addFilter(filter);
        } else if (addFilterBefore != null) {
            builder.addFilterBefore(filter, addFilterBefore);
        } else {
            builder.addFilterAfter(filter, addFilterAfter);
        }
    }

}
