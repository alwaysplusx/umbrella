package com.harmony.umbrella.context.ee.support;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.harmony.umbrella.context.ee.BeanDefinition;
import com.harmony.umbrella.context.ee.BeanResolver;
import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;
import com.harmony.umbrella.util.AnnotationUtils;
import com.harmony.umbrella.util.PropertiesUtils;
import com.harmony.umbrella.util.ReflectionUtils;
import com.harmony.umbrella.util.StringUtils;

/**
 * 通过配置配置的属性，来组合定义的bean
 *
 * @author wuxii@foxmail.com
 */
public class ConfigurationBeanResolver implements BeanResolver {

    private static final String[] JNDI_NAME_KEY = { "lookup", "jndi", "jndiname" };

    private static final String[] BEAN_NAME_KEY = { "mappedName", "beanName" };

    private static final Log log = Logs.getLog(ConfigurationBeanResolver.class);

    private static final List<String> CONTEXT_PROPERTIES = new ArrayList<String>();

    static {
        Field[] fields = Context.class.getFields();
        for (Field field : fields) {
            if (ReflectionUtils.isPublicStaticFinal(field)) {
                CONTEXT_PROPERTIES.add((String) ReflectionUtils.getFieldValue(field, Context.class));
            }
        }
    }

    private PartResolver<String> beanNamePartResolver;

    @SuppressWarnings("rawtypes")
    private PartResolver<Class> beanInterfacePartResolver;

    private JndiFormatter jndiFormatter;

    protected ConfigManager configManager;

    public ConfigurationBeanResolver() {
        this(new Properties());
    }

    public ConfigurationBeanResolver(String propertiesFileLocation) throws IOException {
        this(PropertiesUtils.loadProperties(propertiesFileLocation));
    }

    public ConfigurationBeanResolver(Properties properties) {
        this.configManager = new ConfigManagerImpl(properties);
        this.beanNamePartResolver = new BeanNamePartResolver(this.configManager);
        this.beanInterfacePartResolver = new BeanInterfacePartResolver(this.configManager);
        this.jndiFormatter = new PatternJndiFormatter(this.configManager);
    }

    @Override
    public Context getContext() throws NamingException {
        return new InitialContext(getContextProperties());
    }

    /**
     * {@linkplain javax.naming.Context}所需要的属性集合
     * 
     * @return naming context所需要的属性集合
     */
    protected Properties getContextProperties() {
        Properties result = new Properties();
        for (String name : CONTEXT_PROPERTIES) {
            String property = configManager.getProperty(name);
            if (StringUtils.isNotBlank(property)) {
                result.setProperty(name, property);
            }
        }
        return result;
    }

    @Override
    public Object[] guessBeans(BeanDefinition bd) {
        return guessBeans(bd, null);
    }

    @Override
    public Object[] guessBeans(BeanDefinition bd, Annotation ann) {
        List<Object> beans = new ArrayList<Object>();
        try {
            Context context = getContext();

            // add config bean first
            Object bean = getConfigBean(bd, context);
            if (bean != null) {
                beans.add(bean);
            }

            String[] jndis = guessNames(bd, ann);
            for (String jndi : jndis) {
                bean = tryLookup(jndi, context);
                if (isDeclareBean(bd, bean)) {
                    beans.add(bean);
                    bind(bd, jndi);
                }
            }
        } catch (NamingException e) {
            ReflectionUtils.rethrowRuntimeException(e);
        }
        return beans.toArray();
    }

    @Override
    public String[] guessNames(Class<?> clazz) {
        return guessNames(new BeanDefinition(clazz), new HashMap<String, Object>());
    }

    @Override
    public String[] guessNames(BeanDefinition bd) {
        return guessNames(bd, new HashMap<String, Object>());
    }

    @Override
    public String[] guessNames(BeanDefinition bd, Annotation ann) {
        Map<String, Object> properties = (ann == null) ? new HashMap<String, Object>() : AnnotationUtils.toMap(ann);
        return guessNames(bd, properties);
    }

    @SuppressWarnings("rawtypes")
    protected String[] guessNames(BeanDefinition bd, Map<String, Object> properties) {
        Context context = null;
        try {
            context = getContext();
        } catch (NamingException e) {
            log.warn("", e);
        }
        JndiHolder jndiHolder = new JndiHolder(context);

        for (String key : JNDI_NAME_KEY) {
            Object jndi = properties.get(key);
            if (jndi instanceof String && StringUtils.isNotBlank((String) jndi)) {
                jndiHolder.addIfAbsent((String) jndi);
            }
        }

        if (jndiHolder.isEmpty()) {

            Set<String> beanNames = beanNamePartResolver.resolve(bd);
            // 如果配置属性中有配置mappedName也添加到其中
            for (String key : BEAN_NAME_KEY) {
                Object beanName = properties.get(key);
                if (beanName instanceof String && StringUtils.isNotBlank((String) beanName)) {
                    beanNames.add((String) beanName);
                }
            }

            Set<Class> beanInterfaces = beanInterfacePartResolver.resolve(bd);

            Set<String> jndis = jndiFormatter.format(beanNames, beanInterfaces);

            for (String jndi : jndis) {
                jndiHolder.addIfAbsent(jndi);
            }
        }

        return jndiHolder.getJndis();
    }

    private Object getConfigBean(BeanDefinition bd, Context context) {
        final Class<?> clazz = bd.isRemoteClass() ? bd.getRemoteClass() : bd.getBeanClass();
        String jndi = configManager.getJndi(clazz);
        if (jndi != null) {
            Object bean = tryLookup(jndi, context);
            if (isDeclareBean(bd, bean)) {
                return bean;
            }
            unbind(clazz);
        }
        return null;
    }

    protected final Object tryLookup(String jndi, Context context) {
        try {
            return context.lookup(jndi);
        } catch (NamingException e) {
            return null;
        }
    }

    protected boolean isDeclareBean(final BeanDefinition bd, final Object bean) {
        if (bean == null) {
            return false;
        }
        Class<?> remoteClass = bd.getRemoteClass();
        if (log.isDebugEnabled()) {
            log.debug(
                    "test, it is declare bean? "//
                            + "\n\tdeclare remoteClass -> {}" //
                            + "\n\tdeclare beanClass   -> {}" //
                            + "\n\tactual  bean        -> {}", //
                    remoteClass, bd.getBeanClass(), bean);
        }
        return bd.getBeanClass().isInstance(bean) || (remoteClass != null && remoteClass.isInstance(bean));
    }

    protected void unbind(Class<?> clazz) {
        configManager.removeJndi(clazz);
    }

    protected void bind(BeanDefinition beanDefinition, String jndi) {
        configManager.setJndi(beanDefinition.getBeanClass(), jndi);
        configManager.setJndi(beanDefinition.getRemoteClass(), jndi);
    }

    protected class JndiHolder {

        private boolean forced;
        private Context context;

        private List<String> jndis = new ArrayList<String>();

        public JndiHolder(Context context) {
            this.context = context;
        }

        public void addIfAbsent(String jndi) {
            if (!jndis.contains(jndi) && (exists(jndi) || !forced)) {
                jndis.add(jndi);
            }
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

}
