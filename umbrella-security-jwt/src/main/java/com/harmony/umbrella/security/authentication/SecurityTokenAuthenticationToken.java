package com.harmony.umbrella.security.authentication;

import com.harmony.umbrella.security.SecurityToken;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;

/**
 * @author wuxii
 */
public class SecurityTokenAuthenticationToken extends AbstractAuthenticationToken implements IdentityAuthentication {

	private final Object principal;
	private final SecurityToken securityToken;
	private final Object credentials;

	public SecurityTokenAuthenticationToken(SecurityToken securityToken) {
		this(null, null, securityToken, Collections.emptyList());
	}

	public SecurityTokenAuthenticationToken(Object principal, Object credentials,
											SecurityToken securityToken,
											Collection<? extends GrantedAuthority> authorities) {
		super(authorities);
		this.principal = principal;
		this.credentials = credentials;
		this.securityToken = securityToken;
	}

	@Override
	public Object getPrincipal() {
		return principal;
	}

	@Override
	public Object getCredentials() {
		return credentials;
	}

	@Override
	public SecurityToken getSecurityToken() {
		return securityToken;
	}

}
