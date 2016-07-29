package com.harmony.umbrella.ee.support;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.harmony.umbrella.beans.BeansException;
import com.harmony.umbrella.ee.BeanDefinition;
import com.harmony.umbrella.ee.BeanNameResolver;
import com.harmony.umbrella.ee.SessionBean;
import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;
import com.harmony.umbrella.util.AnnotationUtils;

/**
 * @author wuxii@foxmail.com
 */
public abstract class AbstractBeanNameResolver implements BeanNameResolver {

    protected Properties contextProperties = new Properties();

    private static final Log log = Logs.getLog(AbstractBeanNameResolver.class);

    protected abstract String[] guessNames(BeanDefinition bd, Map<String, Object> properties, Context context);

    @Override
    public Context getContext() throws NamingException {
        return new InitialContext(contextProperties);
    }

    @Override
    public String[] guessNames(BeanDefinition bd) {
        return guessNames(bd, (Map<String, Object>) null);
    }

    @Override
    public String[] guessNames(BeanDefinition bd, Annotation... ann) {
        return guessNames(bd, annToMap(ann));
    }

    @Override
    public String[] guessNames(BeanDefinition bd, Map<String, Object> properties) {
        return guessNames(bd, properties, null);
    }

    @Override
    public SessionBean[] guessBeans(BeanDefinition bd) {
        return guessBeans(bd, (Map<String, Object>) null);
    }

    @Override
    public SessionBean[] guessBeans(BeanDefinition bd, Annotation... ann) {
        return guessBeans(bd, annToMap(ann));
    }

    @Override
    public SessionBean[] guessBeans(BeanDefinition bd, Map<String, Object> properties) {
        List<SessionBean> result = new ArrayList<SessionBean>();
        try {
            Context context = getContext();
            String[] jndis = guessNames(bd, properties, context);
            for (String jndi : jndis) {
                Object bean = tryLookup(jndi, context);
                if (bean != null && isDeclareBean(bd, bean)) {
                    result.add(new SessionBeanImpl(bd, jndi, bean));
                }
            }
        } catch (NamingException e) {
            throw new BeansException(e);
        }
        return result.toArray(new SessionBean[result.size()]);
    }

    protected boolean isDeclareBean(final BeanDefinition bd, final Object bean) {
        if (log.isDebugEnabled()) {
            log.debug(
                    "Test it is declare bean? "//
                            + "\n\tdeclare remoteClass -> {}" //
                            + "\n\tdeclare beanClass   -> {}" //
                            + "\n\tactual  bean        -> {}", //
                    Arrays.asList(bd.getRemoteClasses()), bd.getBeanClass(), bean);
        }
        if (bean == null) {
            return false;
        }
        if (bd.getBeanClass().isInstance(bean)) {
            return true;
        }
        Class<?>[] remoteClasses = bd.getRemoteClasses();
        for (Class<?> c : remoteClasses) {
            if (c.isInstance(bean)) {
                return true;
            }
        }
        return false;
    }

    protected final Object tryLookup(String jndi, Context context) {
        try {
            return context.lookup(jndi);
        } catch (NamingException e) {
            return null;
        }
    }

    private Map<String, Object> annToMap(Annotation[] ann) {
        Map<String, Object> properties = null;
        if (ann != null && ann.length > 0) {
            properties = new HashMap<String, Object>();
            for (Annotation a : ann) {
                properties.putAll(AnnotationUtils.toMap(a));
            }
        }
        return properties;
    }

    public Properties getContextProperties() {
        return contextProperties;
    }

    public void setContextProperties(Properties contextProperties) {
        this.contextProperties = contextProperties;
    }

    static final class SessionBeanImpl implements SessionBean {

        BeanDefinition beanDefinition;
        Object bean;
        String jndi;
        boolean cacheable;
        boolean wrapped;

        public SessionBeanImpl(BeanDefinition bd, String jndi, Object bean) {
            this.beanDefinition = bd;
            this.jndi = jndi;
            this.bean = bean;
        }

        public SessionBeanImpl(BeanDefinition bd, String jndi, Object bean, boolean cachedable, boolean wrapped) {
            this.beanDefinition = bd;
            this.jndi = jndi;
            this.bean = bean;
            this.cacheable = cachedable;
            this.wrapped = wrapped;
        }

        @Override
        public Object getBean() {
            return bean;
        }

        @Override
        public String getJndi() {
            return jndi;
        }

        @Override
        public boolean isCacheable() {
            return cacheable;
        }

        @Override
        public boolean isWrapped() {
            return wrapped;
        }

        @Override
        public BeanDefinition getBeanDefinition() {
            return beanDefinition;
        }

    }

}
