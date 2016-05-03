package com.harmony.umbrella.el.resolver;

import java.util.Map;

import com.harmony.umbrella.el.CheckedResolver;
import com.harmony.umbrella.util.StringUtils;

/**
 * @author wuxii@foxmail.com
 */
public class MapResolver extends CheckedResolver<Map<?, ?>> {

    public MapResolver(int priority) {
        super(priority);
    }

    @Override
    public boolean support(String name, Object obj) {
        return StringUtils.isNotBlank(name) && obj instanceof Map;
    }

    @Override
    protected Object doResolve(String name, Map<?, ?> obj) {
        return obj.get(name);
    }

}
