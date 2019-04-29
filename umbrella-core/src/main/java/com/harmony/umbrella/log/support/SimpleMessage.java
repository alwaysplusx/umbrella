package com.harmony.umbrella.log.support;

import com.harmony.umbrella.log.Message;

class SimpleMessage implements Message {

    private static final long serialVersionUID = 6434807138233803162L;

    private String format;
    private String message;
    private Object[] parameters;
    private Throwable throwable;

    SimpleMessage(String message) {
        this.message = message;
    }

    SimpleMessage(String format, Object[] parameters, Throwable throwable) {
        this.format = format;
        this.parameters = parameters;
        this.throwable = throwable;
    }

    SimpleMessage(String format, String message, Object[] parameters, Throwable throwable) {
        this.format = format;
        this.message = message;
        this.parameters = parameters;
        this.throwable = throwable;
    }

    @Override
    public String getFormattedMessage() {
        if (message == null) {
            message = String.format(format, parameters);
        }
        return message;
    }

    @Override
    public String getFormat() {
        return format;
    }

    @Override
    public Object[] getParameters() {
        return parameters;
    }

    @Override
    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

}