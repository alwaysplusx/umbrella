package com.harmony.umbrella.web;

/**
 * @author wuxii
 */
public class ResponseException extends RuntimeException {

    private final int code;

    public ResponseException(int code) {
        super();
        this.code = code;
    }

    public ResponseException(int code, String message) {
        this(code, message, null);
    }

    public ResponseException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}
