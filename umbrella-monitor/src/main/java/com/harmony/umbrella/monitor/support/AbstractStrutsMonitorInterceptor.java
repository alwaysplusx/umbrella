package com.harmony.umbrella.monitor.support;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.interceptor.Interceptor;

/**
 * @author wuxii@foxmail.com
 */
public abstract class AbstractStrutsMonitorInterceptor extends AbstractMonitorInterceptor<ActionInvocation> implements Interceptor {

    private static final long serialVersionUID = 1L;

    @Override
    protected InvocationContext convert(ActionInvocation invocation) {
        return new StrutsInvocationContext(invocation);
    }

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        return (String) doInterceptor(convert(invocation));
    }

    protected static final Method getMethod(ActionInvocation ctx) {
        // action 的方法一般为无参的public方法，再此不做过多判断
        ActionProxy proxy = ctx.getProxy();
        String methodName = proxy.getMethod();
        if (methodName == null) {
            methodName = proxy.getConfig().getMethodName();
        }
        try {
            return ctx.getAction().getClass().getMethod(methodName);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    public HttpServletRequest getRequest() {
        return ServletActionContext.getRequest();
    }

    public HttpServletResponse getResponse() {
        return ServletActionContext.getResponse();
    }

    static final class StrutsInvocationContext extends InvocationContext {

        private final ActionInvocation invocation;

        public StrutsInvocationContext(ActionInvocation invocation) {
            super(invocation.getAction(), AbstractStrutsMonitorInterceptor.getMethod(invocation), new Object[0]);
            this.invocation = invocation;
        }

        @Override
        protected Object doProcess() throws Exception {
            return invocation.invoke();
        }
    }
}
