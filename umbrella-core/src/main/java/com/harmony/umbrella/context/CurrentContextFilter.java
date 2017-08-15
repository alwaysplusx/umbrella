package com.harmony.umbrella.context;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * add filter configuration in web.xml
 * 
 * <pre>
 * &lt;filter&gt; 
 *  &lt;filter-name&gt;currentContextFilter&lt;/filter-name&gt;
 *  &lt;filter-class&gt;com.harmony.umbrella.context.CurrentContextFilter&lt;/filter-class&gt;
 * &lt;/filter&gt; 
 * &lt;filter-mapping&gt;
 *  &lt;filter-name&gt;currentContextFilter&lt;/filter-name&gt;
 *  &lt;url-pattern&gt;/*&lt;/url-pattern&gt; 
 * &lt;/filter-mapping&gt;
 * </pre>
 * 
 * @author wuxii@foxmail.com
 */
public class CurrentContextFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        CurrentContext occ = ApplicationContext.getCurrentContext();
        try {
            CurrentContext ncc = createCurrentContext(request, response);
            ApplicationContext.setCurrentContext(ncc);
            chain.doFilter(request, response);
        } finally {
            ApplicationContext.setCurrentContext(occ);
        }
    }

    protected CurrentContext createCurrentContext(ServletRequest request, ServletResponse response) {
        return new DefaultCurrentContext((HttpServletRequest) request, (HttpServletResponse) response);
    }

    @Override
    public void destroy() {
    }

}
