package com.harmony.umbrella.log.log4j2;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.ObjectMessage;

import com.harmony.umbrella.log.LogInfo;
import com.harmony.umbrella.log.support.LogWriter;

/**
 * @author wuxii@foxmail.com
 */
public class LogWriterAppender extends AbstractAppender {

    private LogWriter logWriter;

    public LogWriterAppender() {
        this(null);
    }

    public LogWriterAppender(LogWriter logWriter) {
        super("logWriter", null, null);
        this.logWriter = logWriter;
    }

    @Override
    public void initialize() {
        logWriter.startup();
        super.initialize();
    }

    @Override
    public void append(LogEvent event) {
        Message message = event.getMessage();
        if (message instanceof ObjectMessage //
                && ((ObjectMessage) message).getParameter() instanceof LogInfo) {
            logWriter.write((LogInfo) ((ObjectMessage) message).getParameter());
        }
    }

    @Override
    public void stop() {
        super.stop();
        logWriter.shutdown();
    }

    public LogWriter getLogWriter() {
        return logWriter;
    }

    public void setLogWriter(LogWriter logWriter) {
        this.logWriter = logWriter;
    }

}
