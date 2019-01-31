package com.harmony.umbrella.security.userdetails;

/**
 * @author wuxii
 */
public interface JwtUserDetailsService {

	JwtUserDetails loadUserById(Long uid);

}
