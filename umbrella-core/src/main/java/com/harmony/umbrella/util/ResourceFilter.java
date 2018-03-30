package com.harmony.umbrella.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author wuxii@foxmail.com
 */
public abstract class ResourceFilter<R, P> {

    protected Set<P> includes;
    protected Set<P> excludes;

    public boolean accept(R resource) {
        return isEmpty(includes)//
                ? isEmpty(excludes) || !isMatched(resource, excludes)//
                : isMatched(resource, includes) && (isEmpty(excludes) || !isMatched(resource, excludes));
    }

    protected abstract boolean isMatched(R resource, Set<P> patterns);

    protected boolean isEmpty(Set<P> s) {
        return s == null || s.isEmpty();
    }

    public Set<P> getIncludes() {
        return includes;
    }

    public void setIncludes(Collection<P> includes) {
        this.includes = new HashSet<>(includes);
    }

    public Set<P> getExcludes() {
        return excludes;
    }

    public void setExcludes(Collection<P> excludes) {
        this.excludes = new HashSet<>(excludes);
    }

    public void addIncludes(Collection<P> includes) {
        if (this.includes == null) {
            this.includes = new HashSet<>();
        }
        this.includes.addAll(includes);
    }

    public void addIncludes(P... includes) {
        addIncludes(Arrays.asList(includes));
    }

    public void addExcludes(Collection<P> includes) {
        if (this.excludes == null) {
            this.excludes = new HashSet<>();
        }
        this.excludes.addAll(includes);
    }

    public void addExcludes(P... excludes) {
        addExcludes(Arrays.asList(excludes));
    }

}
