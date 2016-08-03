package com.harmony.umbrella.ee.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.naming.Context;

import com.harmony.umbrella.ee.BeanDefinition;
import com.harmony.umbrella.ee.formatter.JndiFormatter;
import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;
import com.harmony.umbrella.util.StringUtils;

/**
 * @author wuxii@foxmail.com
 */
public class ConfigurableBeanResolver extends AbstractBeanResolver {

    private static final Log log = Logs.getLog(ConfigurableBeanResolver.class);

    private List<String> beanNameAttributes = new ArrayList<String>();

    private List<String> beanInterfaceAttributes = new ArrayList<String>();

    private List<String> jndiAttributes = new ArrayList<String>();

    private PartResolver<String> beanNameResolver;

    @SuppressWarnings("rawtypes")
    private PartResolver<Class> beanInterfaceResolver;

    private JndiFormatter jndiFormatter;

    private boolean forced;

    @SuppressWarnings("rawtypes")
    @Override
    protected String[] guessNames(BeanDefinition bd, Map<String, Object> properties, Context context) {
        JndiHolder holder = new JndiHolder(context, forced);

        // 配置属性直接存在jndi名称无需猜测直接返回jndi
        for (String attr : jndiAttributes) {
            Object jndi = properties.get(attr);
            if (jndi != null && jndi instanceof String && StringUtils.isNotBlank((String) jndi)) {
                if (!holder.addIfAbsent((String) jndi)) {
                    log.warn("配置的jndi[{}]无效", jndi);
                }
            }
        }
        if (!holder.isEmpty()) {
            // 属性中有直接配置jndi
            return holder.getJndis();
        }

        // 配置属性中的beanName
        Collection<String> beanNames = new HashSet<String>();
        for (String attr : beanNameAttributes) {
            Object beanName = properties.get(attr);
            if (beanName != null && beanName instanceof String && StringUtils.isNotBlank((String) beanName)) {
                beanNames.add((String) beanName);
            }
        }

        // 不存在beanName的配置属性则通过猜测获取beanName
        if (beanNames.isEmpty() && beanNameResolver != null) {
            beanNames.addAll(beanNameResolver.resolve(bd));
        }

        // 未能猜测到beanName无法组成jndi
        if (beanNames.isEmpty()) {
            log.warn("{} not bean name find", bd);
            return new String[0];
        }

        // 配置属性中的beanInterface
        Collection<Class> beanInterfaces = new HashSet<Class>();
        for (String attr : beanInterfaceAttributes) {
            Object beanInterface = properties.get(attr);
            if (beanInterface != null && beanInterface instanceof Class && ((Class) beanInterface).isInterface()) {
                beanInterfaces.add((Class) beanInterface);
            }
        }

        // 不存在配置的beanInterface属性则通过猜测获取beanInterface
        if (beanInterfaces.isEmpty() && beanInterfaceResolver != null) {
            beanInterfaces.addAll(beanInterfaceResolver.resolve(bd));
        }

        // 默认猜测不到则添加默认beanDefinition中的beanInterface
        if (beanInterfaces.isEmpty()) {
            Collections.addAll(beanInterfaces, bd.getRemoteClasses());
        }

        // 无法获取以及猜测到beanInterface无法组成jndi
        if (beanInterfaces.isEmpty()) {
            log.warn("{} not bean interface find", bd);
            return new String[0];
        }

        // 将得出的beanName与beanInterface格式化组成jndi
        Collection<String> jndis = jndiFormatter.format(beanNames, beanInterfaces);
        for (String jndi : jndis) {
            if (!holder.addIfAbsent(jndi)) {
                log.info("猜测到的jndi[{}]无效", jndi);
            }
        }

        return holder.getJndis();
    }

    public List<String> getBeanNameAttributes() {
        return beanNameAttributes;
    }

    public void setBeanNameAttributes(List<String> beanNameAttributes) {
        this.beanNameAttributes = beanNameAttributes;
    }

    public List<String> getJndiAttributes() {
        return jndiAttributes;
    }

    public void setJndiAttributes(List<String> jndiAttributes) {
        this.jndiAttributes = jndiAttributes;
    }

    public List<String> getBeanInterfaceAttributes() {
        return beanInterfaceAttributes;
    }

    public void setBeanInterfaceAttributes(List<String> beanInterfaceAttributes) {
        this.beanInterfaceAttributes = beanInterfaceAttributes;
    }

    public PartResolver<String> getBeanNameResolver() {
        return beanNameResolver;
    }

    public void setBeanNameResolver(PartResolver<String> beanNameResolver) {
        this.beanNameResolver = beanNameResolver;
    }

    @SuppressWarnings("rawtypes")
    public PartResolver<Class> getBeanInterfaceResolver() {
        return beanInterfaceResolver;
    }

    @SuppressWarnings("rawtypes")
    public void setBeanInterfaceResolver(PartResolver<Class> beanInterfaceResolver) {
        this.beanInterfaceResolver = beanInterfaceResolver;
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

    protected class JndiHolder {

        private boolean forced;
        private Context context;

        private List<String> jndis = new ArrayList<String>();

        public JndiHolder(Context context, boolean forced) {
            this.context = context;
            this.forced = forced;
        }

        public boolean addIfAbsent(String jndi) {
            if (!jndis.contains(jndi) && (exists(jndi) || !forced)) {
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
}
