package com.harmony.umbrella.security.authentication;

import com.harmony.umbrella.security.SecurityToken;
import org.springframework.security.core.Authentication;

/**
 * @author wuxii
 */
public interface IdentityAuthentication extends Authentication {

	SecurityToken getSecurityToken();

}
