package com.harmony.umbrella.web;

import org.springframework.http.HttpStatus;

/**
 * @author wuxii
 */
public class ResponseException extends RuntimeException implements ResponseDetails {

    private final int code;
    private String description;
    private final HttpStatus httpStatus;

    public ResponseException(int code) {
        this(code, null);
    }

    public ResponseException(ResponseDetails responseDetails) {
        this(responseDetails, null, null);
    }

    public ResponseException(ResponseDetails responseDetails, String description) {
        this(responseDetails, description, null);
    }

    public ResponseException(ResponseDetails responseDetails, String description, Throwable cause) {
        super(responseDetails.getMessage(), cause);
        this.code = responseDetails.getCode();
        this.httpStatus = responseDetails.getHttpStatus();
        this.description = description;
    }

    public ResponseException(int code, String message) {
        this(code, message, null);
    }

    public ResponseException(int code, String message, Throwable cause) {
        this(code, message, HttpStatus.OK, cause);
    }

    protected ResponseException(int code, String message, HttpStatus httpStatus, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.httpStatus = httpStatus;
    }

    public int getCode() {
        return code;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getDescription() {
        return this.description;
    }
    
}
