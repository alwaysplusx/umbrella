package com.harmony.umbrella.el.resolver;

import java.util.List;

import com.harmony.umbrella.el.CheckedResolver;
import com.harmony.umbrella.util.DigitUtils;

/**
 * @author wuxii@foxmail.com
 */
@SuppressWarnings("rawtypes")
public class ListResolver extends CheckedResolver<List> {

    public ListResolver(int priority) {
        super(List.class, priority);
    }

    @Override
    public boolean support(String name, Object obj) {
        return DigitUtils.isDigit(name) && obj instanceof List;
    }

    @Override
    protected Object doResolve(String name, List obj) {
        return obj.get(Integer.valueOf(name));
    }

}
