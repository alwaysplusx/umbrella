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
import com.harmony.umbrella.core.ClassWrapper;
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

    public static final String JNDI_GLOBAL = "jndi.global.prefix";
    public static final String JNDI_ROOT = "jndi.format.root";
    public static final String JNDI_BEAN = "jndi.format.bean";
    public static final String JNDI_SEPARATOR = "jndi.format.separator";
    public static final String JNDI_REMOTE = "jndi.format.remote";

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

    protected ConfigManager configManager;

    public ConfigurationBeanResolver() {
        this(new Properties());
    }

    public ConfigurationBeanResolver(Properties properties) {
        this.configManager = new ConfigManagerImpl(properties);
    }

    public ConfigurationBeanResolver(String propertiesFileLocation) throws IOException {
        this(PropertiesUtils.loadProperties(propertiesFileLocation));
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
    public String[] guessNames(BeanDefinition bd) {
        return guessNames(bd, new HashMap<String, Object>());
    }

    @Override
    public String[] guessNames(BeanDefinition bd, Annotation ann) {
        Map<String, Object> properties = ann == null ? new HashMap<String, Object>() : AnnotationUtils.toMap(ann);
        return guessNames(bd, properties);
    }

    private static final String[] JNDI_NAME_KEY = { "lookup", "jndi", "jndiname" };

    private static final String[] BEAN_NAME_KEY = { "mappedName", "beanName" };

    @SuppressWarnings("rawtypes")
    protected String[] guessNames(BeanDefinition bd, Map<String, Object> properties) {
        List<String> result = new ArrayList<String>();
        Context context = null;
        try {
            context = getContext();
        } catch (NamingException e) {
        }

        for (String key : JNDI_NAME_KEY) {
            Object jndi = properties.get(key);
            if (jndi instanceof String && StringUtils.isNotBlank((String) jndi)) {
                if (!result.contains(jndi) && (context == null || tryLookup((String) jndi, context) != null)) {
                    result.add((String) jndi);
                }
            }
        }

        if (result.isEmpty()) {

            String globalPrefix = configManager.getProperty("jndi.global.prefix", "");

            if (!StringUtils.isBlank(globalPrefix) && !globalPrefix.endsWith("/")) {
                globalPrefix = globalPrefix + "/";
            }

            List<String> beanNames = new ArrayList<String>();
            for (String key : BEAN_NAME_KEY) {
                Object beanName = properties.get(key);
                if (beanName instanceof String && StringUtils.isNotBlank((String) beanName)) {
                    beanNames.add((String) beanName);
                }
            }

            // 没有配置则通过猜想得出
            if (beanNames.isEmpty()) {
                beanNames.addAll(guessBeanNames(bd));
            }

            Set<String> separators = configManager.getPropertySet(JNDI_SEPARATOR);
            // add default
            separators.add("#");
            separators.add("!");

            List<Class> remoteClasses = new ArrayList<Class>();
            if (bd.isRemoteClass()) {
                remoteClasses.add(bd.getRemoteClass());
            } else {
                remoteClasses.addAll(guessRemoteClass(bd));
            }

            for (String beanName : beanNames) {
                for (String separator : separators) {
                    for (Class remoteClass : remoteClasses) {
                        String jndi = new StringBuilder().append(globalPrefix).append(beanName).append(separator).append(remoteClass.getName()).toString();
                        if (!result.contains(jndi) && (context == null || tryLookup(jndi, context) != null)) {
                            result.add(jndi);
                        }
                    }
                }
            }
        }

        return result.toArray(new String[result.size()]);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected List<String> guessBeanNames(BeanDefinition bd) {
        if (bd.isSessionClass()) {
            return Arrays.asList(bd.getMappedName());
        }
        List<String> result = new ArrayList<String>();
        Class<?> remoteClass = bd.getRemoteClass();

        ClassWrapper cw = new ClassWrapper(remoteClass);
        for (Class clazz : cw.getAllSubClasses()) {
            BeanDefinition subBd = new BeanDefinition(clazz);
            if (subBd.isSessionClass()) {
                result.add(subBd.getMappedName());
            }
        }

        final String remoteName = remoteClass.getSimpleName();

        Set<String> remoteSuffix = configManager.getPropertySet(JNDI_REMOTE);
        remoteSuffix.add("Remote");
        remoteSuffix.remove("");

        String name = remoteName;
        for (String remote : remoteSuffix) {
            if (remoteName.endsWith(remote)) {
                name = remoteName.substring(0, remoteName.length() - remote.length());
                break;
            }
        }

        Set<String> beanSuffix = configManager.getPropertySet(JNDI_BEAN);
        beanSuffix.add("Bean");
        beanSuffix.add("");

        for (String bean : beanSuffix) {
            String beanName = name + bean;
            if (!result.contains(beanName)) {
                result.add(beanName);
            }
        }

        return result;
    }

    @SuppressWarnings("rawtypes")
    protected List<Class> guessRemoteClass(BeanDefinition bd) {
        if (bd.isRemoteClass()) {
            return Arrays.<Class> asList(bd.getRemoteClass());
        }
        return new ArrayList<Class>(bd.getAllRemoteClasses());
    }

    protected Object getConfigBean(BeanDefinition bd, Context context) {
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
            log.debug("test, it is declare bean? "//
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

}
