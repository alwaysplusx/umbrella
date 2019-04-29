package com.harmony.umbrella.log.interceptor;

import com.harmony.umbrella.log.LogMessage;

import java.util.*;

/**
 * @author wuxii
 */
public final class LogTraceContext {

    private static final ThreadLocal<Stack<LogTraceContext>> TRACE_CONTEXT = new InheritableThreadLocal<>();

    public static synchronized LogTraceContext push() {
        Optional<LogTraceContext> contextOpt = peek();
        String traceId = contextOpt.map(LogTraceContext::getTraceId).orElseGet(LogMessage::messageId);
        return getStack(true)
                .push(new LogTraceContext(contextOpt.orElse(null), traceId));
    }

    public static synchronized Optional<LogTraceContext> pop() {
        Stack<LogTraceContext> stack = getStack(false);
        return stack == null || stack.isEmpty() ? Optional.empty() : Optional.of(stack.pop());
    }

    public static synchronized Optional<LogTraceContext> peek() {
        Stack<LogTraceContext> stack = getStack(false);
        return stack == null || stack.isEmpty() ? Optional.empty() : Optional.of(stack.peek());
    }

    public static synchronized void clear() {
        Stack<LogTraceContext> stack = getStack(false);
        if (stack != null) {
            stack.clear();
            TRACE_CONTEXT.set(null);
        }
    }

    private static Stack<LogTraceContext> getStack(boolean create) {
        Stack<LogTraceContext> stack = TRACE_CONTEXT.get();
        if (stack == null && create) {
            TRACE_CONTEXT.set(stack = new Stack<>());
        }
        return stack;
    }

    private LogTraceContext parent;
    private String traceId;
    private Map<String, Object> properties;

    LogTraceContext(LogTraceContext parent, String traceId) {
        this.parent = parent;
        this.traceId = traceId;
        this.properties = new HashMap<>();
    }

    public LogTraceContext getParent() {
        return parent;
    }

    public String getTraceId() {
        return traceId;
    }

    public Map<Object, Object> getProperties() {
        return Collections.unmodifiableMap(properties);
    }

    public LogTraceContext putProperty(String key, Object value) {
        this.properties.put(key, value);
        return this;
    }

}
