package com.harmony.umbrella.web.filter;

import java.io.Serializable;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.harmony.umbrella.context.CurrentContext;
import com.harmony.umbrella.util.StringUtils;

/**
 * @author wuxii@foxmail.com
 */
public class HttpCurrentContext implements CurrentContext, Serializable {

    private static final long serialVersionUID = 5420344680969585609L;

    private final String ipHeader;
    private HttpContext httpContext;
    private Locale locale;

    public HttpCurrentContext(HttpContext httpContext) {
        this(httpContext, null);
    }

    public HttpCurrentContext(HttpContext httpContext, String ipHeader) {
        this.httpContext = httpContext;
        this.ipHeader = ipHeader;
    }

    public HttpCurrentContext(HttpServletRequest request, HttpServletResponse response) {
        this(new HttpContext(request, response), null);
    }

    public HttpCurrentContext(HttpServletRequest request, HttpServletResponse response, String ipHeader) {
        this(new HttpContext(request, response), ipHeader);
    }

    @Override
    public Principals getPrincipals() {
        // FIXME
        return null;
    }

    @Override
    public String getHost() {
        String host = null;
        HttpServletRequest request = httpContext.getHttpRequest();
        if (StringUtils.isNotBlank(ipHeader)) {
            host = request.getHeader(ipHeader);
        }
        return host != null ? host : request.getRemoteAddr();
    }

    @Override
    public Locale getLocale() {
        return locale == null ? httpContext.getHttpRequest().getLocale() : locale;
    }

    @Override
    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    @Override
    public String getCharacterEncoding() {
        return httpContext.getHttpRequest().getCharacterEncoding();
    }

    @Override
    public HttpContext getHttpContext() {
        return httpContext;
    }

}
