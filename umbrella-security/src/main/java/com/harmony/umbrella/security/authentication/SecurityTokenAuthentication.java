package com.harmony.umbrella.security.authentication;

import com.harmony.umbrella.security.SecurityToken;
import org.springframework.security.core.Authentication;

/**
 * @author wuxii
 */
public interface SecurityTokenAuthentication extends Authentication {

	SecurityToken getSecurityToken();

}
