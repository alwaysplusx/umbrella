package com.harmony.umbrella.message;

/**
 * @author wuxii@foxmail.com
 */
public class ApplicationMessage implements Message {

    private static final long serialVersionUID = -9046491708985395608L;

    private final String message;

    public ApplicationMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
