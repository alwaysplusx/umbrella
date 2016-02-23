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
package com.harmony.umbrella.log.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.harmony.umbrella.log.LogMessage;
import com.harmony.umbrella.log.Logs;
import com.harmony.umbrella.log.annotation.Log;

/**
 * @author wuxii@foxmail.com
 */
public class SimpleServiceProxy implements InvocationHandler {

    private Object target;

    public SimpleServiceProxy(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;

        Class<?> targetClass = target.getClass();
        com.harmony.umbrella.log.Log log = Logs.getLog(targetClass);
        LogMessage logMessage = LogMessage.create(log);

        Log ann = method.getAnnotation(Log.class);

        if (ann != null) {
            logMessage.action(ann.action())//
                    .module(ann.module())//
                    .bizModule(ann.bizModule())//
                    .level(ann.level());
        }

        logMessage.operator("wuxii")//
                .operatorId(1l);

        try {
            logMessage.start();

            result = method.invoke(target, args);

            logMessage.finish();

            logMessage.result(result);
        } catch (Throwable e) {
            logMessage.exception(e);
            throw e;
        }

        logMessage.log();

        return result;
    }
}
