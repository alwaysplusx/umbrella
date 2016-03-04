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

import javax.interceptor.InvocationContext;

import com.harmony.umbrella.context.ApplicationContext;
import com.harmony.umbrella.context.CurrentContext;
import com.harmony.umbrella.log.ErrorHandlerManager;
import com.harmony.umbrella.log.LogInfo;
import com.harmony.umbrella.log.LogMessage;
import com.harmony.umbrella.log.Logs;
import com.harmony.umbrella.log.Template;
import com.harmony.umbrella.log.TemplateFactory;
import com.harmony.umbrella.log.annotation.Log;
import com.harmony.umbrella.log.template.MessageTemplateFactory;

/**
 * @author wuxii@foxmail.com
 */
public class LoggingInterceptor {

    protected TemplateFactory templateFactory = new MessageTemplateFactory();

    private ErrorHandlerManager errorHandlerManager = ErrorHandlerManager.INSTANCE;

    protected ApplicationContext context = ApplicationContext.getApplicationContext();

    public Object log(InvocationContext ctx) throws Exception {
        Object result = null;

        Object target = ctx.getTarget();
        Method method = ctx.getMethod();
        Object[] params = ctx.getParameters();

        Log ann = method.getAnnotation(Log.class);

        if (ann != null) {
            com.harmony.umbrella.log.Log log = Logs.getLog(target.getClass());
            LogMessage msg = new LogMessage(log);

            msg.module(ann.module())//
                    .action(ann.action())//
                    .bizModule(ann.bizModule())//
                    .level(ann.level());

            // http request or session support
            CurrentContext cctx = context.getCurrentContext();
            if (cctx != null) {
                msg.operator(cctx.getUsername())//
                        .operatorId(cctx.getUserId());
            }

            msg.start();

            try {

                result = ctx.proceed();

                msg.result(result);

            } catch (Exception e) {
                msg.exception(e);
                throw e;
            }

            msg.finish();

            // messageTemplate + method + target + params + result
            Template template = templateFactory.createTemplate(method);

            msg.message(template.newMessage(target, result, params));
            msg.bizId(template.getId(target, result, params));

            msg.log();

            if (errorHandlerManager != null) {
                LogInfo logInfo = msg.asInfo();
                if (logInfo.isException()) {
                    errorHandlerManager.dispatch(logInfo, method, target, ann.errorHandler());
                }
            }

        } else {
            result = method.invoke(target, params);
        }

        return result;
    }

}
