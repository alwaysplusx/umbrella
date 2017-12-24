package com.harmony.umbrella.context;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import com.harmony.umbrella.util.StringUtils;

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

    private String ipHeader;
    private Set<String> excludedPatterns;
    protected PathMatcher pathMatcher;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String excludes = filterConfig.getInitParameter("excludes");
        if (excludes != null) {
            String[] arr = StringUtils.tokenizeToStringArray(excludes, ",");
            excludedPatterns.addAll(Arrays.asList(arr));
        }
        ipHeader = filterConfig.getInitParameter("ipHeader");
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        if (isExcludedRequest(request)) {
            chain.doFilter(request, response);
        } else {
            CurrentContext occ = ApplicationContext.getCurrentContext();
            try {
                CurrentContext ncc = createCurrentContext(request, response);
                ApplicationContext.setCurrentContext(ncc);
                chain.doFilter(request, response);
            } finally {
                ApplicationContext.setCurrentContext(occ);
            }
        }
    }

    protected CurrentContext createCurrentContext(HttpServletRequest request, HttpServletResponse response) {
        return new HttpCurrentContext(request, response, ipHeader);
    }

    @Override
    public void destroy() {
    }

    protected boolean isExcludedRequest(HttpServletRequest request) {
        if (excludedPatterns == null) {
            return false;
        }
        PathMatcher pathMatcher = getPathMatcher();
        String uri = getUri(request);
        for (String pattern : excludedPatterns) {
            if (pathMatcher.match(pattern, uri)) {
                return true;
            }
        }
        return false;
    }

    private PathMatcher getPathMatcher() {
        if (pathMatcher == null) {
            this.pathMatcher = new AntPathMatcher();
        }
        return pathMatcher;
    }

    public Set<String> getExcludedPatterns() {
        if (excludedPatterns == null) {
            excludedPatterns = new HashSet<>();
        }
        return excludedPatterns;
    }

    public void setExcludedPatterns(Set<String> excludedPatterns) {
        this.excludedPatterns = excludedPatterns;
    }

    public static String getUri(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.substring(request.getContextPath().length());
    }

}
