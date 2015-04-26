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
package com.harmony.umbrella.jaxws.cxf;

import java.lang.reflect.Method;
import java.util.Map;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.message.Message;

/**
 * CXF Message 工具类
 * 
 * @author wuxii@foxmail.com
 */
public class CXFMessageUtils {

	/**
	 * 从{@linkplain Message}中获取上下文
	 * 
	 * @param message
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getContext(Message message) {
		return (Map<String, Object>) message.get(Message.INVOCATION_CONTEXT);
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getRequestContext(Message message) {
		Map<String, Object> context = getContext(message);
		return (Map<String, Object>) (null != context ? context.get(Client.REQUEST_CONTEXT) : null);
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getResponseContext(Message message) {
		Map<String, Object> context = getContext(message);
		return (Map<String, Object>) (null != context ? context.get(Client.RESPONSE_CONTEXT) : null);
	}

	public static Method getRequestMethod(Message message) {
		Map<String, Object> reqContext = getRequestContext(message);
		return (Method) (reqContext != null ? reqContext.get(Method.class.getName()) : null);
	}

}
