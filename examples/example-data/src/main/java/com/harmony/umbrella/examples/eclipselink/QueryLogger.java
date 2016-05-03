package com.harmony.umbrella.examples.eclipselink;

import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLogEntry;
import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;

import com.harmony.umbrella.util.StringUtils;

/**
 * @author wuxii@foxmail.com
 */
public class QueryLogger extends AbstractSessionLog {

    private static final Log log = Logs.getLog(QueryLogger.class);

    @Override
    public void log(SessionLogEntry entry) {
        if ("sql".equals(entry.getNameSpace()) && StringUtils.isNotBlank(entry.getMessage())) {
            String message = entry.getMessage().toLowerCase();
            if (message.startsWith("select")) {
                log.info(message);
            } else if (message.startsWith("update")) {

            } else if (message.startsWith("delete")) {

            } else if (message.startsWith("insert")) {

            }
        }
    }
}
