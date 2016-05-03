package com.harmony.umbrella.monitor.matcher;

import com.harmony.umbrella.io.util.AntPathMatcher;
import com.harmony.umbrella.io.util.PathMatcher;
import com.harmony.umbrella.monitor.ResourceMatcher;

/**
 * http 资源连接匹配
 * 
 * @author wuxii@foxmail.com
 * @see PathMatcher
 * @see AntPathMatcher
 */
public class ResourcePathMatcher implements ResourceMatcher<String> {

    private final String pattern;
    private PathMatcher matcher;

    public ResourcePathMatcher(String pattern) {
        this.pattern = pattern;
        this.matcher = new AntPathMatcher(pattern);
    }

    @Override
    public boolean matches(String resource) {
        return matcher.isPattern(resource);
    }

    @Override
    public String getExpression() {
        return pattern;
    }

    public PathMatcher getMatcher() {
        return matcher;
    }

    public void setMatcher(PathMatcher matcher) {
        this.matcher = matcher;
    }

    @Override
    public String toString() {
        return "resource matcher of pattern:" + pattern;
    }
}
