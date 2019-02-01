package com.harmony.umbrella.security.jwt;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.core.AuthenticatedPrincipal;

import java.time.Instant;
import java.util.List;

/**
 * @author wuxii
 */
public class JwtToken implements AuthenticatedPrincipal {

	private DecodedJWT jwt;

	public JwtToken(DecodedJWT jwt) {
		this.jwt = jwt;
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

	public DecodedJWT getJwt() {
		return jwt;
	}

}
