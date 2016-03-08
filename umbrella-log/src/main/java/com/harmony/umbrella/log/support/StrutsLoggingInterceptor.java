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
package com.harmony.umbrella.log.support;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;

import com.harmony.umbrella.log.LogMessage;
import com.harmony.umbrella.log.Template;
import com.harmony.umbrella.log.annotation.Log;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.interceptor.Interceptor;

/**
 * @author wuxii@foxmail.com
 */
public class StrutsLoggingInterceptor extends AbstractLoggingInterceptor implements Interceptor {

    private static final long serialVersionUID = 8379237026930289206L;

    @Override
    public void init() {
    }

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        String result = null;

        Object target = invocation.getAction();
        Method method = getMethod(invocation);
        if (method != null && hasLog(method)) {
            Log ann = getLogAnnotation(method);
            LogMessage msg = createLogMessage(target, ann);

            // http request or session support
            /*CurrentContext cctx = context.getCurrentContext();
            if (cctx != null) {
                msg.operator(cctx.getUsername())//
                        .operatorId(cctx.getUserId());
            }*/

            msg.start();

            try {

                result = invocation.invoke();

                msg.result(result);

            } catch (Exception e) {
                msg.exception(e);
                throw e;
            }

            msg.finish();

            // messageTemplate + method + target + params + result
            Template template = templateFactory.createTemplate(method);

            msg.message(template.newMessage(target, result, null));
            msg.bizId(template.getId(target, result, null));

            msg.log();

        } else {
            result = invocation.invoke();
        }
        return result;
    }

    protected Method getMethod(ActionInvocation ctx) {
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

    @Override
    public void destroy() {
    }

    protected HttpServletRequest getRequest() {
        return ServletActionContext.getRequest();
    }

    protected HttpSession getSession() {
        return getRequest().getSession();
    }
}
