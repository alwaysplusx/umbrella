package com.harmony.umbrella.log.log4j;

import org.apache.log4j.Appender;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

import com.harmony.umbrella.log.LogInfo;
import com.harmony.umbrella.log.support.LogWriter;

/**
 * @author wuxii@foxmail.com
 */
public class LogWriterAppender extends AppenderSkeleton implements Appender {

    private LogWriter logWriter;
    private boolean initialized;

    public LogWriterAppender() {
    }

    public LogWriterAppender(LogWriter logWriter) {
        this.logWriter = logWriter;
    }

    public synchronized void init() {
        if (!initialized) {
            logWriter.startup();
            initialized = true;
        }
    }

    @Override
    protected void append(LoggingEvent event) {
        Object msg = event.getMessage();
        if (msg instanceof LogInfo) {
            if (!initialized) {
                init();
            }
            logWriter.write((LogInfo) msg);
        }
    }

    @Override
    public boolean requiresLayout() {
        return false;
    }

    @Override
    public void close() {
        logWriter.shutdown();
    }

    public LogWriter getLogWriter() {
        return logWriter;
    }

    public void setLogWriter(LogWriter logWriter) {
        this.logWriter = logWriter;
    }

}
