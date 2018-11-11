package com.harmony.umbrella.web;

import com.harmony.umbrella.context.ContextHelper;
import com.harmony.umbrella.json.Json;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author wuxii@foxmail.com
 */
public final class Response<T> implements Serializable {

    private static final long serialVersionUID = 1655555210935131563L;

    /**
     * 返回正常的code
     */
    public static final int OK = 200;

    public static final int ERROR = 500;

    public static final int NOT_FOUND = 404;

    public static final int UNKNOW = 0;

    /**
     * 返回编码
     */
    protected int code;
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
    protected T data;
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

    public T getData() {
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
            resp.put("request", request);
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

    public static <T> Response<T> ok(T data) {
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

    public static ParamResponseBuilder newBuilder(int code) {
        return new ParamResponseBuilder(code);
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

        public View toView() {
            return new ResponseView(build());
        }
    }

    public static final class OKResponseBuilder {

        private Response response = new Response();
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

        public OKResponseBuilder setData(Object data) {
            this.data = data;
            return this;
        }

        public <T> Response<T> build(T data) {
            Response response = new Response();
            response.code = OK;
            response.data = data;
            return response;
        }

        public <T> Response<T> build() {
            return (Response<T>) build(data);
        }

        public View toView() {
            return new ResponseView(build());
        }

    }

    public static final class ParamResponseBuilder {

        private Response response = new Response();
        private Map<String, Object> data;

        private ParamResponseBuilder(int code) {
            this.response.code = code;
        }

        public ParamResponseBuilder msg(String msg) {
            this.response.msg = msg;
            return this;
        }

        public ParamResponseBuilder trace(String trace) {
            this.response.trace = trace;
            return this;
        }

        public ParamResponseBuilder request(String request) {
            this.response.request = request;
            return this;
        }

        public ParamResponseBuilder param(String key, Object value) {
            if (data == null) {
                data = new LinkedHashMap<>();
            }
            this.data.put(key, value);
            return this;
        }

        public Response<Map<String, Object>> build(Map<String, Object> data) {
            Response response = new Response();
            response.code = OK;
            response.data = data;
            return response;
        }

        public Response<Map<String, Object>> build() {
            return build(data);
        }

        public View toView() {
            return new ResponseView(build());
        }

    }

    private static class ResponseView implements View {

        private Response response;

        public ResponseView(Response response) {
            this.response = response;
        }

        @Override
        public String getContentType() {
            return MediaType.APPLICATION_JSON_UTF8_VALUE;
        }

        @Override
        public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
            String responseText = this.response.toJson();
            response.setContentType(getContentType());
            response.getWriter().write(responseText);
        }

    }

}
