package com.harmony.umbrella.log;

import com.harmony.umbrella.log.serializer.LogSerializer;
import com.harmony.umbrella.log.support.MessageFactoryFactoryBean;
import org.slf4j.Logger;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * 统一日志消息
 *
 * @author wuxii@foxmail.com
 */
public class LogMessage {

    private final MessageFactory messageFactory;

    private String messageId = messageId();
    private String traceId = messageId();

    private String module;
    private String action;

    private Object key;

    private Message message;
    private Throwable throwable;
    private Level level;

    private Object userId;
    private String username;

    private long startTime = -1;
    private long finishTime = -1;

    private String threadFrame;
    private String thread;

    public LogMessage() {
        this(MessageFactoryFactoryBean.getMessageFactory());
    }

    public LogMessage(MessageFactory messageFactory) {
        this.messageFactory = messageFactory;
        this.currentThread();
        this.start();
    }

    public LogMessage traceId(String traceId) {
        this.traceId = traceId;
        return this;
    }

    /**
     * 设置业务数据的id
     *
     * @param key 业务数据的id
     * @return current logMessage
     */
    public LogMessage key(Object key) {
        this.key = key;
        return this;
    }

    public LogMessage message(String message) {
        this.message = messageFactory.newMessage(message);
        return this;
    }

    public LogMessage message(String message, Object... args) {
        this.message = messageFactory.newMessage(message, args);
        return this;
    }

    public LogMessage message(Message message) {
        this.message = message;
        return this;
    }

    /**
     * 设置日志所属于的模块
     *
     * @param module
     * @return
     */
    public LogMessage module(String module) {
        this.module = module;
        return this;
    }

    /**
     * 设置日志所表示的动作
     *
     * @param action 日志表示的动作
     * @return current logMessage
     */
    public LogMessage action(String action) {
        this.action = action;
        return this;
    }

    public LogMessage error(Throwable exception) {
        this.throwable = exception;
        return this;
    }

    /**
     * 设置业务数据的操作人
     *
     * @param username 操作人名称
     * @return current logMessage
     */
    public LogMessage username(String username) {
        this.username = username;
        return this;
    }

    public LogMessage userId(Object userId) {
        this.userId = userId;
        return this;
    }

    /**
     * 设置开始时间 startTime = Calendar.getInstance();
     *
     * @return current logMessage
     */
    public LogMessage start() {
        this.startTime = System.currentTimeMillis();
        return this;
    }

    public LogMessage start(long startTime) {
        this.startTime = startTime;
        return this;
    }

    /**
     * 设置开始时间
     *
     * @param startTime 开始时间
     * @return current logMessage
     */
    public LogMessage start(Calendar startTime) {
        this.startTime = startTime.getTimeInMillis();
        return this;
    }

    public LogMessage start(Date startTime) {
        this.startTime = startTime.getTime();
        return this;
    }

    /**
     * 设置结束时间 finishTime = Calendar.getInstance();
     *
     * @return current logMessage
     */
    public LogMessage finish() {
        this.finishTime = System.currentTimeMillis();
        return this;
    }

    public LogMessage finish(long finishTime) {
        this.finishTime = finishTime;
        return this;
    }

    /**
     * 设置结束时间
     *
     * @param finishTime 结束时间
     * @return current logMessage
     */
    public LogMessage finish(Calendar finishTime) {
        this.finishTime = finishTime.getTimeInMillis();
        return this;
    }

    public LogMessage finish(Date finishTime) {
        this.finishTime = finishTime.getTime();
        return this;
    }

    /**
     * 设置日志级别
     *
     * @param level 日志级别
     * @return current logMessage
     */
    public LogMessage level(Level level) {
        this.level = level;
        return this;
    }

    public LogMessage threadFrame(String stack) {
        this.threadFrame = stack;
        return this;
    }

    public LogMessage currentThreadFrame() {
        return threadFrame(currentQualifiedClassName());
    }

    public LogMessage thread(Thread thread) {
        this.thread = thread.getName();
        return this;
    }

    public LogMessage thread(String thread) {
        this.thread = thread;
        return this;
    }

