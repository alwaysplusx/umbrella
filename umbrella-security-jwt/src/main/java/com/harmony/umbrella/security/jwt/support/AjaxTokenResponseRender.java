package com.harmony.umbrella.security.jwt.support;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author wuxii
 */
public class AjaxTokenResponseRender implements TokenResponseRender {

    private static final String TEMPLATE = "{\"%s\":\"%s\"}";
    private final String tokenName;
    private String contentType = "application/json;charset=UTF-8";

    public AjaxTokenResponseRender() {
        this("token");
    }

    public AjaxTokenResponseRender(String tokenName) {
        this.tokenName = tokenName;
    }

    @Override
    public void render(String token, HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType(contentType);
        response.getWriter().write(buildResponseText(token));
    }

    private String buildResponseText(String token) {
        return tokenName == null ? token : String.format(TEMPLATE, tokenName, token);
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
