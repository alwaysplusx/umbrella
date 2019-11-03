package com.harmony.umbrella.security.jwt;

import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author wuxii
 */
public interface JwtTokenGenerator {

    /**
     * 根据用户信息生成jwt token
     *
     * @param userDetails 用户信息
     * @return
     */
    String generate(UserDetails userDetails);

}
