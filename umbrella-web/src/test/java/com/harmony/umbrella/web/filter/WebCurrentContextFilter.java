package com.harmony.umbrella.web.filter;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.harmony.umbrella.context.AbstractCurrentContextFilter;
import com.harmony.umbrella.context.ContextHelper;
import com.harmony.umbrella.context.CurrentContext;
import com.harmony.umbrella.util.PatternResourceFilter;
import com.harmony.umbrella.util.StringUtils;

/**
 * @author wuxii@foxmail.com
 */
public class WebCurrentContextFilter extends AbstractCurrentContextFilter {

    private PatternResourceFilter filter;
    private String ipHeader;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        if (filter == null) {
            filter = new PatternResourceFilter();
            String excludes = filterConfig.getInitParameter("excludes");
            if (excludes != null) {
                filter.setExcludes(StringUtils.tokenizeToStringSet(excludes, ","));
            }
            String includes = filterConfig.getInitParameter("includes");
            if (includes != null) {
                filter.setIncludes(StringUtils.tokenizeToStringSet(includes, ","));
            }
        }
        if (ipHeader == null) {
            ipHeader = filterConfig.getInitParameter("ip-header");
        }
    }

    @Override
    protected boolean isCurrentContextRequest(HttpServletRequest request, HttpServletResponse response) {
        String url = ContextHelper.getRequestUrl(request);
        return filter.accept(url);
    }

    @Override
    protected CurrentContext createCurrentContext(HttpServletRequest request, HttpServletResponse response) {
        // return new HttpCurrentContext(request, response, ipHeader);
        return null;
    }

    @Override
    public void destroy() {
    }

    public String getIpHeader() {
        return ipHeader;
    }

    public void setIpHeader(String ipHeader) {
        this.ipHeader = ipHeader;
    }

    public PatternResourceFilter getCurrentContextPathFilter() {
        return filter;
    }

    public void setCurrentContextPathFilter(PatternResourceFilter filter) {
        this.filter = filter;
    }

}
