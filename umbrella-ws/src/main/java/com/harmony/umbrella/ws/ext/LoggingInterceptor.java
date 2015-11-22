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
package com.harmony.umbrella.ws.ext;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import com.harmony.umbrella.monitor.GraphListener;
import com.harmony.umbrella.monitor.MethodGraph;
import com.harmony.umbrella.monitor.ext.LogUtils;
import com.harmony.umbrella.monitor.support.EJBMethodInterceptor;

/**
 * 服务端webservice日志监控工具
 * 
 * @author wuxii@foxmail.com
 */
@Interceptor
public class LoggingInterceptor extends EJBMethodInterceptor {

    public LoggingInterceptor() {
        this.policy = MonitorPolicy.All;
        this.graphListeners.add(new GraphListener<MethodGraph>() {

            @Override
            public void analyze(MethodGraph graph) {
                LogUtils.log(graph);
            }

        });
    }

    @AroundInvoke
    public Object log(InvocationContext ctx) throws Exception {
        return monitor(ctx);
    }

}
