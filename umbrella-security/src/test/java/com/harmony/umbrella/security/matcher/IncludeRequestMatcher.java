package com.harmony.umbrella.security.matcher;

import org.springframework.http.HttpMethod;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author wuxii
 */
public class IncludeRequestMatcher implements RequestMatcher {

    private final Map<String, List<HttpMethod>> excludeUrls;
    private PathMatcher matcher = new AntPathMatcher();

    public IncludeRequestMatcher(Map<String, List<HttpMethod>> excludeUrls) {
        this.excludeUrls = excludeUrls;
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        String url = getRequestPath(request);
        String matched = null;
        Set<String> patterns = excludeUrls.keySet();
        for (String pattern : patterns) {
            if (matcher.match(pattern, url)) {
                matched = pattern;
                break;
            }
        }
        if (matched == null) {
            return false;
        }
        List<HttpMethod> httpMethods = excludeUrls.get(matched);
        return !httpMethods.contains(HttpMethod.resolve(request.getMethod()));
    }

    protected String getRequestPath(HttpServletRequest request) {
        return request.getRequestURI();
    }

}
