package com.harmony.umbrella.ws.proxy;

import java.util.Collection;

import javax.xml.ws.Holder;

import com.harmony.umbrella.util.Assert;

/**
 * @author wuxii@foxmail.com
 */
public abstract class AbstractResult implements Result {

    private final Object result;
    private boolean wrapped;

    public AbstractResult(Object result) {
        this(result, false);
    }

    public AbstractResult(Object result, boolean wrapped) {
        this.result = result;
        this.wrapped = wrapped;
    }

    @Override
    public boolean isWrapped() {
        return wrapped;
    }

    @Override
    public Object getResult() {
        return result;
    }

    @Override
    public <T> T unwrap(Class<T> requiredType) {
        Assert.notNull(requiredType, "required type must not be null");
        if (result == null) {
            return null;
        }
        return find(this.result, requiredType);
    }

    @SuppressWarnings("rawtypes")
    public <T> T find(Object obj, Class<T> requiredType) {
        Assert.notNull(requiredType, "required type must not be null");
        if (obj == null) {
            return null;
        } else if (obj instanceof Collection) {
            return findInCollection((Collection) obj, requiredType);
        }
        return findInObject(obj, requiredType);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private <T> T findInObject(Object obj, Class<T> requiredType) {
        if (obj == null) {
            return null;
        } else if (requiredType.isAssignableFrom(obj.getClass())) {
            return (T) obj;
        } else if (obj instanceof Holder && ((Holder) obj).value != null) {
            return find(((Holder) obj).value, requiredType);
        }
        return null;
    }

    @SuppressWarnings("rawtypes")
    private <T> T findInCollection(Collection c, Class<T> requiredType) {
        T result = null;
        for (Object obj : c) {
            result = find(obj, requiredType);
            if (result != null) {
                break;
            }
        }
        return result;
    }
}
