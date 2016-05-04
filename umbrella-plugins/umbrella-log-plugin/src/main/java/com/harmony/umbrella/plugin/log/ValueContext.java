package com.harmony.umbrella.plugin.log;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.harmony.umbrella.access.AccessorHolder;
import com.harmony.umbrella.access.TypedAccessor;

/**
 * @author wuxii@foxmail.com
 */
public class ValueContext {

    //private static final ThreadLocal<ValueContext> valueContextThreadLocal = new InheritableThreadLocal<ValueContext>();

    private Map<String, Object> context;
    private AccessorHolder accessorHolder = new AccessorHolder();

    private ValueContext() {
    }

    private ValueContext(AccessorHolder accessorHolder) {
        this.accessorHolder = accessorHolder;
    }

    public Map<String, Object> getContext() {
        return context;
    }

    public void set(String name, Object val) {
        context.get(name);
    }

    public Object find(String expression) {
        return find(new Expression(expression, ".[]"));
    }

    public Object find(Expression expression) {
        if (expression.isEmpty()) {
            return null;
        }
        if (expression.isText()) {
            return expression.getExpressionText();
        }
        // 直接存在对应的key
        if (context.containsKey(expression.getExpressionText())) {
            return context.get(expression.getExpressionText());
        }
        Object value = context;
        Iterator<String> it = expression.iterator(".[]");
        while (it.hasNext()) {
            String token = it.next();
            Object tmp = accessorHolder.getValue(token, value);
            if (value == null && it.hasNext()) {
                throw new IllegalArgumentException("got null value from " + value + " " + token);
            }
            value = tmp;
        }
        return context;
    }

    @SuppressWarnings("rawtypes")
    public void addAccessor(TypedAccessor... accessor) {
        accessorHolder.addAccessor(accessor);
    }

    public AccessorHolder getAccessorHolder() {
        return accessorHolder;
    }

    public static ValueContext createDefault() {
        return create(AccessorHolder.getAllAccessor());
    }

    public static ValueContext create(AccessorHolder accessorHolder) {
        return new ValueContext(accessorHolder);
    }

    @SuppressWarnings("rawtypes")
    public static ValueContext create(List<TypedAccessor> accessors) {
        return new ValueContext(new AccessorHolder(accessors));
    }
}
