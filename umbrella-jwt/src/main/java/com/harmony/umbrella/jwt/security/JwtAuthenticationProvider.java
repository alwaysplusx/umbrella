package com.harmony.umbrella.jwt.security;

import com.harmony.umbrella.jwt.JwtToken;
import com.harmony.umbrella.jwt.JwtTokenException;
import com.harmony.umbrella.jwt.user.JwtUserDetails;
import com.harmony.umbrella.jwt.user.JwtUserDetailsService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * @author wuxii
 */
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private JwtUserDetailsService jwtUserDetailsService;

    public JwtAuthenticationProvider() {
    }

    public JwtAuthenticationProvider(JwtUserDetailsService jwtUserDetailsService) {
        this.jwtUserDetailsService = jwtUserDetailsService;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        JwtAuthenticationToken token = (JwtAuthenticationToken) authentication;
        Object principal = token.getPrincipal();
        if (!(principal instanceof JwtToken)) {
            throw new JwtTokenException("unknown authentication principal");
        }
        JwtToken jwtToken = ((JwtToken) principal);
        Long uid = jwtToken.getUId();
        JwtUserDetails userDetails = jwtUserDetailsService.loadUserById(uid);
        if (userDetails == null) {
            throw new JwtTokenException("user not found");
        }
        return createSuccessAuthentication(jwtToken, userDetails);
    }

    protected Authentication createSuccessAuthentication(JwtToken jwtToken, JwtUserDetails userDetails) {
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        JwtAuthenticationToken result = new JwtAuthenticationToken(jwtToken, authorities);
        result.setAuthenticated(true);
        result.setDetails(userDetails);
        return result;
    }

    public void setJwtUserDetailsService(JwtUserDetailsService jwtUserDetailsService) {
        this.jwtUserDetailsService = jwtUserDetailsService;
    }

}