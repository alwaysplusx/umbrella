package com.harmony.umbrella.log.access;

import com.harmony.umbrella.access.MemberAccess;
import com.harmony.umbrella.util.ReflectionUtils;

/**
 * @author wuxii@foxmail.com
 */
public class NamedAccessor extends CheckedAccessor<Object> {

    public NamedAccessor() {
        super(Object.class);
    }

    @Override
    public boolean support(String name) {
        return name != null && MemberAccess.isReadable(getType(), name);
    }

    @Override
    public Object get(String name, Object obj) {
        return ReflectionUtils.getFieldValue(name, obj);
    }

    @Override
    public void set(String name, Object obj, Object val) {
        ReflectionUtils.setFieldValue(name, obj, val);
    }

}
