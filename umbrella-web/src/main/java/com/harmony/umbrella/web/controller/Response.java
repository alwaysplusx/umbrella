package com.harmony.umbrella.web.controller;

import java.util.LinkedHashMap;

/**
 * FIXME
 * 
 * @author wuxii@foxmail.com
 */
public final class Response extends LinkedHashMap<String, Object> {

    private static final long serialVersionUID = -188009097825662248L;

    private Response() {
    }

    public static ErrorResponseBuilder error(String error) {
        return null;
    }

    protected static class AbstractResponseBuider<T extends AbstractResponseBuider> {

        public T param(String key, Object value) {
            return (T) this;
        }

    }

    public static class ResponseBuilder extends AbstractResponseBuider<ResponseBuilder> {

    }

    public static class ErrorResponseBuilder extends AbstractResponseBuider<ErrorResponseBuilder> {

        public ErrorResponseBuilder code(int code) {
            return this;
        }

        public ErrorResponseBuilder error(String error) {
            return this;
        }

        public Response build() {
            return null;
        }

    }

}
