package com.harmony.umbrella.jaxws.cxf;

import java.lang.reflect.Method;
import java.util.Map;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.message.Message;

public class CXFMessageUtils {

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
