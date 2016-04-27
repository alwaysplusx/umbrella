package com.harmony.umbrella.core.accessor;

import com.harmony.umbrella.util.ReflectionUtils;
import com.harmony.umbrella.util.StringUtils;

/**
 * @author wuxii@foxmail.com
 */
public class ClassMethodAccessor extends AbstractAccessor {

    public static final ClassMethodAccessor INSTANCE = new ClassMethodAccessor();
    
    @Override
    public boolean isAccessible(String name, Object target) {
        return target instanceof Class && StringUtils.isNotBlank(name);
    }

    @Override
    public Object getNameValue(String name, Object target) {
        return ReflectionUtils.findMethod((Class<?>) target, name);
    }

    @Override
    public void setNameValue(String name, Object target, Object value) {
        throw new UnsupportedOperationException("unsupport set class method");
    }

}
