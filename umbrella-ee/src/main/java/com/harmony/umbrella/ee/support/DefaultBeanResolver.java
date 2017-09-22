package com.harmony.umbrella.ee.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.harmony.umbrella.ee.BeanDefinition;
import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;

/**
 * @author wuxii@foxmail.com
 */
public class DefaultBeanResolver extends AbstractBeanResolver {

    private static final Log log = Logs.getLog(DefaultBeanResolver.class);

    private List<String> beanNameAttributes = new ArrayList<String>();

    private List<String> beanInterfaceAttributes = new ArrayList<String>();

    private List<String> jndiAttributes = new ArrayList<String>();

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

    @Override
    public Collection<String> getJndiAttributes() {
        return jndiAttributes;
    }

    @Override
    public Collection<String> getBeanNameAttributes() {
        return beanNameAttributes;
    }

    @Override
    public Collection<String> getBeanInterfaceAttributes() {
        return beanInterfaceAttributes;
    }

    public void setBeanNameAttributes(Collection<String> beanNameAttributes) {
        this.beanNameAttributes.clear();
        this.beanNameAttributes.addAll(beanNameAttributes);
    }

    public void setBeanInterfaceAttributes(Collection<String> beanInterfaceAttributes) {
        this.beanInterfaceAttributes.clear();
        this.beanInterfaceAttributes.addAll(beanInterfaceAttributes);
    }

    public void setJndiAttributes(Collection<String> jndiAttributes) {
        this.jndiAttributes.clear();
        this.jndiAttributes.addAll(jndiAttributes);
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
