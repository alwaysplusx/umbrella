package com.harmony.umbrella.web;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.harmony.umbrella.context.ContextHelper;
import com.harmony.umbrella.json.Json;

/**
 * @author wuxii@foxmail.com
 */
public final class Response implements Serializable {

    private static final long serialVersionUID = 1655555210935131563L;

    /**
     * 返回正常的code
     */
    public static final int OK = 0;
    /**
     * 返回编码
     */
    protected int code = -1;
    /**
     * 异常的信息
     */
    protected String msg;
    /**
     * 日志追踪码
     */
    protected String trace;
    /**
     * Response的数据
     */
    protected Object data;
    /**
     * 请求的资源
     */
    protected String request;

    protected Response() {
        this(getCurrentRequestUrl());
    }

    Response(String request) {
        this.request = request;
    }

    public boolean isOk() {
        return code == OK;
    }

    public Object getData() {
        return data;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public String getTrace() {
        return trace;
    }

    public String getRequest() {
        return request;
    }

    public String toJson() {
        return isOk() ? okJson() : errorJson();
    }

    protected String okJson() {
        if (data != null) {
            return Json.toJson(data);
        }
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("code", code);
        if (msg != null) {
            resp.put("msg", msg);
        }
        return Json.toJson(resp);
    }

    protected String errorJson() {
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("code", code);
        if (msg != null) {
            resp.put("msg", msg);
        }
        if (trace != null) {
            resp.put("trace", trace);
        }
        if (request != null) {
            resp.put("request", "");
        }
        return Json.toJson(resp);
    }

    // static

    private static String getCurrentRequestUrl() {
        HttpServletRequest request = ContextHelper.getHttpRequest();
        return request != null ? request.getRequestURI() : null;
    }

    public static Response ok() {
        return okBuilder().build();
    }

    public static Response ok(Object data) {
        return okBuilder().setData(data).build();
    }

    public static Response error(int code, String msg) {
        return errorBuilder().code(code).msg(msg).build();
    }

    public static OKResponseBuilder okBuilder() {
        return new OKResponseBuilder();
    }

    public static ErrorResponseBuilder errorBuilder() {
        return new ErrorResponseBuilder();
    }

    // builder

    public static final class ErrorResponseBuilder {

        private Response response = new Response();

        private ErrorResponseBuilder() {
        }

        public ErrorResponseBuilder code(int code) {
            this.response.code = code;
            return this;
        }

        public ErrorResponseBuilder msg(String msg) {
            this.response.msg = msg;
            return this;
        }

        public ErrorResponseBuilder trace(String trace) {
            this.response.trace = trace;
            return this;
        }

        public ErrorResponseBuilder request(String request) {
            this.response.request = request;
            return this;
        }

        public Response build() {
            Response result = this.response;
            this.response = new Response();
            return result;
        }

    }

    public static final class OKResponseBuilder {

        private Response response = new Response();
        private Map<String, Object> params;
        private Object data;

        private OKResponseBuilder() {
        }

        public OKResponseBuilder msg(String msg) {
            this.response.msg = msg;
            return this;
        }

        public OKResponseBuilder trace(String trace) {
            this.response.trace = trace;
            return this;
        }

        public OKResponseBuilder request(String request) {
            this.response.request = request;
            return this;
        }

        public OKResponseBuilder param(String name, Object value) {
            this.data = null;
            if (params == null) {
                params = new LinkedHashMap<>();
            }
            params.put(name, value);
            return this;
        }

        public OKResponseBuilder setData(Object data) {
            this.data = data;
            this.params = null;
            return this;
        }

        public Response build() {
            Response response = new Response();
            response.code = OK;
            response.data = data != null ? data : params;
            return response;
        }

    }

}
