package com.harmony.umbrella.config;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import com.harmony.umbrella.config.annotation.Bean;
import com.harmony.umbrella.util.ReflectionUtils;

/**
 * 全局配置, 类似于Spring {@linkplain org.springframework.context.annotation.Configuration}
 * 
 * @author wuxii@foxmail.com
 */
public class ConfigSupport implements Configurations {

    private Method[] methods;

    public final <T> T getBean(Class<T> beanClass) {
        return getBean(beanClass.getName());
    }

    public final <T> List<T> getBeans(Class<T> beanClass) {
        return getBeans(beanClass.getName());
    }

    @SuppressWarnings("unchecked")
    @Override
    public final <T> T getBean(String beanName) {
        Object result = findTypedBean(beanName, Object.class);
        if (result == null) {
            throw new ConfigurationException("cannot found configuration bean " + beanName);
        }
        return (T) result;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public final <T> List<T> getBeans(String beanName) {
        List result = findTypedBean(beanName, List.class);
        if (result == null) {
            throw new ConfigurationException("cannot found configuration list bean " + beanName);
        }
        return result;
    }

    protected <T> T findTypedBean(String beanName, Class<T> type) {
        Object result = null;
        for (Method method : getMethods()) {
            if (isQualified(method, type)) {
                Bean ann = method.getAnnotation(Bean.class);
                if (ann != null) {
                    if (ann.value().length > 0) {
                        for (String v : ann.value()) {
                            if (v.equals(beanName)) {
                                result = ReflectionUtils.invokeMethod(method, this);
                            }
                        }
                    } else if (method.getName().equalsIgnoreCase(beanName)) {
                        result = ReflectionUtils.invokeMethod(method, this);
                        break;
                    }
                }
            }
        }
        return type.cast(result);
    }

    private final Method[] getMethods() {
        if (methods == null) {
            List<Method> temp = new ArrayList<Method>();
            for (Method method : getClass().getMethods()) {
                if (isQualified(method, Object.class)) {
                    temp.add(method);
                }
            }
            methods = temp.toArray(new Method[temp.size()]);
        }
        return methods;
    }

    private boolean isQualified(Method method, Class<?> requireType) {
        return method.getDeclaringClass() != Object.class //
                && Modifier.isPublic(method.getModifiers())//
                && !Modifier.isStatic(method.getModifiers())//
                && method.getParameterTypes().length == 0 //
                && requireType.isAssignableFrom(method.getReturnType());
    }

}
