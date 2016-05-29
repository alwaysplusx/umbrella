package com.harmony.umbrella.context.ee.support;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.harmony.umbrella.context.ee.BeanDefinition;
import com.harmony.umbrella.context.ee.BeanFilter;
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
@SuppressWarnings("rawtypes")
public class ConfigurationBeanResolver implements BeanResolver {

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
    public Object guessBean(BeanDefinition beanDefinition) {
        return guessBean(beanDefinition, null, null);
    }

    @Override
    public Object guessBean(BeanDefinition beanDefinition, Annotation ann) {
        return guessBean(beanDefinition, ann, null);
    }

    @Override
    public Object guessBean(BeanDefinition beanDefinition, BeanFilter filter) {
        return guessBean(beanDefinition, null, filter);
    }

    @Override
    public Object guessBean(BeanDefinition beanDefinition, Annotation ann, BeanFilter filter) {
        String[] jndis = guessNames(beanDefinition, ann);
        for (String jndi : jndis) {
            Object bean = tryLookup(jndi);
            if (isDeclareBean(beanDefinition, bean)) {
                bind(jndi, beanDefinition);
                if (filter == null || filter.accept(jndi, bean)) {
                    return bean;
                }
            }
        }
        return null;
    }

    public String[] guessNames(BeanDefinition beanDefinition, Annotation ann) {
        Map<String, Object> properties = (ann == null) ? Collections.<String, Object> emptyMap() : AnnotationUtils.toMap(ann);
        return new RemoteJndiGuesser(beanDefinition, properties).guess();
    }

    private void bind(String jndi, BeanDefinition beanDefinition) {
        configManager.setJndi(beanDefinition.getBeanClass(), jndi);
        configManager.setJndi(beanDefinition.getRemoteClass(), jndi);
    }

    /**
     * 通过jndi查找对应的bean
     *
     * @param jndi
     *            jndi
     * @param context
     *            javax.naming.Context
     * @return 如果未找到返回null
     */
    public Object tryLookup(String jndi) {
        try {
            return getContext().lookup(jndi);
        } catch (NamingException e) {
            return null;
        }
    }

    protected Object tryLookup(String jndi, Context context) {
        try {
            return context.lookup(jndi);
        } catch (NamingException e) {
            return null;
        }
    }

    @Override
    public boolean isDeclareBean(final BeanDefinition declare, final Object bean) {
        Class<?> remoteClass = declare.getRemoteClass();
        if (log.isDebugEnabled()) {
            log.debug("test, it is declare bean? "//
                    + "\n\tdeclare remoteClass -> {}" //
                    + "\n\tdeclare beanClass   -> {}" //
                    + "\n\tactual  bean        -> {}", //
                    remoteClass, declare.getBeanClass(), bean);
        }
        return declare.getBeanClass().isInstance(bean) || (remoteClass != null && remoteClass.isInstance(bean));
    }

    protected boolean isWrappedBean(final Object bean) {
        return bean == unwrap(bean);
    }

    // 通过配置的wrappedBeanHandlers来解压实际的bean
    protected Object unwrap(Object bean) {
        return bean;
    }

