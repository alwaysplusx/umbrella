package com.harmony.umbrella.web.context;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.OrderComparator;
import org.springframework.web.WebApplicationInitializer;

import com.harmony.umbrella.context.ApplicationConfiguration;
import com.harmony.umbrella.context.ApplicationConfigurationBuilder;
import com.harmony.umbrella.context.ApplicationContext;
import com.harmony.umbrella.context.ContextHelper;
import com.harmony.umbrella.context.listener.ApplicationDestroyListener;
import com.harmony.umbrella.context.listener.ApplicationEventListener;
import com.harmony.umbrella.context.listener.ApplicationStartListener;
import com.harmony.umbrella.util.ClassFilter;
import com.harmony.umbrella.util.ClassFilterFeature;

/**
 * @author wuxii@foxmail.com
 */
public class WebApplicationContextInitializer implements WebApplicationInitializer {

    @Autowired
    private ApplicationConfigurationBuilder applicationConfigurationBuilder;
    private ServletContext servletContext;

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        this.servletContext = servletContext;
        ApplicationConfiguration cfg = applicationConfigurationBuilder.build();
        ApplicationContext.start(cfg);
        Set<Class<?>> c = scanApplicationEventListener();

        if (c.isEmpty()) {
            servletContext.log("No application ApplicationInitializer types detected on classpath");
            return;
        }

        final List<ApplicationEventListener> eventListeners = new ArrayList<ApplicationEventListener>(c.size());
        for (Class<?> cls : c) {
            try {
                eventListeners.add((ApplicationEventListener) cls.newInstance());
            } catch (Throwable e) {
                throw new ServletException("Failed to instantiate ApplicationInitializer class", e);
            }
        }

        OrderComparator.sort(eventListeners);

        servletContext.addListener(new ServletContextListener() {

            @Override
            public void contextInitialized(ServletContextEvent sce) {
                for (int i = 0, max = eventListeners.size(); i < max; i++) {
                    ApplicationEventListener listener = eventListeners.get(i);
                    if (listener instanceof ApplicationStartListener) {
                        ((ApplicationStartListener) listener).onStartup(cfg);
                    }
                }
            }

            @Override
            public void contextDestroyed(ServletContextEvent sce) {
                for (int i = eventListeners.size() - 1; i >= 0; i--) {
                    ApplicationEventListener listener = eventListeners.get(i);
                    if (listener instanceof ApplicationDestroyListener) {
                        ((ApplicationDestroyListener) listener).onDestroy(cfg);
                    }
                }
                ApplicationContext.shutdown();
            }

        });
    }

    public ApplicationConfigurationBuilder getApplicationConfigurationBuilder() {
        return applicationConfigurationBuilder;
    }

    public void setApplicationConfigurationBuilder(ApplicationConfigurationBuilder applicationConfigurationBuilder) {
        this.applicationConfigurationBuilder = applicationConfigurationBuilder;
    }

    private String getInitParameter(String name) {
        return servletContext.getInitParameter(name);
    }

    protected Set<Class<?>> scanApplicationEventListener() throws ServletException {
        // under weblogic just load @HandlesTypes in WEB-INF/lib/*.jar, so load @HandlesTypes manually
        Set<Class<?>> result = new HashSet<>();
        if (Boolean.valueOf(getInitParameter("scan-handles-types")) || ContextHelper.isWeblogic()) {
            ApplicationContext.getApplicationClasses(new ClassFilter() {
                @Override
                public boolean accept(Class<?> clazz) {
                    if (ClassFilterFeature.NEWABLE.accept(clazz)//
                            && !result.contains(clazz)//
                            && ApplicationEventListener.class.isAssignableFrom(clazz))
                        result.add(clazz);
                    return false;
                }
            });
        }
        return result;
    }

}
