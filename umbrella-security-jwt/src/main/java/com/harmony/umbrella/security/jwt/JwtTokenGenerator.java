package com.harmony.umbrella.security.jwt;

import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.http.HttpServletRequest;

/**
 * @author wuxii
 */
public interface JwtTokenGenerator {

    /**
     * 根据用户信息生成jwt token
     *
     * @param userDetails 用户信息
     * @param request     http request
     * @return
     */
    String generate(UserDetails userDetails, HttpServletRequest request);

}
