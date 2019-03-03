package com.harmony.umbrella.web;

/**
 * @author wuxii
 */
public class ResponseException extends RuntimeException implements ResponseDetails {

    private final int code;
    private String description;

    public ResponseException(int code) {
        this(code, null);
    }

    public ResponseException(ResponseDetails r) {
        this(r, null, null);
    }

    public ResponseException(ResponseDetails r, String description) {
        this(r, description, null);
    }

    public ResponseException(ResponseDetails r, Throwable cause) {
        this(r, cause.getMessage(), cause);
    }

    public ResponseException(ResponseDetails r, String description, Throwable cause) {
        super(r.getMsg(), cause);
        this.code = r.getCode();
        this.description = description;
    }


    public ResponseException(int code, String message) {
        this(code, message, null);
    }

    protected ResponseException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return getMessage();
    }

    public String getDescription() {
        return this.description;
    }

}
