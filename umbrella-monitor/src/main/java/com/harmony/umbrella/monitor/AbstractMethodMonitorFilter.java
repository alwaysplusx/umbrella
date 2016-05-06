package com.harmony.umbrella.monitor;

import java.lang.reflect.Method;

import com.harmony.umbrella.monitor.matcher.MethodExpressionMatcher;

/**
 * 监控基础抽象类
 *
 * @param <T>
 *            监控的资源类型，由子类指定
 * @author wuxii@foxmail.com
 */
public abstract class AbstractMethodMonitorFilter implements MonitorFilter<Method> {

    private ResourceMatcher<Method> resourceMatcher = new MethodExpressionMatcher();
    private MonitorPolicy policy = MonitorPolicy.All;

    @Override
    public boolean isMonitored(Method resource) {
        switch (policy) {
        case Skip:
            return false;
        case All:
            return true;
        case WhiteList:
            // 与模版匹配不监控
            for (String pattern : getPattern(policy)) {
                if (getResourceMatcher().matches(pattern, resource)) {
                    return false;
                }
            }
            return true;
        case BlackList:
            // blocklist 资源匹配监控
            for (String pattern : getPattern(policy)) {
                if (getResourceMatcher().matches(pattern, resource)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    public ResourceMatcher<Method> getResourceMatcher() {
        return resourceMatcher;
    }

    public void setResourceMatcher(ResourceMatcher<Method> resourceMatcher) {
        this.resourceMatcher = resourceMatcher;
    }

    @Override
    public void setMonitorPolicy(MonitorPolicy policy) {
        this.policy = policy;
    }

    @Override
    public MonitorPolicy getMonitorPolicy() {
        return policy;
    }

}
