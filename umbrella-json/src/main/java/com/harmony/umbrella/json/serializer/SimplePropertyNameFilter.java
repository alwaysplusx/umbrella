package com.harmony.umbrella.json.serializer;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;
import com.harmony.umbrella.util.AntPathMatcher;
import com.harmony.umbrella.util.PathMatcher;

/**
 * 对需要json格式化的对象进行字段的过滤，默认filterMode = EXCLUDE
 * 
 * @author wuxii@foxmail.com
 */
public class SimplePropertyNameFilter extends PropertyNameFilter {

    private static final Log log = Logs.getLog(SimplePropertyNameFilter.class);

    private PathMatcher pathMatcher = new AntPathMatcher(".");

    /**
     * 过滤的模式
     */
    protected FilterMode filterMode = FilterMode.EXCLUDE;
    /**
     * 过滤的模版
     */
    protected final Set<String> patterns = new HashSet<String>();

    public SimplePropertyNameFilter() {
    }

    public SimplePropertyNameFilter(String... patterns) {
        this.addPattern(patterns);
    }

    public SimplePropertyNameFilter(String[] patterns, FilterMode mode) {
        this.filterMode = mode;
        this.addPattern(patterns);
    }

    public SimplePropertyNameFilter(Collection<String> patterns, FilterMode mode) {
        this.filterMode = mode;
        this.addPatterns(patterns);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean accept(Object source, String propertyName) {
        log.debug("filter property name -> {}", propertyName);
        if (patterns.isEmpty() || patterns.contains(propertyName)) {
            // 如果模版是空则根据类型
            return FilterMode.INCLUDE.equals(filterMode);
        }

        if (FilterMode.INCLUDE.equals(filterMode)) {
            // 只有符合条件 = true
            for (String pattern : patterns) {
                if (pattern.startsWith(propertyName) || pathMatcher.match(pattern, propertyName)) {
                    return true;
                }
            }
        } else {
            // 只有不符合条件 = true
            for (String pattern : patterns) {
                if (pathMatcher.match(pattern, propertyName)) {
                    return false;
                }
            }
        }

        return !FilterMode.INCLUDE.equals(filterMode);
    }

    public Set<String> getPatterns() {
        return patterns;
    }

    public void addPattern(String... pattern) {
        Collections.addAll(this.patterns, pattern);
    }

    private void addPatterns(Collection<String> patterns) {
        this.patterns.addAll(patterns);
    }

    public PathMatcher getPathMatcher() {
        return pathMatcher;
    }

    public void setPathMatcher(PathMatcher pathMatcher) {
        this.pathMatcher = pathMatcher;
    }

    public FilterMode getFilterMode() {
        return filterMode;
    }

    public void setFilterMode(FilterMode filterMode) {
        this.filterMode = filterMode;
    }

    public void setPatterns(Set<String> patterns) {
        this.patterns.clear();
        this.patterns.addAll(patterns);
    }

}