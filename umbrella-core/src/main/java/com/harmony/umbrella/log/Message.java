package com.harmony.umbrella.log;

/**
 * @author wuxii@foxmail.com
 */
public interface Message {

    String getFormat();

    String getFormattedMessage();

    Object[] getParameters();

    Throwable getThrowable();

}
