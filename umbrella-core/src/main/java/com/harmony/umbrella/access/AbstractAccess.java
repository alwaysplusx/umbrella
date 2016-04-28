package com.harmony.umbrella.access;

/**
 * @author wuxii@foxmail.com
 */
public abstract class AbstractAccess implements Access {

    @Override
    public Class<?> getType(String name, Object target) {
        accessible(name, target);
        Object value = getNameValue(name, target);
        return value != null ? value.getClass() : null;
    }

    @Override
    public Object get(String name, Object target) {
        accessible(name, target);
        return getNameValue(name, target);
    }

    @Override
    public void set(String name, Object target, Object value) {
        accessible(name, target);
        setNameValue(name, target, value);
    }

    private void accessible(String name, Object target) {
        if (!isAccessible(name, target)) {
            throw new IllegalArgumentException(target + " " + name + " not accessible");
        }
    }

    protected abstract Object getNameValue(String name, Object target);

    protected abstract void setNameValue(String name, Object target, Object value);
}
