package com.harmony.umbrella.ee.support;

import java.util.List;
import java.util.Map;

import javax.naming.Context;

import com.harmony.umbrella.ee.BeanDefinition;

/**
 * @author wuxii@foxmail.com
 */
public class DefaultBeanResolver extends AbstractBeanResolver {

    @Override
    protected String[] guessNames(BeanDefinition bd, Map<String, Object> properties, Context context) {
        return null;
    }

    @Override
    protected List<String> getJndiAttributes() {
        return null;
    }

    @Override
    protected List<String> getBeanNameAttributes() {
        return null;
    }

    @Override
    protected List<String> getBeanInterfaceAttributes() {
        return null;
    }

}
