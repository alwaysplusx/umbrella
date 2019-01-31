package com.harmony.umbrella.context;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
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
			CurrentContext occ = ApplicationContext.getCurrentContext();
			try {
				CurrentContext ncc = createCurrentContext(request, response);
				ApplicationContext.setCurrentContext(ncc);
				chain.doFilter(request, response);
			} finally {
				ApplicationContext.setCurrentContext(occ);
			}
		} else {
			chain.doFilter(request, response);
		}
	}

	protected abstract boolean isCurrentContextRequest(HttpServletRequest req, HttpServletResponse resp);

	protected abstract CurrentContext createCurrentContext(HttpServletRequest request, HttpServletResponse response);

}
