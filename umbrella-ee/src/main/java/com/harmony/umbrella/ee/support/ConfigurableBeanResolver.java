package com.harmony.umbrella.ee.support;

import static com.harmony.umbrella.ee.JndiConstanst.*;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.harmony.umbrella.core.PropertyManager;
import com.harmony.umbrella.ee.BeanDefinition;
import com.harmony.umbrella.ee.util.EJBUtils;
import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;

/**
 * @author wuxii@foxmail.com
 */
public class ConfigurableBeanResolver extends AbstractBeanResolver {

    private static final Log log = Logs.getLog(ConfigurableBeanResolver.class);

    private PropertyManager propertyManager;

    private String propertyDelimiter = ",";

    private PartResolver<String> beanNameResolver;

    private PartResolver<Class> beanInterfaceResolver;

    @Override
    protected boolean guessNames(BeanDefinition bd, Map<String, Object> properties, JndiHolder holder) {

        if (holder.isAcceptBeanName() && beanNameResolver != null) {
            holder.addBeanName(beanNameResolver.resolve(bd));
        }

        // 未能猜测到beanName无法组成jndi
        if (holder.isBeanNameEmpty()) {
            log.warn("{} not bean name found!", bd);
            return false;
        }

        // 不存在配置的beanInterface属性则通过猜测获取beanInterface
        if (holder.isAcceptBeanInterface() && beanInterfaceResolver != null) {
            holder.addBeanInterface(beanInterfaceResolver.resolve(bd));
            // 默认猜测不到则添加默认beanDefinition中的beanInterface
            if (holder.isBeanInterfaceEmpty()) {
                holder.addBeanInterface(bd.getRemoteClasses());
                // 无法获取以及猜测到beanInterface无法组成jndi
                if (holder.isBeanInterfaceEmpty()) {
                    log.warn("{} not bean interface found!", bd);
                    return false;
                }
            }
        }
        return holder.test();
    }

    private Set<String> asSet(String text) {
        return EJBUtils.asSet(text, propertyDelimiter);
    }

    @Override
    protected Collection<String> getJndiAttributes() {
        return asSet(propertyManager.getString(ATTRIBUTE_JNDI));
    }

    @Override
    protected Collection<String> getBeanNameAttributes() {
        return asSet(propertyManager.getString(ATTRIBUTE_BEAN_NAME));
    }

    @Override
    protected Collection<String> getBeanInterfaceAttributes() {
        return asSet(propertyManager.getString(ATTRIBUTE_BEAN_INTERFACE));
    }

    public PropertyManager getPropertyManager() {
        return propertyManager;
    }

    public void setPropertyManager(PropertyManager propertyManager) {
        this.propertyManager = propertyManager;
    }

    public String getPropertyDelimiter() {
        return propertyDelimiter;
    }

    public void setPropertyDelimiter(String propertyDelimiter) {
        this.propertyDelimiter = propertyDelimiter;
    }

    public PartResolver<String> getBeanNameResolver() {
        return beanNameResolver;
    }

    public void setBeanNameResolver(PartResolver<String> beanNameResolver) {
        this.beanNameResolver = beanNameResolver;
    }

    public PartResolver<Class> getBeanInterfaceResolver() {
        return beanInterfaceResolver;
    }

    public void setBeanInterfaceResolver(PartResolver<Class> beanInterfaceResolver) {
        this.beanInterfaceResolver = beanInterfaceResolver;
    }

}
