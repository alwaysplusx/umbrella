package com.harmony.umbrella.ee.support;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.springframework.core.annotation.AnnotationUtils;

import com.harmony.umbrella.core.BeansException;
import com.harmony.umbrella.ee.BeanDefinition;
import com.harmony.umbrella.ee.BeanResolver;
import com.harmony.umbrella.ee.SessionBean;
import com.harmony.umbrella.ee.formatter.JndiFormatter;
import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;
import com.harmony.umbrella.util.StringUtils;

/**
 * @author wuxii@foxmail.com
 */
public abstract class AbstractBeanResolver implements BeanResolver {

    private static final Log log = Logs.getLog(AbstractBeanResolver.class);

    protected Properties contextProperties = new Properties();

    protected boolean forced;

    protected boolean testForced;

    protected JndiFormatter jndiFormatter;

    protected abstract boolean guessNames(BeanDefinition bd, Map<String, Object> properties, final JndiHolder holder);

    /*
     * 配置优先的原则
     */
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
        JndiHolder holder = new JndiHolder(context, forced, testForced);
        if (findInProperties(bd, properties, holder)) {
            return holder.getJndis();
        }
        if (guessNames(bd, properties, holder) || holder.test()) {
            return holder.getJndis();
        }
        log.warn("can't find any jndi for {}", bd);
        return new String[0];
    }

    protected boolean findInProperties(BeanDefinition bd, Map<String, Object> properties, JndiHolder holder) {
        if (properties.isEmpty()) {
            return false;
        }
        // 配置属性直接存在jndi名称无需猜测直接返回jndi
        for (String attr : getJndiAttributes()) {
            Object jndi = properties.get(attr);
            if (jndi != null && jndi instanceof String && StringUtils.isNotBlank((String) jndi)) {
                if (!holder.addIfAbsent((String) jndi)) {
                    log.warn("配置中的jndi[{}]无效", jndi);
                }
            }
        }
        // 找到了配置属性中的jndi全名
        if (!holder.isJndiEmpty()) {
            return true;
        }
        // 配置属性中的beanName
        Collection<String> beanNames = new HashSet<String>();
        for (String attr : getBeanNameAttributes()) {
            Object beanName = properties.get(attr);
            if (beanName != null && beanName instanceof String && StringUtils.isNotBlank((String) beanName)) {
                beanNames.add((String) beanName);
            }
        }

        // 配置属性中存在就不再允许添加新的beanName了
        if (!beanNames.isEmpty()) {
            holder.addBeanName(beanNames);
            holder.acceptBeanName = false;
        }

        // 配置属性中的beanInterface
        Collection<Class> beanInterfaces = new HashSet<Class>();
        for (String attr : getBeanInterfaceAttributes()) {
            Object beanInterface = properties.get(attr);
            if (beanInterface != null && beanInterface instanceof Class && ((Class) beanInterface).isInterface()) {
                beanInterfaces.add((Class) beanInterface);
            }
        }

        // 配置属性中存在就不在允许添加新的beanInterface了
        if (!beanInterfaces.isEmpty()) {
            holder.addBeanInterface(beanInterfaces);
            holder.acceptBeanInterface = false;
        }

        return holder.test();
    }

    protected abstract Collection<String> getJndiAttributes();

    protected abstract Collection<String> getBeanNameAttributes();

    protected abstract Collection<String> getBeanInterfaceAttributes();

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
                properties.putAll(AnnotationUtils.getAnnotationAttributes(a));
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

    public JndiFormatter getJndiFormatter() {
        return jndiFormatter;
    }

    public void setJndiFormatter(JndiFormatter jndiFormatter) {
        this.jndiFormatter = jndiFormatter;
    }

    public boolean isForced() {
        return forced;
    }

    public void setForced(boolean forced) {
        this.forced = forced;
    }

    public boolean isTestForced() {
        return testForced;
    }

    public void setTestForced(boolean testForced) {
        this.testForced = testForced;
    }

    protected class JndiHolder {

        private boolean forced;

        private boolean testForced;

        private Context context;

        private boolean acceptBeanName = true;

        private boolean acceptBeanInterface = true;

        private Collection<String> beanNames = new HashSet<String>();

        private Collection<Class> beanInterfaces = new HashSet<Class>();

        private List<String> jndis = new ArrayList<String>();

        private JndiHolder(Context context, boolean forced, boolean testForced) {
            this.context = context;
            this.forced = forced;
            this.testForced = testForced;
        }

        public boolean test() {
            if (context == null && testForced) {
                return false;
            }
            Collection<String> jndiNames = getJndiFormatter().format(beanNames, beanInterfaces);
            for (String jndi : jndiNames) {
                addIfAbsent(jndi, testForced);
            }
            return !jndis.isEmpty();
        }

        public boolean addIfAbsent(String jndi) {
            return addIfAbsent(jndi, forced);
        }

        public boolean addIfAbsent(String jndi, boolean forced) {
            if (!jndis.contains(jndi) && (!forced || exists(jndi))) {
                return jndis.add(jndi);
            }
            return false;
        }

        public boolean addBeanName(Collection<String> beanName) {
            return addBeanName(beanName.toArray(new String[beanName.size()]));
        }

        public boolean addBeanInterface(Collection<Class> beanInterface) {
            return addBeanInterface(beanInterface.toArray(new Class[beanInterface.size()]));
        }

        public boolean addBeanName(String... beanName) {
            if (acceptBeanName) {
                Collections.addAll(beanNames, beanName);
                return true;
            }
            return false;
        }

        public boolean addBeanInterface(Class<?>... beanInterface) {
            if (acceptBeanInterface) {
                Collections.addAll(beanInterfaces, beanInterface);
                return true;
            }
            return false;
        }

        public boolean isBeanNameEmpty() {
            return beanNames.isEmpty();
        }

        public boolean isBeanInterfaceEmpty() {
            return beanInterfaces.isEmpty();
        }

        public boolean isJndiEmpty() {
            return jndis.isEmpty();
        }

        private boolean exists(String jndi) {
            return context != null && tryLookup(jndi, context) != null;
        }

        public String[] getJndis() {
            String[] result = jndis.toArray(new String[jndis.size()]);
            Arrays.sort(result);
            return result;
        }

        public Context getContext() {
            return context;
        }

        public boolean isAcceptBeanName() {
            return acceptBeanName;
        }

        public boolean isAcceptBeanInterface() {
            return acceptBeanInterface;
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
