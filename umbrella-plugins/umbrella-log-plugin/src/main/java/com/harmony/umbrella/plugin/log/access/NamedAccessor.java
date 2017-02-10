package com.harmony.umbrella.plugin.log.access;

import com.harmony.umbrella.util.MemberUtils;

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
        return MemberUtils.getValue(name, obj);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void set(String name, Object obj, Object val) {
        MemberUtils.setValue(name, obj, val);
    }

}
