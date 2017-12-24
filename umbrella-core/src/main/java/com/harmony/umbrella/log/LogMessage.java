package com.harmony.umbrella.log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.harmony.umbrella.log.Level.StandardLevel;
import com.harmony.umbrella.log.support.MessageFactoryFactoryBean;

/**
 * 统一日志消息
 *
 * @author wuxii@foxmail.com
 */
public class LogMessage {

    public static final Level DEFAULT_LEVEL = Level.INFO;

    public static final String LOGMESSAGE_FQNC = LogMessage.class.getName();

    private Log log;
    private MessageFactory messageFactory;

    private final String id;
    private String module;
    private String action;

    private Object key;

    private Message message;
    private Throwable throwable;
    private Level level;

    private String username;
    private Long userId;
    private String clientId;

    private Object result;

    private long startTime = -1;
    private long finishTime = -1;

    private String stack;
    private String threadName;

    private Map<String, Object> context;

    public LogMessage(Log log, MessageFactory messageFactory) {
        this.log = log;
        this.messageFactory = messageFactory;
        this.id = messageId();
    }

    public static LogMessage create(Log log) {
        return new LogMessage(log, MessageFactoryFactoryBean.getMessageFactory());
    }

    public static LogMessage create(Log log, MessageFactory messageFactory) {
        return new LogMessage(log, messageFactory);
    }

    /**
     * 设置业务数据的id
     *
     * @param key
     *            业务数据的id
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
     * @param action
     *            日志表示的动作
     * @return current logMessage
     */
    public LogMessage action(String action) {
        this.action = action;
        return this;
    }

    /**
     * 设置结果
     *
     * @param result
     *            结果
     * @return current logMessage
     */
    public LogMessage result(Object result) {
        this.result = result;
        return this;
    }

    public LogMessage exception(Throwable exception) {
        this.throwable = exception;
        return this;
    }

    /**
     * 设置业务数据的操作人
     *
     * @param username
     *            操作人名称
     * @return current logMessage
     */
    public LogMessage username(String username) {
        this.username = username;
        return this;
    }

    public LogMessage userId(Long userId) {
        this.userId = userId;
        return this;
    }

