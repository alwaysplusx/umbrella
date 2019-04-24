package com.harmony.umbrella.context;

import static com.harmony.umbrella.context.WebXmlConstant.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;

import org.springframework.core.OrderComparator;
import org.springframework.util.ClassUtils;

import com.harmony.umbrella.context.listener.ApplicationDestroyListener;
import com.harmony.umbrella.context.listener.ApplicationEventListener;
import com.harmony.umbrella.context.listener.ApplicationListener;
import com.harmony.umbrella.context.listener.ApplicationStartListener;
import com.harmony.umbrella.util.ClassFilter;
import com.harmony.umbrella.util.ClassFilterFeature;

/**
 * @author wuxii@foxmail.com
 */
@HandlesTypes({
        ApplicationEventListener.class,
        ApplicationStartListener.class,
        ApplicationDestroyListener.class,
        ApplicationListener.class
})
public class ApplicationServletContainerInitializer implements ServletContainerInitializer {

    @Override
    public void onStartup(final Set<Class<?>> c, final ServletContext servletContext) throws ServletException {
        Set<Class<?>> classToUse = new HashSet<>();

        if (c != null && !c.isEmpty()) {
            classToUse.addAll(c);
        }

        ApplicationConfiguration cfg;
        try {
            cfg = (ApplicationConfiguration) servletContext.getAttribute(CONTAINER_CONTEXT_ATTRIBUTE_APP_CONFIG);
            if (cfg == null) {
                cfg = buildApplicationConfiguration(servletContext);
            }
        } catch (Exception e) {
            throw new ServletException("build application configuration failure!", e);
        }

        // init application static first
        ApplicationContext.start(cfg);

        // must after application started
        if (cfg.getBooleanProperty(APPLICATION_CFG_PROPERTIES_SCAN_HANDLES_TYPES) || ContextHelper.isWeblogic()) {
            classToUse.addAll(scanApplicationEventListeners());
        }

        if (classToUse.isEmpty()) {
            servletContext.log("No application initializer detected on classpath");
            return;
        }

        ApplicationServletContextListener applicationServletContextListener = buildApplicationServletContextListener(classToUse, cfg);

        servletContext.log("add dynamic servlet context listener " + applicationServletContextListener);
        servletContext.addListener(applicationServletContextListener);

    }

    private ApplicationConfiguration buildApplicationConfiguration(ServletContext servletContext) throws Exception {
        ApplicationConfigurationBuilder builder = null;
        String builderName = servletContext.getInitParameter(APPLICATION_CFG_PROPERTIES_CUSTOM_BUILDER);
        if (builderName != null) {
            Class<?> builderClass = ClassUtils.forName(builderName, ClassUtils.getDefaultClassLoader());
            builder = (ApplicationConfigurationBuilder) builderClass.newInstance();
        } else {
            builder = ApplicationConfigurationBuilder.newBuilder();
        }
        return builder.apply(servletContext).build();
    }

    private ApplicationServletContextListener buildApplicationServletContextListener(Set<Class<?>> classToUse, ApplicationConfiguration cfg)
            throws ServletException {
        ServletContext servletContext = cfg.getServletContext();
        ApplicationContext applicationContext = null;
        boolean autowire = cfg.getBooleanProperty(APPLICATION_CFG_PROPERTIES_LISTENER_AUTOWIRE);

        final List<ApplicationEventListener> eventListeners = new ArrayList<ApplicationEventListener>(classToUse.size());

        for (Class<?> cls : classToUse) {
            try {
                ApplicationEventListener listener = (ApplicationEventListener) cls.newInstance();
                if (autowire) {
                    if (applicationContext == null) {
                        applicationContext = ApplicationContext.getApplicationContext();
                    }
                    applicationContext.autowire(listener);
                }
                eventListeners.add(listener);
            } catch (Throwable e) {
                servletContext.log("Ignore initializer " + cls.getName() + ", " + e.toString());
            }
        }

        OrderComparator.sort(eventListeners);
        return new ApplicationServletContextListener(eventListeners, cfg);
    }

    /*
     * under weblogic just load @HandlesTypes in WEB-INF/lib/*.jar, so load @HandlesTypes manually
     */
    protected Set<Class<?>> scanApplicationEventListeners() throws ServletException {
        Set<Class<?>> result = new HashSet<>();

        Class<?>[] classes = ApplicationContext.getApplicationClasses(new ClassFilter() {

            @Override
            public boolean accept(Class<?> c) {
                return ClassFilterFeature.NEWABLE.accept(c)//
                        && !result.contains(c)//
                        && ApplicationEventListener.class.isAssignableFrom(c);
            }
        });

        Collections.addAll(result, classes);
        return result;
    }

    /**
     * 内部动态servlet context listener
     *
     * @author wuxii@foxmail.com
     */
    private static final class ApplicationServletContextListener implements ServletContextListener {

        private ApplicationConfiguration cfg;
        private List<ApplicationEventListener> eventListeners;

        public ApplicationServletContextListener(List<ApplicationEventListener> listeners, ApplicationConfiguration cfg) {
            this.cfg = cfg;
            this.eventListeners = listeners;
        }

        @Override
        public void contextInitialized(ServletContextEvent sce) {
            ServletContext servletContext = sce.getServletContext();
            long s = System.currentTimeMillis();
            servletContext.log("begin start application");
            for (int i = 0, max = eventListeners.size(); i < max; i++) {
                ApplicationEventListener listener = eventListeners.get(i);
                if (listener instanceof ApplicationStartListener) {
                    ((ApplicationStartListener) listener).onStartup(cfg);
                }
            }
            servletContext.log("application started, use " + (System.currentTimeMillis() - s) + "ms");
        }

        @Override
        public void contextDestroyed(ServletContextEvent sce) {
            ServletContext servletContext = sce.getServletContext();
            long s = System.currentTimeMillis();
            servletContext.log("begin stop application");
            for (int i = eventListeners.size() - 1; i >= 0; i--) {
                ApplicationEventListener listener = eventListeners.get(i);
                if (listener instanceof ApplicationDestroyListener) {
                    ((ApplicationDestroyListener) listener).onDestroy(cfg);
                }
            }
            if (ApplicationContext.isStarted()) {
                // shutdown application context
                ApplicationContext.stop();
            }
            servletContext.log("application stopped, use " + (System.currentTimeMillis() - s) + "ms");
        }

    }

}
