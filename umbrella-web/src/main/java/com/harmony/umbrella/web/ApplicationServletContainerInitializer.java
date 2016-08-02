package com.harmony.umbrella.web;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;

import com.harmony.umbrella.core.annotation.Order;

/**
 * @author wuxii@foxmail.com
 */
@HandlesTypes(WebApplicationInitializer.class)
public class ApplicationServletContainerInitializer implements ServletContainerInitializer {

    @Override
    public void onStartup(Set<Class<?>> c, ServletContext servletContext) throws ServletException {
        List<WebApplicationInitializer> initializers = new ArrayList<WebApplicationInitializer>();
        if (c != null && c.size() > 0) {
            for (Class<?> cls : c) {
                if (!cls.isInterface() //
                        && !Modifier.isAbstract(cls.getModifiers()) //
                        && WebApplicationInitializer.class.isAssignableFrom(cls)) {
                    try {
                        initializers.add((WebApplicationInitializer) cls.newInstance());
                    } catch (Throwable e) {
                        throw new ServletException("Failed to instantiate WebApplicationInitializer class", e);
                    }
                }
            }
        }

        if (initializers.isEmpty()) {
            servletContext.log("No application WebApplicationInitializer types detected on classpath");
            return;
        }

        Collections.sort(initializers, new Comparator<WebApplicationInitializer>() {

            @Override
            public int compare(WebApplicationInitializer o1, WebApplicationInitializer o2) {
                Order ann1 = o1.getClass().getAnnotation(Order.class);
                Order ann2 = o2.getClass().getAnnotation(Order.class);
                return (ann1 == null || ann2 == null) ? 0 : ann1.value() > ann2.value() ? 1 : ann1.value() == ann2.value() ? 0 : -1;
            }

        });

        for (WebApplicationInitializer initializer : initializers) {
            initializer.onStartup(servletContext);
        }
    }

}
