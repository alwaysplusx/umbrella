package com.harmony.umbrella.json.serializer;

import java.util.Arrays;
import java.util.Collection;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.harmony.umbrella.util.FilterMode;
import com.harmony.umbrella.util.PathFilterMode;

/**
 * 对需要json格式化的对象进行字段的过滤，默认filterMode = EXCLUDE
 * 
 * @author wuxii@foxmail.com
 */
public class SimplePatternFilter extends PropertyPathFilter {

    private FilterMode<String, String> filterMode;

    public SimplePatternFilter() {
    }

    public SimplePatternFilter(String... excludes) {
        this(Arrays.asList(excludes));
    }

    public SimplePatternFilter(Collection<String> excludes) {
        this(new PathFilterMode(excludes));
    }

    public SimplePatternFilter(FilterMode<String, String> filterMode) {
        this.filterMode = filterMode;
    }

    @Override
    public boolean accept(JSONSerializer serializer, Object source, String name) {
        return name != null && filterMode.accept(name);
    }

    protected FilterMode<String, String> getFilterMode() {
        return filterMode;
    }

    protected void setFilterMode(FilterMode<String, String> filterMode) {
        this.filterMode = filterMode;
    }

}