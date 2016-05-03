package com.harmony.umbrella.el.resolver;

import com.harmony.umbrella.el.CheckedResolver;
import com.harmony.umbrella.util.DigitUtils;

/**
 * @author wuxii@foxmail.com
 */
public class ArrayResolver extends CheckedResolver<Object[]> {

    public ArrayResolver(int priority) {
        super(Object[].class, priority);
    }

    @Override
    public boolean support(String name, Object obj) {
        return DigitUtils.isDigit(name) && Object[].class.isAssignableFrom(obj.getClass());
    }

    @Override
    protected Object doResolve(String name, Object[] obj) {
        return obj[Integer.valueOf(name)];
    }

}
