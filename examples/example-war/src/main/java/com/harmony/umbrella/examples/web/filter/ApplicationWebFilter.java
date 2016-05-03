package com.harmony.umbrella.examples.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.harmony.umbrella.context.ApplicationContext;
import com.harmony.umbrella.context.CurrentContext;
import com.harmony.umbrella.context.DefaultHttpCurrentContext;

/**
 * @author wuxii@foxmail.com
 */
@WebFilter(urlPatterns = "/*")
public class ApplicationWebFilter implements Filter {

    private ApplicationContext context;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        context = ApplicationContext.getApplicationContext();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        CurrentContext occ = context.getCurrentContext();

        try {
            context.setCurrentContext(new DefaultHttpCurrentContext(req, resp));
            chain.doFilter(request, response);
        } finally {
            context.setCurrentContext(occ);
        }
    }

    @Override
    public void destroy() {
    }

}
