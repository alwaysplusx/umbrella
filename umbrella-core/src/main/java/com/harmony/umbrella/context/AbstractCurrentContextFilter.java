package com.harmony.umbrella.context;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * add filter configuration in web.xml
 *
 * <pre>
 * &lt;filter&gt;
 *  &lt;filter-name&gt;currentContextFilter&lt;/filter-name&gt;
 *  &lt;filter-class&gt;the_class_implements_AbstractCurrentContextFilter&lt;/filter-class&gt;
 * &lt;/filter&gt;
 * &lt;filter-mapping&gt;
 *  &lt;filter-name&gt;currentContextFilter&lt;/filter-name&gt;
 *  &lt;url-pattern&gt;/*&lt;/url-pattern&gt;
 * &lt;/filter-mapping&gt;
 * </pre>
 *
 * @author wuxii@foxmail.com
 */
public abstract class AbstractCurrentContextFilter implements Filter {

    @Override
    public final void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        if (isCurrentContextRequest(request, response)) {
            CurrentContext occ = CurrentContext.get();
            try {
                CurrentContext ncc = createCurrentContext(request, response);
                CurrentContext.set(ncc);
                chain.doFilter(request, response);
            } finally {
                CurrentContext.set(occ);
            }
        } else {
            chain.doFilter(request, response);
        }
    }

    protected abstract boolean isCurrentContextRequest(HttpServletRequest req, HttpServletResponse resp);

    protected abstract CurrentContext createCurrentContext(HttpServletRequest request, HttpServletResponse response);

}
