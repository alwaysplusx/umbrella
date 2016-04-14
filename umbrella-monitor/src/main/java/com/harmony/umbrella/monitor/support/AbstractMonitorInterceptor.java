package com.harmony.umbrella.monitor.support;

import java.lang.reflect.Method;

import com.harmony.umbrella.monitor.AbstractMonitor;
import com.harmony.umbrella.monitor.ResourceMatcher;
import com.harmony.umbrella.monitor.matcher.MethodExpressionMatcher;

/**
 * 监控抽象类主要提供了对监控资源的控制，在何种情况下对资源进行监控
 *
 * @author wuxii@foxmail.com
 */
public abstract class AbstractMonitorInterceptor<C> extends AbstractMonitor<Method> {

    private ResourceMatcher<Method> resourceMatcher = new MethodExpressionMatcher();

    @Override
    protected ResourceMatcher<Method> getResourceMatcher() {
        return resourceMatcher;
    }

    protected abstract InvocationContext convert(C c);

    protected abstract Object doInterceptor(InvocationContext invocationContext) throws Exception;

    public Object interceptor(InvocationContext invocationContext) throws Exception {
        Method method = invocationContext.getMethod();
        if (isMonitored(method)) {
            return doInterceptor(invocationContext);
        }
        return invocationContext.process();
    }

}
