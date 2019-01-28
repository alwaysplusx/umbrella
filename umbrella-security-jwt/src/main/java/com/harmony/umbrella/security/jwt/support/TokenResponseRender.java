package com.harmony.umbrella.security.jwt.support;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author wuxii
 */
public interface TokenResponseRender {

    void render(String token, HttpServletRequest request, HttpServletResponse response) throws IOException;

}
