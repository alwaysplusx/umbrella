package com.harmony.umbrella.security.web;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author wuxii
 */
public abstract class AbstractTokenAuthenticationFilter extends GenericFilterBean {

    protected ApplicationEventPublisher eventPublisher;
    protected AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource = new WebAuthenticationDetailsSource();
    private AuthenticationManager authenticationManager;
    private AuthenticationSuccessHandler authenticationSuccessHandler;
    private AuthenticationFailureHandler authenticationFailureHandler;
    private boolean continueFilterChainOnUnsuccessfulAuthentication = true;
    private boolean continueFilterChainOnUnsuccessfulPrepareAuthentication = true;

    protected abstract Authentication getRequestAuthentication(HttpServletRequest request);

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        Authentication authRequest = prepareRequestAuthentication(request);

        if (authRequest == null) {
            chain.doFilter(request, response);
            return;
        }

        try {
            Authentication authResult = getAuthenticationManager().authenticate(authRequest);

            successfulAuthentication(request, response, authResult);

        } catch (AuthenticationException failed) {
            unsuccessfulAuthentication(request, response, failed);
            if (!continueFilterChainOnUnsuccessfulAuthentication) {
                throw failed;
            }
        }

        chain.doFilter(request, response);

    }

    private Authentication prepareRequestAuthentication(HttpServletRequest request) {
        Authentication authRequest = null;
        try {
            authRequest = getRequestAuthentication(request);
        } catch (AuthenticationException failed) {
            if (!continueFilterChainOnUnsuccessfulPrepareAuthentication) {
                throw failed;
            }
        }
        return authRequest;
    }

    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, Authentication authResult)
            throws IOException, ServletException {
        if (logger.isDebugEnabled()) {
            logger.debug("Authentication success: " + authResult);
        }
        SecurityContextHolder.getContext().setAuthentication(authResult);
        // Fire event
        if (this.eventPublisher != null) {
            eventPublisher.publishEvent(new InteractiveAuthenticationSuccessEvent(authResult, this.getClass()));
        }

        if (authenticationSuccessHandler != null) {
            authenticationSuccessHandler.onAuthenticationSuccess(request, response, authResult);
        }
    }

    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed)
            throws IOException, ServletException {
        SecurityContextHolder.clearContext();

        if (logger.isDebugEnabled()) {
            logger.debug("Cleared security context due to exception", failed);
        }
        request.setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, failed);

        if (authenticationFailureHandler != null) {
            authenticationFailureHandler.onAuthenticationFailure(request, response, failed);
        }
    }

    protected AuthenticationManager getAuthenticationManager() {
        return authenticationManager;
    }

    protected AuthenticationSuccessHandler getAuthenticationSuccessHandler() {
        return authenticationSuccessHandler;
    }

    protected AuthenticationFailureHandler getAuthenticationFailureHandler() {
        return authenticationFailureHandler;
    }

    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public void setAuthenticationSuccessHandler(AuthenticationSuccessHandler successHandler) {
        this.authenticationSuccessHandler = successHandler;
    }

    public void setAuthenticationFailureHandler(AuthenticationFailureHandler failureHandler) {
        this.authenticationFailureHandler = failureHandler;
    }

    public void setContinueFilterChainOnUnsuccessfulAuthentication(boolean continueFilterChainOnUnsuccessfulAuthentication) {
        this.continueFilterChainOnUnsuccessfulAuthentication = continueFilterChainOnUnsuccessfulAuthentication;
    }

    public void setContinueFilterChainOnUnsuccessfulPrepareAuthentication(boolean continueFilterChainOnUnsuccessfulPrepareAuthentication) {
        this.continueFilterChainOnUnsuccessfulPrepareAuthentication = continueFilterChainOnUnsuccessfulPrepareAuthentication;
    }

    public void setAuthenticationDetailsSource(AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource) {
        this.authenticationDetailsSource = authenticationDetailsSource;
    }

    public void setApplicationEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

}
