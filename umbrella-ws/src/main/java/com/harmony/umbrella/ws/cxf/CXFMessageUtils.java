package com.harmony.umbrella.ws.cxf;

import static org.apache.cxf.endpoint.Client.*;
import static org.apache.cxf.message.Message.*;

import java.lang.reflect.Method;
import java.util.Map;

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
        return (Map<String, Object>) message.get(INVOCATION_CONTEXT);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getRequestContext(Message message) {
        Map<String, Object> context = getContext(message);
        return (Map<String, Object>) (null != context ? context.get(REQUEST_CONTEXT) : null);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getResponseContext(Message message) {
        Map<String, Object> context = getContext(message);
        return (Map<String, Object>) (null != context ? context.get(RESPONSE_CONTEXT) : null);
    }

    public static Method getRequestMethod(Message message) {
        Map<String, Object> reqContext = getRequestContext(message);
        return (Method) (reqContext != null ? reqContext.get(Method.class.getName()) : null);
    }

    public static String getEncoding(Message message) {
        return (String) (message != null ? message.get(ENCODING) : null);
    }

    public static Integer getResponseCode(Message message) {
        return (Integer) (message != null ? message.get(RESPONSE_CODE) : null);
    }

    public static String getHttpRequestMethod(Message message) {
        return (String) (message != null ? message.get(HTTP_REQUEST_METHOD) : null);
    }

    public static String getEndpointAddress(Message message) {
        return (String) (message != null ? message.get(ENDPOINT_ADDRESS) : null);
    }

    public static String getRequestUri(Message message) {
        return (String) (message != null ? message.get(REQUEST_URI) : null);
    }

    public static String getContentType(Message message) {
        return (String) (message != null ? message.get(CONTENT_TYPE) : null);
    }

    public static String getFullAddress(Message message) {
        String address = getEndpointAddress(message);
        if (address != null) {
            String uri = getRequestUri(message);
            if (uri != null && !address.startsWith(uri)) {
                if (!address.endsWith("/") && !uri.startsWith("/")) {
                    address += "/";
                }
                address += uri;
            }
        }
        return address;
    }
}
