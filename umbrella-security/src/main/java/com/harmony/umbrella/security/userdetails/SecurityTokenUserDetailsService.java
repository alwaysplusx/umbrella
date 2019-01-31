package com.harmony.umbrella.security.userdetails;

import com.harmony.umbrella.security.SecurityToken;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author wuxii
 */
public interface SecurityTokenUserDetailsService {

	UserDetails loadUserBySecurityToken(SecurityToken securityToken);

}
