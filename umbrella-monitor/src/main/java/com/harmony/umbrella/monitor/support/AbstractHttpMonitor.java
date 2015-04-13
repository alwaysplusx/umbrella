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
package com.harmony.umbrella.monitor.support;

import java.io.IOException;
import java.util.Calendar;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.harmony.umbrella.monitor.AbstractMonitor;
import com.harmony.umbrella.monitor.DefaultHttpGraph;
import com.harmony.umbrella.monitor.HttpMonitor;
import com.harmony.umbrella.monitor.ResourceMatcher;
import com.harmony.umbrella.monitor.util.MonitorUtils;
import com.harmony.umbrella.util.Exceptions;

/**
 * @author wuxii@foxmail.com
 */
public abstract class AbstractHttpMonitor extends AbstractMonitor<String> implements HttpMonitor {

	/**
	 * 保存http监视结果
	 * 
	 * @param graph
	 */
	protected abstract void persistGraph(HttpGraph graph);

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		String resource = MonitorUtils.requestIdentifie(request);
		if (!isMonitored(resource)) {
			chain.doFilter(request, response);
			return;
		}
		DefaultHttpGraph graph = new DefaultHttpGraph(resource);
		graph.setRequestArguments(request);
		try {
			chain.doFilter(request, response);
			graph.setResponseResult(request, response);
		} catch (Exception e) {
			graph.setException(e);
			if (e instanceof IOException) {
				throw (IOException) e;
			}
			if (e instanceof ServletException) {
				throw (ServletException) e;
			}
			throw Exceptions.unchecked(e);
		} finally {
			graph.setResponseTime(Calendar.getInstance());
			persistGraph(graph);
		}
	}

	@Override
	public void destroy() {
	}

	@Override
	protected ResourceMatcher<String> createMatcher(String pattern) {
		return new ResourcePathMatcher(pattern);
	}

}
