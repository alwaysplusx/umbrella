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

import com.harmony.umbrella.log.ErrorHandlerManager;
import com.harmony.umbrella.log.LogMessage;
import com.harmony.umbrella.log.Logs;
import com.harmony.umbrella.log.TemplateFactory;
import com.harmony.umbrella.log.annotation.Log;
import com.harmony.umbrella.log.template.MessageTemplateFactory;

/**
 * @author wuxii@foxmail.com
 */
public class AbstractLoggingInterceptor {

    protected TemplateFactory templateFactory = new MessageTemplateFactory();

    protected ErrorHandlerManager errorHandlerManager = ErrorHandlerManager.INSTANCE;

    protected LogMessage createLogMessage(Object target, Log ann) {
        com.harmony.umbrella.log.Log log = Logs.getLog(target.getClass());
        LogMessage msg = new LogMessage(log);

        msg.module(ann.module())//
                .action(ann.action())//
                .bizModule(ann.bizModule())//
                .level(ann.level());

        return msg;
    }

    public static Log getLogAnnotation(Method method) {
        return method.getAnnotation(Log.class);
    }

    public static boolean hasLog(Method method) {
        return method.getAnnotation(Log.class) != null;
    }

}
