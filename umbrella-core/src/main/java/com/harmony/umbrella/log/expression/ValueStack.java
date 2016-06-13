package com.harmony.umbrella.log.expression;

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
        return getStack().pop();
    }

    public static synchronized ValueContext peek() {
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
