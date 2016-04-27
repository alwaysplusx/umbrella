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
package com.harmony.umbrella.log.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.harmony.umbrella.log.LogMessage;
import com.harmony.umbrella.log.Logs;
import com.harmony.umbrella.log.annotation.Logging;
import com.harmony.umbrella.log.template.MessageTemplateFactory;
import com.harmony.umbrella.log.template.Template;
import com.harmony.umbrella.log.template.TemplateFactory;

/**
 * @author wuxii@foxmail.com
 */
public class SampleServiceProxy implements InvocationHandler {

    private TemplateFactory templateFactory = new MessageTemplateFactory();
    private Object target;

    public SampleServiceProxy(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;

        Logging ann = method.getAnnotation(Logging.class);

        if (ann != null) {
            com.harmony.umbrella.log.Log log = Logs.getLog(target.getClass());
            LogMessage msg = new LogMessage(log);

            msg.module(ann.module())//
                    .action(ann.action())//
                    .bizModule(ann.bizModule())//
                    .level(ann.level());

            // http request or session support
            msg.operator("wuxii")//
                    .operatorId(1l);

            msg.start();

            try {

                result = method.invoke(target, args);

                msg.result(result);

            } catch (Exception e) {
                msg.exception(e);
                throw e;
            }

            msg.finish();

            // messageTemplate + method + target + params + result
            Template template = templateFactory.createTemplate(method.getAnnotation(Logging.class));

            msg.message(template.newMessage(target, result, args));
            msg.bizId(template.getId(target, result, args));

            msg.log();

        } else {
            result = method.invoke(target, args);
        }

        return result;
    }
}
