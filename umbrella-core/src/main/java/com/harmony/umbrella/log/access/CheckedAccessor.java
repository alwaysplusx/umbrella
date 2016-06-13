package com.harmony.umbrella.log.access;

import com.harmony.umbrella.util.GenericUtils;

/**
 * @author wuxii@foxmail.com
 */
public abstract class CheckedAccessor<T> implements TypedAccessor<T> {

    private Class<T> type;

    public CheckedAccessor(Class<T> type) {
        this.type = type;
    }

    public abstract boolean support(String name);

    @SuppressWarnings("unchecked")
    @Override
    public Class<T> getType() {
        if (type == null) {
            type = (Class<T>) GenericUtils.getTargetGeneric(getClass(), CheckedAccessor.class, 0);
        }
        return type;
    }

    @Override
    public int compareTo(TypedAccessor<?> o) {
        Class<T> t1 = getType();
        Class<?> t2 = o.getType();
        return (!t1.isAssignableFrom(t2) && t1.equals(t2)) ? 0 : ((t1.isAssignableFrom(t2)) ? 1 : -1);
    }

}
