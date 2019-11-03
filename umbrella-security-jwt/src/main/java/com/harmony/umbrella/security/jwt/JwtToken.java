package com.harmony.umbrella.security.jwt;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.core.AuthenticatedPrincipal;

import java.time.Instant;

/**
 * TODO change to jwt token interface
 *
 * @author wuxii
 */
public class JwtToken implements AuthenticatedPrincipal {

    private DecodedJWT jwt;
    private Exception jwtDecodeException;

    public JwtToken(DecodedJWT jwt) {
        this.jwt = jwt;
    }

    public JwtToken(DecodedJWT jwt, Exception jwtDecodeException) {
        this.jwt = jwt;
        this.jwtDecodeException = jwtDecodeException;
    }

    public String getTokenValue() {
        return jwt.getToken();
    }

    public String getUID() {
        Claim claim = jwt.getClaim("uid");
        return claim != null ? claim.asString() : null;
    }

    @Override
    public String getName() {
        return getUID();
    }

    public boolean isExpired() {
        return Instant.now().isAfter(jwt.getExpiresAt().toInstant());
    }

    public Exception getJwtDecodeException() {
        return jwtDecodeException;
    }

    public DecodedJWT getJwt() {
        return jwt;
    }

}
