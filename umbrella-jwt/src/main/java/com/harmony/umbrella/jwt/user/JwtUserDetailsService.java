package com.harmony.umbrella.jwt.user;

/**
 * @author wuxii
 */
public interface JwtUserDetailsService {

    JwtUserDetails loadUserById(Long uid);

}
