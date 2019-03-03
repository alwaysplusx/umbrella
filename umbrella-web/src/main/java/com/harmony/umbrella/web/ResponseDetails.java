package com.harmony.umbrella.web;

import org.springframework.http.HttpStatus;

/**
 * @author wuxii
 */
public interface ResponseDetails {

    int getCode();

    String getMessage();

    HttpStatus getHttpStatus();

}
