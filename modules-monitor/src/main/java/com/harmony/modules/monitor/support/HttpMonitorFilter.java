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
package com.harmony.modules.monitor.support;

import static com.harmony.modules.utils.ObjectUtils.*;

import java.io.IOException;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.harmony.modules.monitor.Graph;
import com.harmony.modules.monitor.HttpMonitor;
import com.harmony.modules.monitor.util.MonitorUtils;
import com.harmony.modules.utils.Exceptions;

/**
 * @author wuxii@foxmail.com
 */
public class HttpMonitorFilter implements HttpMonitor {

	/**
	 * 受到监视的资源
	 */
	private Map<String, Object> monitorList = new HashMap<String, Object>();
	/**
	 * 是否开启白名单策略，开启后只拦截在监视名单中的资源
	 */
	private boolean whiteList;

	@Override
	public void exclude(String resource) {
		monitorList.remove(resource);
	}

	@Override
	public void include(String resource) {
		monitorList.put(resource, null);
	}

	@Override
	public boolean isMonitored(String resource) {
		if (whiteList) {
			return monitorList.containsKey(resource);
		}
		return true;
	}

	@Override
	public boolean isWhiteList() {
		return whiteList;
	}

	@Override
	public void useWhiteList(boolean use) {
		this.whiteList = use;
	}

	@Override
	public String[] getMonitorList() {
		Set<String> set = monitorList.keySet();
		return set.toArray(new String[set.size()]);
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		String resource = MonitorUtils.requestIdentifie(request);
		if (isMonitored(resource)) {
			HttpGraph graph = new HttpGraph(resource);
			graph.setRequestArguments(request);
			try {
				chain.doFilter(request, response);
				graph.setResponseResult(request);
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
				doPersist(graph);
			}
			return;
		}
		chain.doFilter(request, response);
	}

	protected void doPersist(Graph graph) {
	}

	@Override
	public void destroy() {
	}

	public class HttpGraph implements Graph {

		private String identifie;
		private Calendar requestTime = Calendar.getInstance();
		private Calendar responseTime;
		private Map<String, Object> result;
		private Map<String, Object> arguments;
		private Exception exception;

		public HttpGraph() {
			super();
		}

		public HttpGraph(String identifie) {
			super();
			this.identifie = identifie;
		}

		@Override
		public String getIdentifie() {
			return identifie;
		}

		@Override
		public Calendar getRequestTime() {
			return requestTime;
		}

		@Override
		public Calendar getResponseTime() {
			return responseTime;
		}

		@Override
		public Object getResult() {
			return result;
		}

		@Override
		public Map<String, Object> getArguments() {
			return arguments;
		}

		@Override
		public boolean isException() {
			return exception != null;
		}

		@Override
		public String getExceptionMessage() {
			return isException() ? exception.getMessage() : null;
		}

		@Override
		public String getCause() {
			return isException() ? Exceptions.getRootCause(exception).getMessage() : null;
		}

		public Exception getException() {
			return exception;
		}

		public void setException(Exception exception) {
			this.exception = exception;
		}

		public void setIdentifie(String identifie) {
			this.identifie = identifie;
		}

		public void setRequestTime(Calendar requestTime) {
			this.requestTime = requestTime;
		}

		public void setResponseTime(Calendar responseTime) {
			this.responseTime = responseTime;
		}

		public void setRequestArguments(HttpServletRequest request) {
			Map<String, Object> sessionAttrMap = new HashMap<String, Object>();
			Map<String, Object> reqAttrMap = new HashMap<String, Object>();
			arguments.put("parameter", request.getParameterMap());
			arguments.put("sessionAttribute", sessionAttrMap);
			arguments.put("requestAttribute", reqAttrMap);

			for (Enumeration<String> names = request.getAttributeNames(); names.hasMoreElements();) {
				String name = names.nextElement();
				reqAttrMap.put(name, request.getAttribute(name));
			}

			HttpSession session = request.getSession();
			for (Enumeration<String> names = session.getAttributeNames(); names.hasMoreElements();) {
				String name = names.nextElement();
				sessionAttrMap.put(name, session.getAttribute(name));
			}

		}

		public void setResponseResult(HttpServletRequest request) {
			Map<String, Object> sessionAttrMap = new HashMap<String, Object>();
			Map<String, Object> reqAttrMap = new HashMap<String, Object>();
			result.put("sessionAttribute", sessionAttrMap);
			result.put("requestAttribute", reqAttrMap);
			{
				Map<String, Object> requestAttrMap = getRequestAttrMap();
				for (Enumeration<String> names = request.getAttributeNames(); names.hasMoreElements();) {
					String name = names.nextElement();
					if (requestAttrMap.containsKey(name) && nullSafeEquals(request.getAttribute(name), requestAttrMap.get(name))) {
						continue;
					}
					reqAttrMap.put(name, request.getAttribute(name));
				}
			}
			{
				Map<String, Object> requestSessionAttrMap = getRequestSessionAttrMap();
				HttpSession session = request.getSession();
				for (Enumeration<String> names = session.getAttributeNames(); names.hasMoreElements();) {
					String name = names.nextElement();
					if (requestSessionAttrMap.containsKey(name) && nullSafeEquals(session.getAttribute(name), requestSessionAttrMap.get(name))) {
						continue;
					}
					sessionAttrMap.put(name, session.getAttribute(name));
				}
			}

		}

		@SuppressWarnings("unchecked")
		private Map<String, Object> getRequestAttrMap() {
			return (Map<String, Object>) arguments.get("requestAttribute");
		}

		@SuppressWarnings("unchecked")
		private Map<String, Object> getRequestSessionAttrMap() {
			return (Map<String, Object>) arguments.get("sessionAttribute");
		}

        @Override
        public long use() {
            if (requestTime != null && responseTime != null) {
                return responseTime.getTimeInMillis() - requestTime.getTimeInMillis();
            }
            return -1;
        }

	}

}
