package com.harmony.umbrella.security.jwt;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.core.AuthenticatedPrincipal;

import java.time.Instant;
import java.util.List;

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

    public Long getUId() {
        Claim claim = jwt.getClaim("uid");
        return claim != null ? claim.asLong() : null;
    }

    @Override
    public String getName() {
        List<String> audience = jwt.getAudience();
        return audience != null && !audience.isEmpty() ? audience.get(0) : null;
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
