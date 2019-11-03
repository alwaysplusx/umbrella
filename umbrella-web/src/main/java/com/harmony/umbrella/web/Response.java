package com.harmony.umbrella.web;

import com.harmony.umbrella.json.Json;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

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
    private int code;
    /**
     * 异常的信息
     */
    private String msg;
    /**
     * 异常的描述信息
     */
    private String desc;
    /**
     * 日志追踪码
     */
    private String trace;
    /**
     * Response的数据
     */
    private T data;
    /**
     * 请求的资源
     */
    private String request;

    public Response() {
    }

    public Response(int code) {
        this.code = code;
    }

    public Response(int code, T data) {
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

    // static

    public static <T> Response<T> fromJson(String text, Class<T> dataType) {
        return Json.parse(text, null);
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
