package com.harmony.umbrella.util;

import java.util.Collection;
import java.util.Set;

import org.springframework.core.convert.converter.Converter;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

/**
 * @author wuxii@foxmail.com
 */
public class PatternResourceFilter<R> extends ResourceFilter<R, String> {

    private PathMatcher matcher;
    private Converter<R, String> resourceConverter;

    public PatternResourceFilter() {
        this((Converter<R, String>) ToStringConverter.INSTANCE);
    }

    public PatternResourceFilter(Collection<String> patterns) {
        this((Converter<R, String>) ToStringConverter.INSTANCE);
        this.setExcludes(patterns);
    }

    public PatternResourceFilter(PathMatcher matcher) {
        this((Converter<R, String>) ToStringConverter.INSTANCE);
        this.matcher = matcher;
    }

    public PatternResourceFilter(Converter<R, String> resourceConverter) {
        this.resourceConverter = resourceConverter;
        this.matcher = new AntPathMatcher();
    }

    public PatternResourceFilter(PathMatcher matcher, Converter<R, String> resourceConverter) {
        this.matcher = matcher;
        this.resourceConverter = resourceConverter;
    }

    @Override
    protected boolean isMatched(R resource, Set<String> patterns) {
        String res = resourceConverter.convert(resource);
        for (String s : patterns) {
            if (matcher.match(s, res)) {
                return true;
            }
        }
        return false;
    }

    public PathMatcher getPathMatcher() {
        return matcher;
    }

    public void setPathMatcher(PathMatcher matcher) {
        this.matcher = matcher;
    }

    public Converter<R, String> getResourceConverter() {
        return resourceConverter;
    }

    public void setResourceConverter(Converter<R, String> resourceConverter) {
        this.resourceConverter = resourceConverter;
    }

    private static final class ToStringConverter implements Converter<Object, String> {

        public static final Converter<Object, String> INSTANCE = new ToStringConverter();

        @Override
        public String convert(Object source) {
            return source == null ? null : source.toString();
        }

    }

}
