package com.harmony.umbrella.log.template;

import java.util.Stack;

import com.harmony.umbrella.log.template.LoggingContext.ValueContext;

/**
 * 日志值上下文值栈(线程绑定), 配合拦截器可以实现拦截器绑定
 * 
 * @author wuxii@foxmail.com
 */
public final class ValueContextStack {

    private static final ThreadLocal<Stack<ValueContext>> valueContextLocale = new InheritableThreadLocal<Stack<ValueContext>>();

    public static ValueContext push(ValueContext e) {
        return getStack().push(e);
    }

    public static ValueContext pop() {
        Stack<ValueContext> stack = getStack();
        if (stack.isEmpty()) {
            throw new IllegalStateException("value stack not in use, please push it first");
        }
        return stack.pop();
    }

    public static ValueContext peek() {
        Stack<ValueContext> stack = getStack();
        if (stack.isEmpty()) {
            throw new IllegalStateException("value stack not in use, please push it first");
        }
        return getStack().peek();
    }

    public static void clear() {
        Stack<ValueContext> stack = valueContextLocale.get();
        if (stack != null) {
            stack.clear();
        }
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
