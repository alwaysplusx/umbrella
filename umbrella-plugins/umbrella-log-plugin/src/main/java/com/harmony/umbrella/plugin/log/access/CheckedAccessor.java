package com.harmony.umbrella.plugin.log.access;

import com.harmony.umbrella.util.GenericUtils;

/**
 * 带有检查是否支持的类型解析工具
 * 
 * @author wuxii@foxmail.com
 */
public abstract class CheckedAccessor<T> implements TypedAccessor<T> {

    private Class<T> type;

    public CheckedAccessor(Class<T> type) {
        this.type = type;
    }

    /**
     * 检测是否支持解析属性的方法
     * 
     * @param name
     *            属性名称
     * @return true支持解析
     */
    public abstract boolean support(String name);

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<T> getType() {
        if (type == null) {
            type = (Class<T>) GenericUtils.getTargetGeneric(getClass(), CheckedAccessor.class, 0);
        }
        return type;
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return getClass().equals(obj.getClass());
    }

}
