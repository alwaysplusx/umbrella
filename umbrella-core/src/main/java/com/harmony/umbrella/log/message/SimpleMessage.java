package com.harmony.umbrella.log.message;

import java.io.Serializable;

import com.harmony.umbrella.log.Message;

/**
 * The simplest possible implementation of Message. It just returns the String
 * given as the constructor argument.
 */
public class SimpleMessage implements Message, Serializable {

    private static final long serialVersionUID = 3230275896230172134L;

    private final String message;

    /**
     * Basic constructor.
     */
    public SimpleMessage() {
        this(null);
    }

    /**
     * Constructor that includes the message.
     * 
     * @param message
     *            The String message.
     */
    public SimpleMessage(final String message) {
        this.message = message;
    }

    /**
     * Returns the message.
     * 
     * @return the message.
     */
    @Override
    public String getFormattedMessage() {
        return message;
    }

    /**
     * Returns the message.
     * 
     * @return the message.
     */
    @Override
    public String getFormat() {
        return message;
    }

    /**
     * Returns null since there are no parameters.
     * 
     * @return null.
     */
    @Override
    public Object[] getParameters() {
        return null;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final SimpleMessage that = (SimpleMessage) o;

        return !(message != null ? !message.equals(that.message) : that.message != null);
    }

    @Override
    public int hashCode() {
        return message != null ? message.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "SimpleMessage[message=" + message + ']';
    }

    /**
     * Always returns null.
     *
     * @return null
     */
    @Override
    public Throwable getThrowable() {
        return null;
    }
}
