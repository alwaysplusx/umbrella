package com.harmony.umbrella.plugin.log.access;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

/**
 * @author wuxii@foxmail.com
 */
@SuppressWarnings("rawtypes")
public class TypedAccessorChainImpl implements TypedAccessorChain {

    private List<TypedAccessor> typedAccessors = new ArrayList<TypedAccessor>();

    public TypedAccessorChainImpl() {
    }

    public TypedAccessorChainImpl(TypedAccessor... accessors) {
        this.setTypedAccessors(Arrays.asList(accessors));
    }

    public TypedAccessorChainImpl(List<TypedAccessor> accessors) {
        this.setTypedAccessors(accessors);
    }

    /**
     * 通过属性名称获取目标的值
     * 
     * @param name
     *            属性名称
     * @param obj
     *            目标
     * @return 目标值
     * @see TypedAccessor#get(String, Object)
     */
    @SuppressWarnings("unchecked")
    public Object getValue(String name, Object obj) {
        Assert.notNull(obj, "expression target object is null, " + name);
        Exception ex = null;
        List<TypedAccessor> accessor = updateIterator(obj.getClass());
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

    /**
     * 设置目标值
     * 
     * @param name
     *            目标属性
     * @param obj
     *            目标值的拥有者
     * @param val
     *            目标值
     * @see TypedAccessor#set(String, Object, Object)
     */
    @SuppressWarnings("unchecked")
    public void setValue(String name, Object obj, Object val) {
        Assert.notNull(obj, "target object is null");
        Exception ex = null;
        List<TypedAccessor> accessor = updateIterator(obj.getClass());
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

    @SuppressWarnings("unchecked")
    private List<TypedAccessor> updateIterator(Class<?> clazz) {
        List<TypedAccessor> result = new ArrayList<TypedAccessor>();
        for (TypedAccessor ta : typedAccessors) {
            if (ta.getType().isAssignableFrom(clazz)) {
                result.add(ta);
            }
        }
        Collections.sort(result, new Comparator<TypedAccessor>() {

            @Override
            public int compare(TypedAccessor o1, TypedAccessor o2) {
                Class<?> t1 = o1.getType();
                Class<?> t2 = o2.getType();
                return (!t1.isAssignableFrom(t2) && t1.equals(t2)) ? 0 : ((t1.isAssignableFrom(t2)) ? 1 : -1);
            }
        });
        return result;
    }

    public void addTypedAccessors(Collection<TypedAccessor> accessor) {
        this.typedAccessors.addAll(accessor);
    }

    public void addTypedAccessors(TypedAccessor... accessor) {
        Collections.addAll(this.typedAccessors, accessor);
    }

    public List<TypedAccessor> getTypedAccessors() {
        return typedAccessors;
    }

    public void setTypedAccessors(List<TypedAccessor> typedAccessors) {
        this.typedAccessors = typedAccessors;
    }

    public static TypedAccessorChain createDefault() {
        return new TypedAccessorChainImpl(//
                new ArrayAccessor(), //
                new ListAccessor(), //
                new MapAccessor(), //
                new NamedAccessor(), //
                new PrimitiveArrayAccessor()//
        );
    }

}
