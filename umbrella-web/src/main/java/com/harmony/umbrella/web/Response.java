package com.harmony.umbrella.web;

import com.harmony.umbrella.context.ContextHelper;
import com.harmony.umbrella.json.Json;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author wuxii@foxmail.com
 */
public final class Response<T> implements ResponseDetails, Serializable {

    private static final long serialVersionUID = 1655555210935131563L;

    /**
     * 返回正常的code
     */
    public static final int OK = 0;

    public static final int ERROR = -1;

    /**
     * 返回编码
     */
    protected int code;
    /**
     * 异常的信息
     */
    protected String msg;
    /**
     * 异常的描述信息
     */
    protected String desc;
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

    public Response() {
    }

    protected Response(int code) {
        this.code = code;
    }

    private Response(int code, T data) {
        this.code = code;
        this.data = data;
    }

    public boolean isOk() {
        return code == OK;
    }

    public T getData() {
        return data;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }

    public String getTrace() {
        return trace;
    }

    public String getDesc() {
        return desc;
    }

    public String getRequest() {
        return request;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setTrace(String trace) {
        this.trace = trace;
    }

    public void setData(T data) {
        this.data = data;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String toJson() {
        return isOk() ? okJson() : errorJson();
    }

    private String okJson() {
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

    private String errorJson() {
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

    public Optional<T> optionalData() {
        return Optional.ofNullable(data);
    }

    public T orElseThrow() {
        if (!isOk()) {
            throw new ResponseException(code, msg);
        }
        if (data == null) {
            throw new ResponseException(ERROR, "data not found");
        }
        return data;
    }

    public T orElseThrow(Function<ResponseException, ? extends RuntimeException> fun) {
        if (!isOk()) {
            throw fun.apply(new ResponseException(this));
        }
        return data;
    }

    // static

    private static String getCurrentRequestUrl() {
        HttpServletRequest request = ContextHelper.getHttpRequest();
        return request != null ? request.getRequestURI() : null;
    }

    public static <T> Response<T> of(ResponseDetails r) {
        return newBuilder(r).build();
    }

    public static <T> Response<T> of(ResponseDetails r, String desc) {
        return newBuilder(r).desc(desc).build();
    }

    public static <T> Response<T> ok() {
        return ok(null);
    }

    public static <T> Response<T> ok(T data) {
        return new Response<>(OK, data);
    }

    public static <T> Response<T> error(String msg) {
        return error(ERROR, msg);
    }

    public static <T> Response<T> error(int code, String msg) {
        return newBuilder(code).msg(msg).build();
    }

    public static <T> Response<T> error(ResponseException error) {
        return newBuilder(error.getCode())
                .msg(error.getMessage())
                .desc(error.getDescription())
                .build();
    }

    public static ResponseBuilder newBuilder(int code) {
        return new ResponseBuilder(code);
    }

    public static ResponseBuilder newBuilder(ResponseDetails r) {
        return newBuilder(r.getCode())
                .msg(r.getMsg());
    }

    // builder

    public static class ResponseBuilder {

        final Response response;

        private ResponseBuilder(int code) {
            this.response = new Response(code);
            this.response.request = getCurrentRequestUrl();
        }

        public ResponseBuilder msg(String msg) {
            this.response.msg = msg;
            return this;
        }

        public ResponseBuilder trace(String trace) {
            this.response.trace = trace;
            return this;
        }

        public ResponseBuilder desc(String desc) {
            this.response.desc = desc;
            return this;
        }

        public ResponseBuilder request(String request) {
            this.response.request = request;
            return this;
        }

        public ResponseBuilder data(Object data) {
            this.response.data = data;
            return this;
        }

        public MapResponseBodyBuilder mapBodyBuilder() {
            return new MapResponseBodyBuilder(this);
        }

        public <T> Response<T> build(T data) {
            return data(data).build();
        }

        public <T> Response<T> build() {
            return response;
        }

    }

    public static class MapResponseBodyBuilder {

        private ResponseBuilder parent;
        private Map<String, Object> body = new LinkedHashMap<>();

        private MapResponseBodyBuilder(ResponseBuilder parent) {
            this.parent = parent;
        }

        public MapResponseBodyBuilder put(String key, Object val) {
            body.put(key, val);
            return this;
        }

        public MapResponseBodyBuilder putAll(Map<String, Object> bodyMap) {
            body.putAll(bodyMap);
            return this;
        }

        public ResponseBuilder apply() {
            return parent.data(body);
        }

        public Response<Map<String, Object>> build() {
            return parent.data(body).build();
        }

    }

}
