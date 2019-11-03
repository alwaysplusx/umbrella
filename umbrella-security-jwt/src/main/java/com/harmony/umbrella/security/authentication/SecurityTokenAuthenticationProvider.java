package com.harmony.umbrella.security.authentication;

import com.harmony.umbrella.security.SecurityToken;
import com.harmony.umbrella.security.SecurityTokenUsernameResolver;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * @author wuxii
 */
public class SecurityTokenAuthenticationProvider implements AuthenticationProvider {

    private UserDetailsService userDetailsService;

    private SecurityTokenUsernameResolver usernameResolver;

    public SecurityTokenAuthenticationProvider() {
    }

    public SecurityTokenAuthenticationProvider(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return SecurityTokenAuthenticationToken.class.isAssignableFrom(authentication);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        SecurityTokenAuthenticationToken authRequest = (SecurityTokenAuthenticationToken) authentication;
        SecurityToken securityToken = authRequest.getSecurityToken();

        UserDetails userDetails = userDetailsService.loadUserByUsername(usernameResolver.resolve(securityToken));

        SecurityTokenAuthenticationToken authResult = new SecurityTokenAuthenticationToken(
                userDetails.getUsername(),
                userDetails.getPassword(),
                securityToken,
                userDetails.getAuthorities());

        authResult.setAuthenticated(true);
        authResult.setDetails(authRequest.getDetails());

        return authResult;
    }

    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    public void setUsernameResolver(SecurityTokenUsernameResolver usernameResolver) {
        this.usernameResolver = usernameResolver;
    }
    
}