    public LogMessage currentThread() {
        this.thread = Thread.currentThread().getName();
        return this;
    }

    public LogInfo asInfo() {
        return new LogInfoImpl(this);
    }

    public void log(Logger log) {
        log(log, null);
    }

    public void log(Logger log, LogSerializer serializer) {
        level = level == null ? Level.INFO : level;
        LogInfo info = asInfo();
        String message = serializer != null ? serializer.serialize(info) : info.toString();
        switch (level) {
            case TRACE:
                log.trace(message);
                break;
            case DEBUG:
                log.debug(message);
                break;
            case INFO:
                log.info(message);
                break;
            case WARN:
                log.warn(message);
                break;
            case ERROR:
                log.error(message);
                break;
        }
    }

    public static String messageId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    private static String currentQualifiedClassName() {
        StackTraceElement current = null;
        StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        for (int i = stackTrace.length - 1; i >= 0; i--) {
            if (stackTrace[i].getClassName().equals(LOG_MESSAGE_CLASS_NAME)) {
                break;
            }
            current = stackTrace[i];
        }
        return current == null ? "unknown" : current.toString();
    }

    private static final String LOG_MESSAGE_CLASS_NAME = LogMessage.class.getName();

    private static class LogInfoImpl implements LogInfo {

        private String messageId;
        private String traceId;
        private String module;
        private String action;

        private Object key;

        private String message;
        private Throwable throwable;
        private Level level;

        private Object userId;
        private String username;

        private long requestTime;
        private long responseTime;

        private String threadFrame;
        private String thread;

        LogInfoImpl(LogMessage logMessage) {
            this.messageId = logMessage.messageId;
            this.traceId = logMessage.traceId;
            this.module = logMessage.module;
            this.action = logMessage.action;
            this.key = logMessage.key;
            this.message = logMessage.message != null ? logMessage.message.getFormattedMessage() : "";
            this.throwable = logMessage.throwable;
            this.level = logMessage.level;
            this.userId = logMessage.userId;
            this.username = logMessage.username;
            this.requestTime = logMessage.startTime;
            this.responseTime = logMessage.finishTime;
            this.threadFrame = logMessage.threadFrame;
            this.thread = logMessage.thread;
        }

        @Override
        public String getMessageId() {
            return messageId;
        }

        @Override
        public String getTraceId() {
            return traceId;
        }

        @Override
        public String getModule() {
            return module;
        }

        @Override
        public String getAction() {
            return action;
        }

        @Override
        public Object getKey() {
            return key;
        }

        @Override
        public String getMessage() {
            return message;
        }

        @Override
        public Date getRequestTime() {
            return requestTime <= 0 ? null : new Date(requestTime);
        }

        @Override
        public Date getResponseTime() {
            return responseTime <= 0 ? null : new Date(responseTime);
        }

        @Override
        public Throwable getThrowable() {
            return throwable;
        }

        @Override
        public Level getLevel() {
            return level;
        }

        @Override
        public Object getUserId() {
            return userId;
        }

        @Override
        public String getUsername() {
            return username;
        }

        @Override
        public String getThread() {
            return thread;
        }

        @Override
        public String getThreadFrame() {
            return threadFrame;
        }

        @Override
        public String toString() {
            StringBuilder out = new StringBuilder();
            out.append("{\n        'module':'").append(module).append("',")
                    .append("\n    'message_id':'").append(messageId).append("',")
                    .append("\n      'trace_id':'").append(traceId).append("',")
                    .append("\n         'level':'").append(level).append("',")
                    .append("\n           'key':'").append(key).append("',")
                    .append("\n        'action':'").append(action).append("',")
                    .append("\n       'message':'").append(message).append("',")
                    .append("\n      'username':'").append(username).append("',")
                    .append("\n  'thread_frame':'").append(threadFrame).append("',")
                    .append("\n     'has_error':").append(throwable != null).append(",")
                    .append("\n           'use':").append(interval(requestTime, responseTime))
                    .append("\n}");
            return out.toString();
        }

        private static long interval(long start, long end) {
            return (start == -1 || end == -1) ? -1 : end - start;
        }

    }

}
