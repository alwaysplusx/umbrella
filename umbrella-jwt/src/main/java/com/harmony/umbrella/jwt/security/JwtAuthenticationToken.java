package com.harmony.umbrella.jwt.security;

import com.harmony.umbrella.jwt.JwtToken;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;

/**
 * @author wuxii
 */
public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final JwtToken jwtToken;

    public JwtAuthenticationToken(JwtToken jwtToken) {
        this(jwtToken, Collections.emptyList());
    }

    public JwtAuthenticationToken(JwtToken jwtToken, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.jwtToken = jwtToken;
    }

    @Override
    public Object getPrincipal() {
        return jwtToken;
    }

    @Override
    public Object getCredentials() {
        return jwtToken;
    }

    public String getTokenValue() {
        return jwtToken.getTokenValue();
    }

    public JwtToken getJwtToken() {
        return jwtToken;
    }

    public Long getUId() {
        return jwtToken.getUId();
    }

}
