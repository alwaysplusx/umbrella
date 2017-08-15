package com.harmony.umbrella.context;

import java.util.Enumeration;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author wuxii@foxmail.com
 */
public class DefaultCurrentContext implements CurrentContext {

    private static final long serialVersionUID = 1379790597833043807L;

    private final HttpServletRequest request;
    private final HttpServletResponse response;
    protected Locale locale;

    public DefaultCurrentContext(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    @Override
    public <T> T getUserId() {
        return getSessionAttribute(USER_ID);
    }

    @Override
    public String getUsername() {
        return getSessionAttribute(USER_NAME);
    }

    @Override
    public String getNickname() {
        return getSessionAttribute(USER_NICKNAME);
    }

    @Override
    public String getUserHost() {
        return request.getRemoteAddr();
    }

    @Override
    public boolean isAuthenticated() {
        return getUserId() != null;
    }

    @Override
    public Locale getLocale() {
        if (locale == null) {
            locale = request.getLocale();
        }
        return locale;
    }

    @Override
    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    @Override
    public boolean containsKey(String name) {
        return request.getAttribute(name) != null;
    }

    @Override
    public Enumeration<String> getCurrentNames() {
        return request.getAttributeNames();
    }

    @Override
    public String getCharacterEncoding() {
        return request.getCharacterEncoding();
    }

    @Override
    public HttpServletRequest getHttpRequest() {
        return request;
    }

    @Override
    public HttpServletResponse getHttpResponse() {
        return response;
    }

    @Override
    public boolean doesHttpSessionCreated() {
        return request.getSession(false) != null;
    }

    @Override
    public HttpSession getHttpSession() {
        return getHttpSession(true);
    }

    @Override
    public HttpSession getHttpSession(boolean create) {
        return request.getSession(create);
    }

    @Override
    public String getHttpSessionId() {
        return getHttpSession().getId();
    }

    @SuppressWarnings("unchecked")
    public <T> T getSessionAttribute(String name) {
        HttpSession session = getHttpSession(false);
        return session == null ? null : (T) session.getAttribute(name);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(String name) {
        return (T) request.getAttribute(name);
    }

    @Override
    public void put(String name, Object o) {
        request.setAttribute(name, o);
    }

}
