package com.harmony.umbrella.jwt.security.web;

import com.harmony.umbrella.jwt.JwtToken;
import com.harmony.umbrella.jwt.JwtTokenDecoder;
import com.harmony.umbrella.jwt.JwtTokenExtractor;
import com.harmony.umbrella.jwt.security.JwtAuthenticationToken;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.OrderComparator;
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
import org.springframework.security.web.util.matcher.AnyRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wuxii
 */
public class JwtAuthenticationFilter extends GenericFilterBean {

    private ApplicationEventPublisher eventPublisher = null;
    private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource = new WebAuthenticationDetailsSource();
    private AuthenticationManager authenticationManager = null;
    private boolean continueFilterChainOnUnsuccessfulAuthentication = true;
    private boolean checkForPrincipalChanges;
    private boolean invalidateSessionOnPrincipalChange = true;
    private AuthenticationSuccessHandler authenticationSuccessHandler = null;
    private AuthenticationFailureHandler authenticationFailureHandler = null;

    private RequestMatcher requestMatcher = AnyRequestMatcher.INSTANCE;

    private List<JwtTokenExtractor> jwtTokenExtractors = new ArrayList<>();

    private JwtTokenDecoder jwtTokenDecoder;

    @Override
    public void afterPropertiesSet() {
        try {
            super.afterPropertiesSet();
        } catch (ServletException e) {
            // convert to RuntimeException for passivity on afterPropertiesSet signature
            throw new RuntimeException(e);
        }
        OrderComparator.sort(jwtTokenExtractors);
        Assert.notNull(authenticationManager, "An AuthenticationManager must be set");
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (logger.isDebugEnabled()) {
            logger.debug("Checking secure context token: "
                    + SecurityContextHolder.getContext().getAuthentication());
        }

        if (requiresAuthentication((HttpServletRequest) request)) {
            doAuthenticate((HttpServletRequest) request, (HttpServletResponse) response);
        }

        chain.doFilter(request, response);
    }


    protected boolean principalChanged(HttpServletRequest request, Authentication currentAuthentication) {
        Authentication authRequest = getPreAuthentication(request);
        Object principal = authRequest.getPrincipal();

        if ((principal instanceof String) && currentAuthentication.getName().equals(principal)) {
            return false;
        }

        if (principal != null && principal.equals(currentAuthentication.getPrincipal())) {
            return false;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Pre-authenticated principal has changed to " + principal + " and will be reauthenticated");
        }
        return true;
    }


    private void doAuthenticate(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        try {
            Authentication authRequest = getPreAuthentication(request);

            if (authRequest == null) {
                return;
            }

            Authentication authResult = authenticationManager.authenticate(authRequest);
            successfulAuthentication(request, response, authResult);
        } catch (AuthenticationException failed) {
            unsuccessfulAuthentication(request, response, failed);

            if (!continueFilterChainOnUnsuccessfulAuthentication) {
                throw failed;
            }
        }
    }

    private boolean requiresAuthentication(HttpServletRequest request) {
        if (!requestMatcher.matches(request)) {
            return false;
        }

        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();

        if (currentUser == null) {
            return true;
        }

        if (!checkForPrincipalChanges) {
            return false;
        }

        if (!principalChanged(request, currentUser)) {
            return false;
        }

        logger.debug("Pre-authenticated principal has changed and will be reauthenticated");

        if (invalidateSessionOnPrincipalChange) {
            SecurityContextHolder.clearContext();

            HttpSession session = request.getSession(false);

            if (session != null) {
                logger.debug("Invalidating existing session");
                session.invalidate();
                request.getSession();
            }
        }

        return true;
    }

    protected Authentication getPreAuthentication(HttpServletRequest request)
            throws AuthenticationException {

        JwtToken jwtToken = null;

        String tokenValue = extractJwtTokenValue(request);
        if (StringUtils.hasText(tokenValue)) {
            jwtToken = jwtTokenDecoder.decode(tokenValue);
        }

        JwtAuthenticationToken authRequest = new JwtAuthenticationToken(jwtToken);
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
        return authRequest;
    }

