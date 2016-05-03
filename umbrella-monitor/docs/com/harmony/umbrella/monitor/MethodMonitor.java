package com.harmony.umbrella.monitor;

import java.lang.reflect.Method;

import com.harmony.umbrella.UmbrellaProperties;
import com.harmony.umbrella.monitor.matcher.MethodExpressionMatcher;

/**
 * 方法监控器
 * <p>
 * 默认监控符合表达式{@linkplain #DEFAULT_METHOD_PATTERN}的方法.
 * <p>
 * 该表达式解析如 {@linkplain MethodExpressionMatcher}
 * 
 * @author wuxii@foxmail.com
 * @see Monitor
 */
public interface MethodMonitor extends Monitor<Method> {

    /**
     * 默认监控的方法模版
     */
    String DEFAULT_METHOD_PATTERN = "execution(* " + UmbrellaProperties.DEFAULT_PACKAGE + "..*.*(..))";

}
