package com.harmony.umbrella.context.ee.util;

import java.util.Arrays;
import java.util.List;

import com.harmony.umbrella.context.ee.support.WrappedBeanHandler;
import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;
import com.harmony.umbrella.util.ClassUtils;

/**
 * @author wuxii@foxmail.com
 */
public class WeblogicWrappedBeanHandler implements WrappedBeanHandler {

    private static final Log log = Logs.getLog(WeblogicWrappedBeanHandler.class);
    private static final List<String> wrappedClassName = Arrays.asList("weblogic.ejb.container.internal.SessionEJBContextImpl");

    @Override
    public Object unwrap(Object bean) {
        try {
            return bean.getClass().getMethod("getBean").invoke(bean);
        } catch (Exception e) {
            log.warn("not weblogic wrap bean");
        }
        return null;
    }

    @Override
    public boolean isWrappedBean(Object bean) {
        Class<?> beanClass = bean.getClass();
        for (String className : wrappedClassName) {
            if (beanClass.getName().equals(className)) {
                return true;
            }
            try {
                Class<?> wrappedBeanClass = ClassUtils.forName(className);
                if (ClassUtils.isAssignable(wrappedBeanClass, beanClass)) {
                    return true;
                }
            } catch (Throwable e) {
            }
        }
        return false;
    }
}
