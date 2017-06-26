package com.harmony.umbrella.log;

import java.io.Serializable;

/**
 * @author apache log4j2
 */
public interface Message extends Serializable {

    String getFormattedMessage();

    String getFormat();

    Object[] getParameters();

    Throwable getThrowable();
}
