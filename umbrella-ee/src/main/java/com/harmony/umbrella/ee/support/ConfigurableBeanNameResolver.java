package com.harmony.umbrella.ee.support;

import java.util.Map;
import java.util.Set;

import javax.naming.Context;

import com.harmony.umbrella.ee.BeanDefinition;

/**
 * @author wuxii@foxmail.com
 */
public class ConfigurableBeanNameResolver extends AbstractBeanNameResolver {

    private Set<String> beanNameAttributes;

    private Set<String> jndiAttributes;

    @Override
    protected String[] guessNames(BeanDefinition bd, Map<String, Object> properties, Context context) {
        return null;
    }

    public Set<String> getBeanNameAttributes() {
        return beanNameAttributes;
    }

    public void setBeanNameAttributes(Set<String> beanNameAttributes) {
        this.beanNameAttributes = beanNameAttributes;
    }

    public Set<String> getJndiAttributes() {
        return jndiAttributes;
    }

    public void setJndiAttributes(Set<String> jndiAttributes) {
        this.jndiAttributes = jndiAttributes;
    }

}
