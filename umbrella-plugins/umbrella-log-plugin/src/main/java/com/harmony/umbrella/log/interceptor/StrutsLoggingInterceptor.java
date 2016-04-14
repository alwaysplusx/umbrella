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

import javax.servlet.http.HttpServletRequest;

import com.harmony.umbrella.monitor.support.AbstractStrutsMonitorInterceptor;

/**
 * @author wuxii@foxmail.com
 */
public class StrutsLoggingInterceptor extends AbstractStrutsMonitorInterceptor {

    private static final long serialVersionUID = 1136087148041235740L;

    private HttpServletRequest request;
    private LogGraphReporter reporter;

    @Override
    public void init() {
        reporter = new DefaultLogGraphReporter();
    }

    @Override
    protected Object doInterceptor(com.harmony.umbrella.monitor.support.InvocationContext invocationContext) throws Exception {
        Object result = invocationContext.process();
        reporter.report(invocationContext.toGraph(), request);
        return result;
    }

    @Override
    public void destroy() {
    }

    public void setReporter(LogGraphReporter reporter) {
        this.reporter = reporter;
    }

}
