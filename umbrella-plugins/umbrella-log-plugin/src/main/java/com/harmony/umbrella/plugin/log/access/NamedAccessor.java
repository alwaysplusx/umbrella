package com.harmony.umbrella.plugin.log.access;

import com.harmony.umbrella.util.MemberUtils;
import com.harmony.umbrella.util.ReflectionUtils;

/**
 * 反射获取值工具
 * 
 * @author wuxii@foxmail.com
 */
public class NamedAccessor extends CheckedAccessor<Object> {

    public NamedAccessor() {
        super(Object.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean support(String name) {
        return name != null && MemberUtils.isReadable(getType(), name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object get(String name, Object obj) {
        return ReflectionUtils.getFieldValue(name, obj);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void set(String name, Object obj, Object val) {
        ReflectionUtils.setFieldValue(name, obj, val);
    }

}
