package com.harmony.umbrella.ee.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.naming.Context;

import com.harmony.umbrella.ee.BeanDefinition;
import com.harmony.umbrella.util.StringUtils;

/**
 * @author wuxii@foxmail.com
 */
public class ConfigurableBeanNameResolver extends AbstractBeanNameResolver {

    private List<String> beanNameAttributes;

    private List<String> jndiAttributes;

    @Override
    protected String[] guessNames(BeanDefinition bd, Map<String, Object> properties, Context context) {
        List<String> jndis = new ArrayList<String>();
        for (String attr : jndiAttributes) {
            Object jndi = properties.get(attr);
            if (jndi != null && jndi instanceof String && StringUtils.isNotBlank((String) jndi)) {
                jndis.add((String) jndi);
            }
        }
        if (!jndis.isEmpty()) {
            // 属性中有直接配置jndi
            return jndis.toArray(new String[jndis.size()]);
        }

        // 配置属性中的beanName
        Collection<String> beanNames = new HashSet<String>();
        for (String attr : beanNameAttributes) {
            Object beanName = properties.get(attr);
            if (beanName != null && beanName instanceof String && StringUtils.isNotBlank((String) beanName)) {
                beanNames.add((String) beanName);
            }
        }
        
        return null;
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

}
