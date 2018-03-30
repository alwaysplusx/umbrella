package com.harmony.umbrella.json.serializer;

import java.util.Arrays;
import java.util.Collection;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.harmony.umbrella.util.PatternResourceFilter;

/**
 * 对需要json格式化的对象进行字段的过滤，默认filterMode = EXCLUDE
 * 
 * @author wuxii@foxmail.com
 */
public class SimplePatternPropertyPreFilter extends PathPropertyPreFilter {

    private PatternResourceFilter<String> resourceFilter;

    public SimplePatternPropertyPreFilter() {
    }

    public SimplePatternPropertyPreFilter(String... excludes) {
        this(Arrays.asList(excludes));
    }

    public SimplePatternPropertyPreFilter(Collection<String> excludes) {
        this(new PatternResourceFilter(excludes));
    }

    public SimplePatternPropertyPreFilter(PatternResourceFilter<String> filterMode) {
        this.resourceFilter = filterMode;
    }

    @Override
    public boolean accept(JSONSerializer serializer, Object source, String name) {
        return name != null && resourceFilter.accept(name);
    }

    public PatternResourceFilter<String> getResourceFilter() {
        return resourceFilter;
    }

    public void setResourceFilter(PatternResourceFilter<String> resourceFilter) {
        this.resourceFilter = resourceFilter;
    }

}