package com.harmony.umbrella.ws.cxf.log;

import java.util.concurrent.atomic.AtomicInteger;

import com.harmony.umbrella.util.Exceptions;

/**
 * @author wuxii@foxmail.com
 */
public class CXFLogMessage {

    public static final String ID_KEY = CXFLogMessage.class.getName() + ".ID";

    private static final AtomicInteger ID = new AtomicInteger();

    private final String id; // soap消息的唯一标识 同义词in out 使用同一个id
    private String type;// soap消息的类型 in or out
    private final StringBuilder address;
    private final StringBuilder contentType;
    private final StringBuilder encoding;
    private final StringBuilder httpMethod; // 访问服务的方法 GET, POST
    private final StringBuilder header; // http的header内容
    private final StringBuilder payload; // 访问服务的有效负载
    private final StringBuilder responseCode; // 服务返回的http代码
    private Throwable exception; // 服务返回的异常信息
    private String operationName;

    public CXFLogMessage(String id, String type) {
        this.id = id;
        this.type = type;
        this.contentType = new StringBuilder();
        this.address = new StringBuilder();
        this.encoding = new StringBuilder();
        this.httpMethod = new StringBuilder();
        this.header = new StringBuilder();
        this.payload = new StringBuilder();
        this.responseCode = new StringBuilder();
    }

    public static String nextId() {
        return Integer.toString(ID.incrementAndGet());
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public StringBuilder getAddress() {
        return address;
    }

    public StringBuilder getContentType() {
        return contentType;
    }

    public StringBuilder getEncoding() {
        return encoding;
    }

    public StringBuilder getHttpMethod() {
        return httpMethod;
    }

    public StringBuilder getHeader() {
        return header;
    }

    public StringBuilder getPayload() {
        return payload;
    }

    public StringBuilder getResponseCode() {
        return responseCode;
    }

    public boolean isException() {
        return exception != null;
    }

    public Throwable getException() {
        return exception;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }

    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("--------------------------------------");
        buffer.append("\nID: ").append(id);
        buffer.append("\nType: ").append(type);
        if (operationName != null) {
            buffer.append("\nOperation Name: ");
            buffer.append(operationName);
        }

        if (address.length() > 0) {
            buffer.append("\nAddress: ");
            buffer.append(address);
        }

        if (responseCode.length() > 0) {
            buffer.append("\nResponse-Code: ");
            buffer.append(responseCode);
        }

        buffer.append("\nContent-Type: ");
        buffer.append(contentType);

        buffer.append("\nHeaders: ");
        buffer.append(header);

        if (isException()) {
            buffer.append("\nException: ");
            buffer.append(Exceptions.getRootCause(exception));
        }

        if (payload.length() > 0) {
            buffer.append("\nPayload: \n");
            buffer.append(payload);
        }

        if (!buffer.toString().endsWith("\n")) {
            buffer.append("\n");
        }

        buffer.append("--------------------------------------");
        return buffer.toString();
    }

}
