package com.harmony.umbrella.security.authentication;

import com.harmony.umbrella.security.SecurityToken;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;

/**
 * @author wuxii
 */
public class SecurityTokenAuthenticationToken extends AbstractAuthenticationToken {

	private final SecurityToken securityToken;

	public SecurityTokenAuthenticationToken(SecurityToken securityToken) {
		this(securityToken, Collections.emptyList());
	}

	public SecurityTokenAuthenticationToken(SecurityToken securityToken, Collection<? extends GrantedAuthority> authorities) {
		super(authorities);
		this.securityToken = securityToken;
	}

	@Override
	public Object getPrincipal() {
		return securityToken;
	}

	@Override
	public Object getCredentials() {
		return null;
	}

	public SecurityToken getSecurityToken() {
		return securityToken;
	}

}