    private String getValue(Map map, String... keys) {
        Object result = null;
        for (String key : keys) {
            result = map.get(key);
            if (result instanceof String && StringUtils.isNotBlank((String) result)) {
                return (String) result;
            }
        }
        return null;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public void setConfigManager(ConfigManager configManager) {
        this.configManager = configManager;
    }

    public static final String JNDI_GLOBAL = "jndi.global.prefix";
    public static final String JNDI_ROOT = "jndi.format.root";
    public static final String JNDI_BEAN = "jndi.format.bean";
    public static final String JNDI_SEPARATOR = "jndi.format.separator";
    public static final String JNDI_REMOTE = "jndi.format.remote";

    @SuppressWarnings({ "unchecked", "unused" })
    private static final Set<String> ROOTS = new LinkedHashSet(Arrays.asList("", "java:/"));
    @SuppressWarnings("unchecked")
    private static final Set<String> BEANS = new LinkedHashSet(Arrays.asList("Bean", ""));
    @SuppressWarnings("unchecked")
    private static final Set<String> REMOTES = new LinkedHashSet(Arrays.asList("Remote", ""));
    @SuppressWarnings("unchecked")
    private static final Set<String> SEPARATORS = new LinkedHashSet(Arrays.asList("#", "!"));

    // ### jndi guesser
    @SuppressWarnings("unchecked")
    private abstract class JndiGuesser {

        final String globalPrefix = null;

        final BeanDefinition beanDefinition;
        final ClassWrapper classWrapper;
        final Map<String, Object> properties;

        final LinkedHashSet<String> jndiSet = new LinkedHashSet<String>();

        public JndiGuesser(BeanDefinition beanDefinition, Map<String, Object> properties) {
            this.beanDefinition = beanDefinition;
            this.classWrapper = new ClassWrapper(beanDefinition.getBeanClass());
            this.properties = properties;
        }

        protected Collection<String> beanNames() {
            String value = getValue(properties, "mappedName");
            if (StringUtils.isNotBlank(value)) {
                return Arrays.asList(value);
            }
            return guessBeanName();
        }

        protected Collection<Class> remoteClasses() {
            if (beanDefinition.isRemoteClass()) {
                return Arrays.<Class> asList(beanDefinition.getRemoteClass());
            }
            return guessRemoteClasses();
        }

        protected abstract Collection<String> guessBeanName();

        protected abstract Collection<Class> guessRemoteClasses();

        /**
         * 通过配置信息猜想可能的jndi
         */
        public final String[] guess() {
            String value = getValue(properties, "lookup", "jndi", "jndiname");
            if (StringUtils.isNotBlank(value)) {
                jndiSet.add(value);
            }
            Set<String> separators = configManager.getPropertySet("JNDI_BEAN", SEPARATORS);
            for (String mappedName : beanNames()) {
                for (String separator : separators) {
                    for (Class remoteClass : remoteClasses()) {
                        addIfAbsent(mappedName, separator, remoteClass);
                    }
                }
            }
            return jndiSet.toArray(new String[jndiSet.size()]);
        }

        /**
         * 格式划jndi, 再判断jndi是否存在于上下文中
         * <p/>
         * <p/>
         * 
         * <pre>
         *   JNDI = prefix() + beanSuffix + separator + package + . + prefix() + remoteSuffix
         * </pre>
         *
         * @param mappedName
         *            bean的映射名称
         * @param separator
         *            分割符
         * @param remoteClass
         *            remote的类型
         */
        protected void addIfAbsent(String mappedName, String separator, Class<?> remoteClass) {
            StringBuilder jndi = new StringBuilder();
            jndi.append(globalPrefix)//
                    .append(mappedName)//
                    .append(separator)//
                    .append(remoteClass.getName())//
                    .toString();
            if (!jndiSet.contains(jndi)) {
                jndiSet.add(jndi.toString());
            }
        }

    }

    final class RemoteJndiGuesser extends JndiGuesser {

        public RemoteJndiGuesser(BeanDefinition beanDefinition, Map<String, Object> properties) {
            super(beanDefinition, properties);
        }

        /**
         * 通过remote接口猜想mappedName
         * <p/>
         * <ul>
         * <li>首先匹配remoteClass的所有子类,通过对子类的判断来获取mappedName</li>
         * <li>如果有@EJB注解, 通过注解上的名称mappedName来获取对于的名称</li>
         * <li>通过配置的猜想,通过替换远程接口的后缀来猜想mappedName</li>
         * </ul>
         */
        @Override
        protected Collection<String> guessBeanName() {
            Set<String> names = new LinkedHashSet<String>();
            String value = getValue(properties, "mappedName");
            if (value != null) {
                names.add(value);
            }

            // 添加子类的名称
            Class[] subClasses = classWrapper.getAllSubClasses();
            for (Class clazz : subClasses) {
                BeanDefinition subDefinition = new BeanDefinition(clazz);
                if (subDefinition.isSessionClass()) {
                    names.add(clazz.getSimpleName());
                }
            }

            String simpleName = beanDefinition.getRemoteClass().getSimpleName();
            Set<String> remoteSuffixes = configManager.getPropertySet(JNDI_REMOTE, REMOTES);
            Set<String> beanSuffixes = configManager.getPropertySet(JNDI_BEAN, BEANS);
            for (String remote : remoteSuffixes) {
                if (simpleName.endsWith(remote)) {
                    String name = simpleName.substring(0, simpleName.length() - remote.length());
                    for (String bean : beanSuffixes) {
                        names.add(name + bean);
                    }
                }
            }
            return names;
        }

        @Override
        protected Collection<Class> guessRemoteClasses() {
            return beanDefinition.getAllRemoteClasses();
        }

    }

}
