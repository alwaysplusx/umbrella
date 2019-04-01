package com.harmony.umbrella.web.method.bind;

import org.springframework.web.bind.ServletRequestBindingException;

/**
 * @author wuxii
 */
public class MissingCurrentUserException extends ServletRequestBindingException {

    public MissingCurrentUserException(String msg) {
        super(msg);
    }

    public MissingCurrentUserException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
