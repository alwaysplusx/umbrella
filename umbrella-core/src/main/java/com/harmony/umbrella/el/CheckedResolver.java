package com.harmony.umbrella.el;

import com.harmony.umbrella.util.GenericUtils;

/**
 * @author wuxii@foxmail.com
 */
public abstract class CheckedResolver<T> implements TypedResolver, Comparable<CheckedResolver<?>> {

    private Class<T> type;
    private int priority;

    public CheckedResolver(int priority) {
        this(null, priority);
    }

    public CheckedResolver(Class<T> type, int priority) {
        this.type = type;
        this.priority = priority;
    }

    public abstract boolean support(String name, Object obj);

    protected abstract Object doResolve(String name, T obj);

    public int priority() {
        return priority;
    }

    @SuppressWarnings("unchecked")
    public Class<T> getSupportType() {
        if (type == null) {
            type = (Class<T>) GenericUtils.getTargetGeneric(getClass(), CheckedResolver.class, 0);
        }
        return type;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object resolve(String name, Object obj) {
        return doResolve(name, (T) obj);
    }

    @Override
    public int compareTo(CheckedResolver<?> o) {
        return (priority < o.priority) ? -1 : ((priority == o.priority) ? 0 : 1);
    }
}
