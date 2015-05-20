/*
 * Copyright 2013-2015 wuxii@foxmail.com.
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
package com.harmony.umbrella.monitor.impl;

import java.lang.reflect.Method;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harmony.umbrella.monitor.MethodMonitor;
import com.harmony.umbrella.monitor.annotation.Monitored;
import com.harmony.umbrella.monitor.support.AbstractMethodMonitor;

/**
 * 基于拦截器的实现，默认来接{@link MethodMonitor#DEFAULT_METHOD_PATTERN}表达式的方法.<p>当然要连接器能拦截到方法
 * 
 * @author wuxii@foxmail.com
 */
@Monitored
@Interceptor
public class MethodMonitorInterceptor extends AbstractMethodMonitor<InvocationContext> implements MethodMonitor {

	private static final Logger log = LoggerFactory.getLogger(MethodMonitorInterceptor.class);

	public MethodMonitorInterceptor() {
		this(DEFAULT_METHOD_PATTERN);
	}

	public MethodMonitorInterceptor(String pattern) {
		this.includePattern(pattern);
	}

	@Override
	@AroundInvoke
	protected Object monitor(InvocationContext ctx) throws Exception {
		return super.monitor(ctx);
	}

	@Override
	protected void persistGraph(MethodGraph graph) {
		log.info("method interceptor result graph:{}", graph);
	}

	@Override
	protected Method getMethod(InvocationContext ctx) {
		return ctx.getMethod();
	}

	@Override
	protected Object getTarget(InvocationContext ctx) {
		return ctx.getTarget();
	}

	@Override
	protected Object process(InvocationContext ctx) throws Exception {
		return ctx.proceed();
	}

	@Override
	protected Object[] getParameters(InvocationContext ctx) {
		return ctx.getParameters();
	}

}
