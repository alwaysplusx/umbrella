package com.harmony.umbrella.el.resolver;

import com.harmony.umbrella.access.MemberAccess;
import com.harmony.umbrella.el.CheckedResolver;
import com.harmony.umbrella.util.ReflectionUtils;

/**
 * @author wuxii@foxmail.com
 */
public class NamedResolver extends CheckedResolver<Object> {

    public NamedResolver(int priority) {
        super(priority);
    }

    @Override
    public boolean support(String name, Object obj) {
        return obj != null && MemberAccess.isReadable(obj.getClass(), name);
    }

    @Override
    protected Object doResolve(String name, Object obj) {
        return ReflectionUtils.getFieldValue(name, obj);
    }

}
