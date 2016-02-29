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
package com.harmony.umbrella.log;

import java.util.ServiceLoader;

import com.harmony.umbrella.log.spi.LogProvider;
import com.harmony.umbrella.log.support.Log4j2LogProvider;

/**
 * 
 * @author wuxii@foxmail.com
 */
public class Logs {

    private static LogProvider adapter;

    static {
        init();
    }

    public static void init() {
        // TODO 加载类路径下的日志适配器 
        ServiceLoader<LogProvider> providers = ServiceLoader.load(LogProvider.class);
        for (LogProvider provider : providers) {
            adapter = provider;
            break;
        }
        if (adapter == null) {
            adapter = new Log4j2LogProvider();
        }
    }

    /**
     * 从调用栈中找到上层类名的log
     * 
     * @return log
     */
    public static Log getLog() {
        StackTraceElement[] sts = Thread.currentThread().getStackTrace();
        return adapter.getLogger(sts[2].getClassName());
    }

    /**
     * 通过类名创建对应的log
     * 
     * @param clazz
     *            log的类名
     * @return log
     */
    public static Log getLog(Class<?> clazz) {
        return adapter.getLogger(clazz.getName());
    }

    /**
     * 通过名称创建对应的log
     * 
     * @param className
     *            log名称
     * @return log
     */
    public static Log getLog(String className) {
        return adapter.getLogger(className);
    }

}
