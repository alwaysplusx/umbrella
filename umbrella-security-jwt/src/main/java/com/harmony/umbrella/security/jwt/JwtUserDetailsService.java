package com.harmony.umbrella.security.jwt;

/**
 * @author wuxii
 */
public interface JwtUserDetailsService {

    JwtUserDetails loadUserById(Long uid);

}
