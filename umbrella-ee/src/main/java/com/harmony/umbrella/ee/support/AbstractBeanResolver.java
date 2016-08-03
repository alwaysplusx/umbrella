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
import com.harmony.umbrella.ee.BeanResolver;
import com.harmony.umbrella.ee.SessionBean;
import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;
import com.harmony.umbrella.util.AnnotationUtils;
import com.harmony.umbrella.util.StringUtils;

/**
 * @author wuxii@foxmail.com
 */
public abstract class AbstractBeanResolver implements BeanResolver {

    private static final Log log = Logs.getLog(AbstractBeanResolver.class);

    protected Properties contextProperties = new Properties();

    private boolean forced;

    protected abstract String[] guessNames(BeanDefinition bd, Map<String, Object> properties, Context context);

    private String[] doGuess(BeanDefinition bd, Map<String, Object> properties, Context context) {
        if (properties == null) {
            properties = new HashMap<String, Object>();
        }
        if (context == null) {
            try {
                context = getContext();
            } catch (NamingException e) {
                log.error("failed initialize java naming context", e);
            }
        }
        return guessNames(bd, properties, context);
    }

    protected boolean findInProperties(BeanDefinition bd, Map<String, Object> properties, JndiHolder holder) {
        // 配置属性直接存在jndi名称无需猜测直接返回jndi
        for (String attr : getJndiAttributes()) {
            Object jndi = properties.get(attr);
            if (jndi != null && jndi instanceof String && StringUtils.isNotBlank((String) jndi)) {
                if (!holder.addIfAbsent((String) jndi)) {
                    log.warn("配置中的jndi[{}]无效", jndi);
                }
            }
        }
        return !holder.isEmpty();
    }

    protected abstract List<String> getJndiAttributes();

    protected abstract List<String> getBeanNameAttributes();

    protected abstract List<String> getBeanInterfaceAttributes();

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
        return doGuess(bd, properties, null);
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
            String[] jndis = doGuess(bd, properties, context);
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
            log.debug("Test it is declare bean? "//
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

    public boolean isForced() {
        return forced;
    }

    public void setForced(boolean forced) {
        this.forced = forced;
    }

    protected class JndiHolder {

        private boolean forced;
        private Context context;

        private List<String> jndis = new ArrayList<String>();

        public JndiHolder(Context context, boolean forced) {
            this.context = context;
            this.forced = forced;
        }

        public boolean addIfAbsent(String jndi) {
            if (!jndis.contains(jndi) && (!forced || exists(jndi))) {
                return jndis.add(jndi);
            }
            return false;
        }

        public boolean exists(String jndi) {
            return tryLookup(jndi, context) != null;
        }

        public String[] getJndis() {
            String[] result = jndis.toArray(new String[jndis.size()]);
            Arrays.sort(result);
            return result;
        }

        public boolean isEmpty() {
            return jndis.isEmpty();
        }

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
