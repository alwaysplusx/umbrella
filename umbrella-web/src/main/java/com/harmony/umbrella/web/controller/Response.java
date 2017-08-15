package com.harmony.umbrella.web.controller;

import java.util.LinkedHashMap;

/**
 * FIXME 参数顺序修改
 * 
 * @author wuxii@foxmail.com
 */
public final class Response extends LinkedHashMap<String, Object> {

    private static final long serialVersionUID = -188009097825662248L;

    private Response() {
    }

    public static Response error(int code, String error, String description) {
        return errorBulider()//
                .code(code)//
                .error(error)//
                .description(description)//
                .build();
    }

    public static Response error(int code, String error) {
        return errorBulider()//
                .code(code)//
                .error(error)//
                .build();
    }

    public static ErrorResponseBuilder errorBulider() {
        return new ErrorResponseBuilder();
    }

    public static ResponseBuilder successBuilder() {
        return new ResponseBuilder();
    }

    protected static class AbstractResponseBuider<T extends AbstractResponseBuider> {

        protected final Response response = new Response();

        public T param(String key, Object value) {
            response.put(key, value);
            return (T) this;
        }

        public Response build() {
            return response;
        }

    }

    public static class ResponseBuilder extends AbstractResponseBuider<ResponseBuilder> {

    }

    public static class ErrorResponseBuilder extends AbstractResponseBuider<ErrorResponseBuilder> {

        public ErrorResponseBuilder code(int code) {
            return param("error_code", code);
        }

        public ErrorResponseBuilder error(String error) {
            return param("error", error);
        }

        public ErrorResponseBuilder description(String description) {
            return param("description", description);
        }

    }

}
