/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.harmony.umbrella.log.interceptor;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.harmony.umbrella.log.Logs;
import com.harmony.umbrella.log.Message;
import com.harmony.umbrella.log.TemplateFactory;
import com.harmony.umbrella.log.template.MessageTemplateFactory;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.interceptor.Interceptor;

/**
 * @author wuxii@foxmail.com
 */
public class StrutsLoggingInterceptor extends AbstractLoggingInterceptor<ActionInvocation> implements Interceptor, ServletRequestAware {

    private static final long serialVersionUID = 1136087148041235740L;

    private static final com.harmony.umbrella.log.Log log = Logs.getLog(StrutsLoggingInterceptor.class);

    protected TemplateFactory templateFactory;
    private HttpServletRequest request;

    @Override
    public void init() {
        super.init();
        this.templateFactory = new MessageTemplateFactory();
    }

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        return (String) logging(invocation);
    }

    @Override
    protected InvocationContext convert(ActionInvocation invocation) {
        return new StrutsInvocationContext(invocation);
    }

    @Override
    protected Message newMessage(InvocationContext ctx) {
        return templateFactory.createHttpTemplate(ctx.method, request).newMessage(ctx.target, ctx.result, ctx.parameters);
    }

    @Override
    public void setServletRequest(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public void destroy() {

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
            log.warn("cannot found action method {}", methodName);
            return null;
        }
    }

    private static final class StrutsInvocationContext extends InvocationContext {

        private final ActionInvocation invocation;

        public StrutsInvocationContext(ActionInvocation invocation) {
            super(invocation.getAction(), getMethod(invocation), new Object[0]);
            this.invocation = invocation;
        }

        @Override
        protected Object doProcess() throws Exception {
            return invocation.invoke();
        }
    }
}
