package com.harmony.umbrella.log;

/**
 * @author apache log4j2
 */
public interface MessageFactory {

    Message newMessage(Object message);

    Message newMessage(String message);

    Message newMessage(String message, Object... params);
}
