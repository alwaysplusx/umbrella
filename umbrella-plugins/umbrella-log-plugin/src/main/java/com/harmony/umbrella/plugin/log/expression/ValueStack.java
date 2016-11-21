package com.harmony.umbrella.plugin.log.expression;

import java.util.Stack;

/**
 * @author wuxii@foxmail.com
 */
public final class ValueStack {

    private static final ThreadLocal<Stack<ValueContext>> valueContextLocale = new InheritableThreadLocal<Stack<ValueContext>>();

    public static synchronized ValueContext push(ValueContext e) {
        return getStack().push(e);
    }

    public static synchronized ValueContext pop() {
        Stack<ValueContext> stack = getStack();
        if (stack.isEmpty()) {
            throw new IllegalStateException("stack not under interceptor, please push value content in value stack first");
        }
        return stack.pop();
    }

    public static synchronized ValueContext peek() {
        Stack<ValueContext> stack = getStack();
        if (stack.isEmpty()) {
            throw new IllegalStateException("stack not under interceptor, please push value content in value stack first");
        }
        return getStack().peek();
    }

    public static synchronized void clear() {
        valueContextLocale.set(null);
    }

    private static Stack<ValueContext> getStack() {
        Stack<ValueContext> stack = valueContextLocale.get();
        if (stack == null) {
            valueContextLocale.set(stack = new Stack<ValueContext>());
        }
        return stack;
    }

}
