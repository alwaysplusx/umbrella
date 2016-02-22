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

import com.harmony.umbrella.log.LogFormat;
import com.harmony.umbrella.log.LogMessage;
import com.harmony.umbrella.log.Logs;
import com.harmony.umbrella.log.annotation.Log;
import com.harmony.umbrella.monitor.GraphListener;
import com.harmony.umbrella.monitor.MethodGraph;

/**
 * @author wuxii@foxmail.com
 */
public class LogGraphListener implements GraphListener<MethodGraph> {

    @Override
    public void analyze(MethodGraph graph) {
        Method method = graph.getMethod();

        com.harmony.umbrella.log.Log log = Logs.getLog(graph.getTargetClass());

        LogMessage logMessage = LogMessage.create(log);

        Log ann = method.getAnnotation(Log.class);

        if (ann != null) {
            logMessage.module(ann.module()).action(ann.action()).level(ann.level());
            Class<? extends LogFormat> logFormatClass = ann.logFormat();
            if (logFormatClass != null && LogFormat.class != logFormatClass) {
                try {
                    logMessage.formatter(logFormatClass.newInstance());
                } catch (Exception e) {
                }
            }

        }

        // TODO set log message other properties

        logMessage.log();

    }
}
