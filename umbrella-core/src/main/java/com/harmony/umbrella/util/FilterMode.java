package com.harmony.umbrella.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author wuxii@foxmail.com
 */
public abstract class FilterMode<R, T> {

    protected Set<T> includes;
    protected Set<T> excludes;

    public boolean accept(R resource) {
        return isEmpty(includes)//
                ? isEmpty(excludes) || !isMatched(resource, excludes)//
                : isMatched(resource, includes) && (isEmpty(excludes) || !isMatched(resource, excludes));
    }

    protected abstract boolean isMatched(R resource, Set<T> patterns);

    protected boolean isEmpty(Set<T> s) {
        return s == null || s.isEmpty();
    }

    public Set<T> getIncludes() {
        return includes;
    }

    public void setIncludes(Collection<T> includes) {
        this.includes = new HashSet<>(includes);
    }

    public Set<T> getExcludes() {
        return excludes;
    }

    public void setExcludes(Collection<T> excludes) {
        this.excludes = new HashSet<>(excludes);
    }

    public void addIncludes(Collection<T> includes) {
        if (this.includes == null) {
            this.includes = new HashSet<>();
        }
        this.includes.addAll(includes);
    }

    public void addIncludes(T... includes) {
        addIncludes(Arrays.asList(includes));
    }

    public void addExcludes(Collection<T> includes) {
        if (this.excludes == null) {
            this.excludes = new HashSet<>();
        }
        this.excludes.addAll(includes);
    }

    public void addExcludes(T... excludes) {
        addExcludes(Arrays.asList(excludes));
    }

}
