package com.harmony.umbrella.monitor;

/**
 * 资源匹配器
 * <p>
 * 如:监控http资源，使用url匹配原则。 方法则匹配方法签名
 * 
 * @author wuxii@foxmail.com
 * @see com.harmony.umbrella.monitor.matcher.UrlPathMatcher
 * @see com.harmony.umbrella.monitor.matcher.MethodExpressionMatcher
 */
public interface ResourceMatcher<T> {

    /**
     * 匹配资源是否符合当前定义的规则
     * 
     * @param pattern
     *            资源的模版
     * @param resource
     *            待检查的资源
     * @return true匹配成功
     */
    boolean matches(String pattern, T resource);

}
