package com.harmony.umbrella.json.serializer;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.harmony.umbrella.util.AntPathMatcher;
import com.harmony.umbrella.util.PathMatcher;

/**
 * 对需要json格式化的对象进行字段的过滤，默认filterMode = EXCLUDE
 * 
 * @author wuxii@foxmail.com
 */
public class SimplePatternFilter extends PropertyPathFilter {

    private PathMatcher pathMatcher = new AntPathMatcher(".");

    /**
     * 过滤的模版
     */
    protected final Set<String> patterns = new HashSet<String>();

    /**
     * 过滤模式
     */
    protected final boolean include;

    public SimplePatternFilter() {
        this(new String[0], FilterMode.EXCLUDE);
    }

    public SimplePatternFilter(FilterMode mode) {
        this(new String[0], mode);
    }

    public SimplePatternFilter(String... patterns) {
        this(patterns, FilterMode.EXCLUDE);
    }

    public SimplePatternFilter(String[] patterns, FilterMode mode) {
        this(Arrays.asList(patterns), mode);
    }

    public SimplePatternFilter(Collection<String> patterns, FilterMode mode) {
        this.include = (FilterMode.INCLUDE == mode);
        this.patterns.addAll(patterns);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean accept(JSONSerializer serializer, Object source, String name) {
        if (patterns.isEmpty()) {
            return !include;
        }
        if (patterns.contains(name)) {
            return include;
        }
        if (include) {
            // 只有符合条件 = true
            for (String pattern : patterns) {
                if (pattern.startsWith(name) || pathMatcher.match(pattern, name)) {
                    return true;
                }
            }
        } else {
            // 只有不符合条件 = true
            for (String pattern : patterns) {
                if (pathMatcher.match(pattern, name)) {
                    return false;
                }
            }
        }
        return !include;
    }

    public Set<String> getPatterns() {
        return patterns;
    }

    public PathMatcher getPathMatcher() {
        return pathMatcher;
    }

    public void setPathMatcher(PathMatcher pathMatcher) {
        this.pathMatcher = pathMatcher;
    }

    public FilterMode getFilterMode() {
        return include ? FilterMode.INCLUDE : FilterMode.EXCLUDE;
    }

    public void setPatterns(Collection<String> patterns) {
        this.patterns.clear();
        this.patterns.addAll(patterns);
    }

}