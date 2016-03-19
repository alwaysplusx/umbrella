/*
 * Copyright 2002-2015 the original author or authors.
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
package com.harmony.umbrella.monitor;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;

import com.harmony.umbrella.monitor.support.EJBMethodInterceptor;

/**
 * @author wuxii@foxmail.com
 */
@Interceptor
public class LoggingInterceptor extends EJBMethodInterceptor {

    private static final Log log = Logs.getLog(LoggingInterceptor.class);

    public LoggingInterceptor() {
        this.policy = MonitorPolicy.All;
        this.graphListeners.add(new GraphListener<MethodGraph>() {

            @Override
            public void analyze(MethodGraph graph) {
                log.info("{}", graph);
            }
        });
    }

    @AroundInvoke
    public Object log(InvocationContext ctx) throws Exception {
        return monitor(ctx);
    }

}
