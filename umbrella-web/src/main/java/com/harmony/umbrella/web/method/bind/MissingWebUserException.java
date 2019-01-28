package com.harmony.umbrella.web.method.bind;

import org.springframework.web.bind.ServletRequestBindingException;

/**
 * @author wuxii
 */
public class MissingWebUserException extends ServletRequestBindingException {

    public MissingWebUserException(String msg) {
        super(msg);
    }

    public MissingWebUserException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
