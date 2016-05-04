package com.harmony.umbrella.access;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.harmony.umbrella.util.Assert;
import com.harmony.umbrella.util.ReflectionUtils;

/**
 * @author wuxii@foxmail.com
 */
@SuppressWarnings("rawtypes")
public class AccessorHolder {

    private static final List<TypedAccessor> defaultAccessors = new ArrayList<TypedAccessor>();

    static {
        defaultAccessors.add(new ArrayAccessor());
        defaultAccessors.add(new HttpRequestAccessor());
        defaultAccessors.add(new HttpServletContextAccessor());
        defaultAccessors.add(new HttpSessionAccessor());
        defaultAccessors.add(new ListAccessor());
        defaultAccessors.add(new MapAccessor());
        defaultAccessors.add(new NamedAccessor());
        defaultAccessors.add(new PrimitiveArrayAccessor());
    }

    private Map<Class, List<TypedAccessor>> accessorMap = new HashMap<Class, List<TypedAccessor>>();

    public AccessorHolder() {
    }

    public AccessorHolder(TypedAccessor... accessors) {
        this(Arrays.asList(accessors));
    }

    public AccessorHolder(List<TypedAccessor> accessors) {
        for (TypedAccessor t : accessors) {
            this.addAccessor(t);
        }
    }

    @SuppressWarnings("unchecked")
    public List<TypedAccessor> getAccessor(Class<?> type) {
        List<TypedAccessor> result = _getAccessor(type);
        // 直接匹配
        if (result.isEmpty()) {
            result = new ArrayList<TypedAccessor>();
            // 找寻子类的access， 子类的access不保存与type的关联关系
            for (Class clazz : accessorMap.keySet()) {
                if (clazz.isAssignableFrom(type)) {
                    result.addAll(accessorMap.get(clazz));
                }
            }
        }
        Collections.sort(result);
        return result;
    }

    public void addAccessor(TypedAccessor... accessor) {
        for (TypedAccessor t : accessor) {
            _getAccessor(t.getType()).add(t);
        }
    }

    @SuppressWarnings("unchecked")
    public Object getValue(String name, Object obj) {
        Assert.notNull(obj, "target object is null");
        Exception ex = null;
        List<TypedAccessor> accessor = getAccessor(obj.getClass());
        for (TypedAccessor t : accessor) {
            if (t instanceof CheckedAccessor && ((CheckedAccessor) t).support(name)) {
                return t.get(name, obj);
            }
        }
        for (TypedAccessor t : accessor) {
            try {
                return t.get(name, obj);
            } catch (Exception e) {
                ex = e;
            }
        }
        if (ex != null) {
            ReflectionUtils.rethrowRuntimeException(ex);
        }
        throw new IllegalArgumentException("");
    }

    @SuppressWarnings("unchecked")
    public void setValue(String name, Object obj, Object val) {
        Assert.notNull(obj, "target object is null");
        Exception ex = null;
        List<TypedAccessor> accessor = getAccessor(obj.getClass());
        for (TypedAccessor t : accessor) {
            if (t instanceof CheckedAccessor && ((CheckedAccessor) t).support(name)) {
                t.set(name, obj, val);
            } else {
                try {
                    t.set(name, obj, val);
                } catch (Exception e) {
                    ex = e;
                }
            }
        }
        if (ex != null) {
            ReflectionUtils.rethrowRuntimeException(ex);
        }
        throw new IllegalArgumentException("");
    }

    private List<TypedAccessor> _getAccessor(Class<?> type) {
        List<TypedAccessor> result = accessorMap.get(type);
        // 直接匹配
        if (result == null) {
            accessorMap.put(type, result = new ArrayList<TypedAccessor>());
        }
        return result;
    }

    public static List<TypedAccessor> getAllAccessor() {
        return new ArrayList<TypedAccessor>(defaultAccessors);
    }

}