    private String extractJwtTokenValue(HttpServletRequest request) {
        for (JwtTokenExtractor extractor : jwtTokenExtractors) {
            String tokenValue = extractor.extract(request);
            if (StringUtils.hasText(tokenValue)) {
                return tokenValue;
            }
        }
        return null;
    }


    /**
     * Puts the <code>Authentication</code> instance returned by the authentication
     * manager into the secure context.
     */
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

    /**
     * Ensures the authentication object in the secure context is set to null when
     * authentication fails.
     * <p>
     * Caches the failure exception as a request attribute
     */
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

    /**
     * @param anApplicationEventPublisher The ApplicationEventPublisher to use
     */
    public void setApplicationEventPublisher(ApplicationEventPublisher anApplicationEventPublisher) {
        this.eventPublisher = anApplicationEventPublisher;
    }

    /**
     * @param authenticationDetailsSource The AuthenticationDetailsSource to use
     */
    public void setAuthenticationDetailsSource(AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource) {
        Assert.notNull(authenticationDetailsSource, "AuthenticationDetailsSource required");
        this.authenticationDetailsSource = authenticationDetailsSource;
    }

    protected AuthenticationDetailsSource<HttpServletRequest, ?> getAuthenticationDetailsSource() {
        return authenticationDetailsSource;
    }

    /**
     * @param authenticationManager The AuthenticationManager to use
     */
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    /**
     * If set to {@code true}, any {@code AuthenticationException} raised by the
     * {@code AuthenticationManager} will be swallowed, and the request will be allowed to
     * proceed, potentially using alternative authentication mechanisms. If {@code false}
     * (the default), authentication failure will result in an immediate exception.
     *
     * @param shouldContinue set to {@code true} to allow the request to proceed after a
     *                       failed authentication.
     */
    public void setContinueFilterChainOnUnsuccessfulAuthentication(boolean shouldContinue) {
        continueFilterChainOnUnsuccessfulAuthentication = shouldContinue;
    }

    /**
     * If set, the pre-authenticated principal will be checked on each request and
     * compared against the name of the current <tt>Authentication</tt> object. A check to
     * determine if {@link Authentication#getPrincipal()} is equal to the principal will
     * also be performed. If a change is detected, the user will be reauthenticated.
     *
     * @param checkForPrincipalChanges
     */
    public void setCheckForPrincipalChanges(boolean checkForPrincipalChanges) {
        this.checkForPrincipalChanges = checkForPrincipalChanges;
    }

    /**
     * If <tt>checkForPrincipalChanges</tt> is set, and a change of principal is detected,
     * determines whether any existing session should be invalidated before proceeding to
     * authenticate the new principal.
     *
     * @param invalidateSessionOnPrincipalChange <tt>false</tt> to retain the existing
     *                                           session. Defaults to <tt>true</tt>.
     */
    public void setInvalidateSessionOnPrincipalChange(boolean invalidateSessionOnPrincipalChange) {
        this.invalidateSessionOnPrincipalChange = invalidateSessionOnPrincipalChange;
    }

    /**
     * Sets the strategy used to handle a successful authentication.
     */
    public void setAuthenticationSuccessHandler(AuthenticationSuccessHandler authenticationSuccessHandler) {
        this.authenticationSuccessHandler = authenticationSuccessHandler;
    }

    /**
     * Sets the strategy used to handle a failed authentication.
     */
    public void setAuthenticationFailureHandler(AuthenticationFailureHandler authenticationFailureHandler) {
        this.authenticationFailureHandler = authenticationFailureHandler;
    }

    public void setJwtTokenExtractors(List<JwtTokenExtractor> jwtTokenExtractors) {
        this.jwtTokenExtractors = jwtTokenExtractors;
    }

    public void setJwtTokenDecoder(JwtTokenDecoder jwtTokenDecoder) {
        Assert.notNull(jwtTokenDecoder, "jwt token decoder not nullable");
        this.jwtTokenDecoder = jwtTokenDecoder;
    }

    public void setRequestMatcher(RequestMatcher requestMatcher) {
        this.requestMatcher = requestMatcher;
    }

}