    public LogMessage clientId(String clientId) {
        this.clientId = clientId;
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
     * @param startTime
     *            开始时间
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
     * @param finishTime
     *            结束时间
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
     * 用于收集其他扩展属性
     * <p>
     * 当要记录http相关信息时使用
     *
     * @param key
     * @param value
     * @return
     */
    public LogMessage put(String key, Object value) {
        if (this.context == null) {
            this.context = new HashMap<String, Object>();
        }
        this.context.put(key, value);
        return this;
    }

    /**
     * 设置日志级别
     *
     * @param level
     *            日志级别
     * @return current logMessage
     */
    public LogMessage level(Level level) {
        this.level = level;
        return this;
    }

    public LogMessage level(StandardLevel level) {
        this.level = Level.toLevel(level.name());
        return this;
    }

    public LogMessage currentStack() {
        this.stack = Logs.fullyQualifiedClassName(LogMessage.LOGMESSAGE_FQNC, 1);
        return this;
    }

    public LogMessage currentThread() {
        this.threadName = Thread.currentThread().getName();
        return this;
    }

    public LogMessage stack(String stack) {
        this.stack = stack;
        return this;
    }

    public LogMessage threadName(String threadName) {
        this.threadName = threadName;
        return this;
    }

    /**
     * 调用日志log记录本条日志
     */
    public void log() {
        log(level == null ? DEFAULT_LEVEL : level);
    }

    /**
     * 调用日志log记录本条日志
     *
     * @param level
     *            日志级别
     */
    public void log(Level level) {
        this.level = level;

        Log relative = log.relative(LOGMESSAGE_FQNC);
        LogInfo msg = asInfo();

        switch (level.standardLevel) {
        case ERROR:
            relative.error(msg);
            break;
        case WARN:
            relative.warn(msg);
            break;
        case INFO:
            relative.info(msg);
            break;
        case DEBUG:
            relative.debug(msg);
            break;
        case ALL:
        case TRACE:
            relative.trace(msg);
            break;
        case OFF:
            break;
        default:
            break;
        }
    }

    private static String messageId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public LogInfo asInfo() {
        return new LogInfoImpl(this);
    }

    private static final class LogInfoImpl implements LogInfo {

        private static final long serialVersionUID = 1L;

        private String messageId;
        private String module;
        private String action;
        private String message;
        private Throwable throwable;
        private StandardLevel level;
        private Object result;
        private long requestTime;
        private long responseTime;
        private String username;
        private Long userId;
        private String clientId;

        private String stackLocation;
        private String threadName;

        private Object key;

        private Map<String, Object> contextMap;

        LogInfoImpl(LogMessage m) {
            this.messageId = m.id;
            this.module = m.module;
            this.action = m.action;
            this.message = m.message == null ? null : m.message.getFormattedMessage();
            this.throwable = m.throwable;
            this.level = m.level == null ? null : m.level.standardLevel;
            this.result = m.result;
            this.requestTime = m.startTime;
            this.responseTime = m.finishTime;
            this.username = m.username;
            this.userId = m.userId;
            this.clientId = m.clientId;
            this.key = m.key;
            this.stackLocation = m.stack;
            this.threadName = m.threadName;
            this.contextMap = m.context;
        }

        @Override
        public String getMessageId() {
            return messageId;
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
        public Object getResult() {
            return result;
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
        public StandardLevel getLevel() {
            return level;
        }

        @Override
        public String getUsername() {
            return username;
        }

        @Override
        public Long getUserId() {
            return userId;
        }

        @Override
        public String getClientId() {
            return clientId;
        }

        @Override
        public String getStackLocation() {
            return stackLocation;
        }

        @Override
        public String getThreadName() {
            return threadName;
        }

        @Override
        public Map<String, Object> getContext() {
            return Collections.unmodifiableMap(contextMap);
        }

        private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
            this.action = ois.readUTF();
            this.contextMap = (Map<String, Object>) ois.readObject();
            this.key = ois.readObject();
            this.level = (StandardLevel) ois.readObject();
            this.message = ois.readUTF();
            this.module = ois.readUTF();
            this.clientId = ois.readUTF();
            this.userId = ois.readLong();
            this.username = ois.readUTF();
            this.requestTime = ois.readLong();
            this.responseTime = ois.readLong();
            this.result = ois.readObject();
            this.stackLocation = ois.readUTF();
            this.threadName = ois.readUTF();
            this.throwable = (Throwable) ois.readObject();
        }

        private void writeObject(ObjectOutputStream oos) throws IOException {
            oos.writeUTF(this.action);
            oos.writeObject(this.contextMap);
            oos.writeObject(this.key);
            oos.writeObject(this.level);
            oos.writeUTF(this.message);
            oos.writeUTF(this.module);
            oos.writeUTF(this.clientId);
            oos.writeObject(this.userId);
            oos.writeUTF(this.username);
            oos.writeLong(this.requestTime);
            oos.writeLong(this.responseTime);
            oos.writeObject(this.result);
            oos.writeUTF(this.stackLocation);
            oos.writeUTF(this.threadName);
            oos.writeObject(this.throwable);
        }

        @Override
        public String toString() {
            StringBuilder out = new StringBuilder();
            out.append("{\n        module : ").append(module)//
                    .append("\n        action : ").append(action)//
                    .append("\n           key : ").append(key)//
                    .append("\n       message : ").append(message)//
                    .append("\n         level : ").append(level)//
                    .append("\n      username : ").append(username)//
                    .append("\n        client : ").append(clientId)//
                    .append("\n        result : ").append(result)//
                    .append("\n         stack : ").append(stackLocation)//
                    .append("\n           use : ").append(interval(requestTime, responseTime))//
                    .append("\n}");
            return out.toString();
        }
    }

    private static long interval(long start, long end) {
        return (start == -1 || end == -1) ? -1 : end - start;
    }

}
