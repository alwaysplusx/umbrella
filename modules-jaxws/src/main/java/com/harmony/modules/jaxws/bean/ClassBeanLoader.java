package com.harmony.modules.jaxws.bean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ClassBeanLoader implements BeanLoader, Serializable {

    private static final long serialVersionUID = 1L;
    private Map<Class<?>, Object> beans = new HashMap<Class<?>, Object>();

    @SuppressWarnings("unchecked")
    @Override
    public <T> T loadBean(Class<T> beanClass) {
        if (!beans.containsKey(beanClass)) {
            beans.put(beanClass, newBean(beanClass, null));
        }
        return (T) beans.get(beanClass);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T loadBean(Class<T> beanClass, String scope) {
        if (SINGLETON.equals(scope)) {
            return loadBean(beanClass);
        } else if (PROTOTYPE.equals(scope)) {
            return (T) newBean(beanClass, null);
        } else {
            throw new IllegalArgumentException("unsupport scope " + scope);
        }
    }

    private Object newBean(Class<?> beanClass, Map<String, Object> properties) {
        try {
            return beanClass.newInstance();
        } catch (InstantiationException e) {
            throw new IllegalArgumentException(e);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }
    }
}