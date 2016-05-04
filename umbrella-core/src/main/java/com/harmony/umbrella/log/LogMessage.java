package com.harmony.umbrella.log;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import com.harmony.umbrella.log.Level.StandardLevel;

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

    private String bizModule;
    private Object bizId;

    private String module;
    private String action;

    private Message message;
    private Throwable exception;
    private Level level;

    private String operator;
    private Object operatorId;

    private Object result;

    private long startTime = -1;
    private long finishTime = -1;

    private String stack;
    private String threadName;

    private Map<String, Object> context;

    public LogMessage(Log log) {
        this.log = log;
        this.messageFactory = log.getMessageFactory();
    }

    public static LogMessage create(Log log) {
        return new LogMessage(log);
    }

    /**
     * 设置业务数据的id
     *
     * @param bizId
     *            业务数据的id
     * @return current logMessage
     */
    public LogMessage bizId(Object bizId) {
        this.bizId = bizId;
        return this;
    }

    /**
     * 设置业务模块
     *
     * @param bizModule
     *            业务模块
     * @return current logMessage
     */
    public LogMessage bizModule(String bizModule) {
        this.bizModule = bizModule;
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
        this.exception = exception;
        return this;
    }

    /**
     * 设置业务数据的操作人
     *
     * @param username
     *            操作人名称
     * @return current logMessage
     */
    public LogMessage operator(String username) {
        this.operator = username;
        return this;
    }

    public LogMessage operatorId(Object operatorId) {
        this.operatorId = operatorId;
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

    public LogInfo asInfo() {
        return new LogInfo() {

            @Override
            public boolean isException() {
                return exception != null;
            }

            @Override
            public Date getStartTime() {
                return new Date(startTime);
            }

            @Override
            public Object getResult() {
                return result;
            }

            @Override
            public Object getOperatorId() {
                return operatorId;
            }

            @Override
            public String getOperator() {
                return operator;
            }

            @Override
            public String getModule() {
                return module;
            }

            @Override
            public Message getMessage() {
                return message;
            }

            @Override
            public Level getLevel() {
                return level;
            }

            @Override
            public Date getFinishTime() {
                return new Date(finishTime);
            }

            @Override
            public Throwable getException() {
                return exception;
            }

            @Override
            public String getBizModule() {
                return bizModule;
            }

            @Override
            public Object getBizId() {
                return bizId;
            }

            @Override
            public String getAction() {
                return action;
            }

            @Override
            public String getStack() {
                return stack;
            }

            @Override
            public String getThreadName() {
                return threadName;
            }

            @Override
            public long use() {
                return (startTime == -1 || finishTime == -1) ? -1 : finishTime - startTime;
            }

            @Override
            public String toString() {
                return LogMessage.this.toString();
            }

            @Override
            public Map<String, Object> getContext() {
                return context;
            }

        };
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append("{\"bizModule\":\"").append(bizModule)//
                .append("\", \"bizId\":\"").append(bizId)//
                .append("\", \"module\":\"").append(module)//
                .append("\", \"action\":\"").append(action)//
                .append("\", \"message\":\"").append(((message != null) ? message.getFormattedMessage() : null))//
                .append("\", \"level\":\"").append(level == null ? null : level.getName())//
                .append("\", \"operator\":\"").append(operator)//
                .append("\", \"result\":\"").append(result)//
                .append("\", \"stack\":\"").append(stack)//
                .append("\", \"threadName\":\"").append(threadName)//
                .append("\"}");
        return out.toString();
    }

}
