package com.harmony.umbrella.security.jwt;

import com.harmony.umbrella.security.SecurityToken;
import com.harmony.umbrella.security.SecurityTokenUsernameResolver;

public class JwtUsernameResolver implements SecurityTokenUsernameResolver {

    private JwtTokenDecoder jwtTokenDecoder;

    public JwtUsernameResolver() {
    }

    public JwtUsernameResolver(JwtTokenDecoder jwtTokenDecoder) {
        this.jwtTokenDecoder = jwtTokenDecoder;
    }

    @Override
    public String resolve(SecurityToken securityToken) {
        return jwtTokenDecoder.decode(securityToken.getToken()).getUID();
    }

    public void setJwtTokenDecoder(JwtTokenDecoder jwtTokenDecoder) {
        this.jwtTokenDecoder = jwtTokenDecoder;
    }
}
