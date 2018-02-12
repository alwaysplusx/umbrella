package com.harmony.umbrella.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

/**
 * @author wuxii@foxmail.com
 */
public class PathFilterMode extends FilterMode<String, String> {

    private PathMatcher matcher;

    public PathFilterMode() {
        this(AntPathMatcher.DEFAULT_PATH_SEPARATOR);
    }

    public PathFilterMode(String pathSeparator) {
        this(new AntPathMatcher(pathSeparator));
    }

    public PathFilterMode(PathMatcher matcher) {
        this.matcher = matcher;
    }

    public PathFilterMode(Collection<String> excludes) {
        this.excludes = new HashSet<>(excludes);
    }

    @Override
    protected boolean isMatched(String resource, Set<String> patterns) {
        for (String s : patterns) {
            if (matcher.match(s, resource)) {
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

}
