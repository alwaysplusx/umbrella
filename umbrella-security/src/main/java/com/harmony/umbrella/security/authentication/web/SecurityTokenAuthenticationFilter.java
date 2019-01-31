package com.harmony.umbrella.security.authentication.web;

import com.harmony.umbrella.security.SecurityToken;
import com.harmony.umbrella.security.SecurityTokenExtractor;
import com.harmony.umbrella.security.authentication.SecurityTokenAuthenticationToken;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;

/**
 * @author wuxii
 */
public class SecurityTokenAuthenticationFilter extends AbstractTokenAuthenticationFilter {

    private SecurityTokenExtractor securityTokenExtractor;

    public SecurityTokenAuthenticationFilter() {
    }

    public SecurityTokenAuthenticationFilter(SecurityTokenExtractor securityTokenExtractor) {
        this.securityTokenExtractor = securityTokenExtractor;
    }

    @Override
    protected Authentication getRequestAuthentication(HttpServletRequest request) {
        SecurityToken securityToken = securityTokenExtractor.extract(request);
        SecurityTokenAuthenticationToken authRequest = new SecurityTokenAuthenticationToken(securityToken);
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
        return authRequest;
    }

    public void setSecurityTokenExtractor(SecurityTokenExtractor securityTokenExtractor) {
        this.securityTokenExtractor = securityTokenExtractor;
    }

}
