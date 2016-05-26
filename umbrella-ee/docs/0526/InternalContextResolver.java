package com.harmony.umbrella.context.ee.support;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.naming.Context;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import com.harmony.umbrella.context.ee.BeanDefinition;
import com.harmony.umbrella.context.ee.BeanFilter;
import com.harmony.umbrella.context.ee.ContextResolver;
import com.harmony.umbrella.context.ee.SessionBean;
import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;
import com.harmony.umbrella.util.ClassUtils;

/**
 * JavaEE环境内部解析工具，用于分析{@linkplain javax.naming.Context}
 *
 * @author wuxii@foxmail.com
 */
public class InternalContextResolver extends ConfigurationBeanResolver implements ContextResolver {

    private static final Log log = Logs.getLog(InternalContextResolver.class);
    private final Map<Class<?>, Set<String>> fastCache = new HashMap<Class<?>, Set<String>>();

    private final Set<String> roots;
    private final int maxDeeps;

    public InternalContextResolver(String globalPrefix,
                                   Set<String> separators,
                                   Set<String> beanSuffixes,
                                   Set<String> remoteSuffixes,
                                   Set<String> localSuffixes,
                                   Set<WrappedBeanHandler> wrappedBeanHandlers,
                                   boolean transformLocale,
                                   Set<String> roots,
                                   int maxDeeps) {
        super(globalPrefix, separators, beanSuffixes, remoteSuffixes, localSuffixes, wrappedBeanHandlers, transformLocale);
        this.roots = roots;
        this.maxDeeps = maxDeeps;
    }

    @Override
    public SessionBean search(final BeanDefinition bd, Context context) {
        MatchingSessionBean matchingSessionBean = new MatchingSessionBean(bd);
        // 从缓存的jndi中查找最合适的
        Set<String> jndiNames = fastCache.get(bd.getBeanClass());
        if (jndiNames != null) {
            for (String jndi : jndiNames) {
                if (matchingSessionBean.accept(jndi, tryLookup(jndi, context))) {
                    return matchingSessionBean;
                }
            }
        }
        guessBean(bd, context, matchingSessionBean);
        if (matchingSessionBean.hasResult()) {
            return matchingSessionBean;
        }
        deepSearch(matchingSessionBean, context);
        return matchingSessionBean.hasResult() ? matchingSessionBean : null;
    }

    /**
     * 深度查找BeanDefinition对应的类以及jndi
     */
    protected void deepSearch(MatchingSessionBean bean, Context context) {
        for (String root : roots) {
            doDeepSearch(root, bean, context, 0);
            if (bean.hasResult()) {
                return;
            }
        }
    }

    /**
     * 迭代查找Context中与beanDefinition相匹配的结果
     *
     * @param currentJndi
     *         parent Jndi
     * @param sessionBean
     *         迭代中的bean
     * @param context
     *         迭代的context
     * @param deeps
     *         当前迭代深度
     */
    private void doDeepSearch(String currentJndi, MatchingSessionBean sessionBean, Context context, int deeps) {
        if (deeps > this.maxDeeps) {
            // 超出最深迭代深度, 放弃
            return;
        }
        log.debug("deep search in context [{}], deep index {}", currentJndi, deeps);
        try {
            Object bean = context.lookup(currentJndi);
            if (bean instanceof Context) {
                NamingEnumeration<NameClassPair> subContextNames = ((Context) bean).list("");
                while (subContextNames.hasMoreElements()) {
                    NameClassPair subNcp = subContextNames.nextElement();
                    // 进入下层迭代查找
                    doDeepSearch(createSubJndi(currentJndi, subNcp), sessionBean, context, deeps++);
                    if (sessionBean.bean != null && sessionBean.jndi != null) {
                        // 迭代找到了session bean, 查找成功返回
                        return;
                    }
                }
            } else if (sessionBean.accept(currentJndi, bean)) {
                return;
            }
        } catch (NamingException e) {
            log.warn("context [{}] not find in {}", currentJndi, context);
        }
    }

    /**
     * root + NameClassPair 组合生成下一个jndi名称
     */
    private String createSubJndi(String jndi, NameClassPair subNcp) {
        // root + ("".equals(root) ? "" : "/") + subNcp.getName();
        StringBuilder sb = new StringBuilder(jndi);
        if (!"".equals(jndi)) {
            sb.append("/");
        }
        sb.append(subNcp.getName());
        return sb.toString();
    }

    /**
     * 记录jndi以及bean到缓存中
     */
    private void recordBeanWithJndi(Object bean, String jndi) {
        Class<?>[] interfaces = ClassUtils.getAllInterfaces(bean.getClass());
        for (Class<?> clazz : interfaces) {
            String className = clazz.getName();
            if (!className.startsWith("java.") //
                    && !className.startsWith("javax.")//
                    && !className.startsWith("org.apache.") //
                    && !className.startsWith("com.weblogic") //
                    && !className.startsWith("org.glassfish")//
                    && !className.startsWith("org.jboss")) {
                putJndiIntoCache(clazz, jndi);
            }
        }
    }

    /**
     * 将 interface 以及对应的jndi到缓存中
     */
    private void putJndiIntoCache(Class<?> clazz, String jndi) {
        Set<String> jndiNames = fastCache.get(clazz);
        if (jndiNames == null) {
            synchronized (fastCache) {
                if (!fastCache.containsKey(clazz)) {
                    jndiNames = new HashSet<String>();
                    fastCache.put(clazz, jndiNames);
                }
            }
        }
        jndiNames.add(jndi);
    }

    protected boolean filter(BeanDefinition beanDefinition) {
        return !(beanDefinition.isRemoteClass()
                || beanDefinition.isSessionClass()
                || beanDefinition.isLocalClass());
    }

    @Override
    public void clear() {
        fastCache.clear();
    }

    private final class MatchingSessionBean implements BeanFilter, SessionBean {

        final BeanDefinition beanDefinition;
        Object bean;
        String jndi;
        boolean wrapped;

        public MatchingSessionBean(BeanDefinition beanDefinition) {
            this.beanDefinition = beanDefinition;
        }

        @Override
        public boolean accept(String jndi, Object bean) {
            // 记录查找过的记录
            recordBeanWithJndi(bean, jndi);
            Object unwrapBean = unwrap(bean);
            if (isDeclare(beanDefinition, unwrapBean)) {
                this.bean = bean;
                this.jndi = jndi;
                this.wrapped = (bean != unwrapBean);
                return true;
            }
            return false;
        }

        public boolean hasResult() {
            return bean != null && jndi != null;
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
            return beanDefinition.isStateless();
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
