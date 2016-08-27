package com.harmony.umbrella.log;

import com.harmony.umbrella.log.message.ParameterizedMessageFactory;

/**
 * 日志基础抽象实现,负责桥接实际实现
 * 
 * @author wuxii@foxmail.com
 */
public abstract class AbstractLog implements Log {

    protected static final String FQCN = AbstractLog.class.getName();
    
    protected final String className;
    protected final MessageFactory messageFactory;

    protected boolean isTraceEnabled = false;
    protected boolean isDebugEnabled = false;
    protected boolean isInfoEnabled = true;
    protected boolean isWarnEnabled = true;
    protected boolean isErrorEnabled = true;

    public AbstractLog(String className) {
        this.className = className;
        this.messageFactory = createDefaultMessageFactory();
    }

    public AbstractLog(String className, MessageFactory messageFactory) {
        this.className = className;
        this.messageFactory = messageFactory;
    }

    protected MessageFactory createDefaultMessageFactory() {
        return new ParameterizedMessageFactory();
    }

    /**
     * 记录日志汇总入口
     * <p>
     * 此之前已经对带参数的message做了格式化处理
     *
     * @param level
     *            日志级别
     * @param message
     *            日志信息
     * @param t
     *            日志异常信息
     */
    protected abstract void logMessage(Level level, Message message, Throwable t);

    /**
     * 记录日志汇总入口
     * <p>
     * 此之前已经对带参数的message做了格式化处理
     *
     * @param level
     *            日志级别
     * @param logInfo
     *            日志信息
     */
    protected abstract void logMessage(Level level, LogInfo logInfo);

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return className;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isTraceEnabled() {
        return isTraceEnabled;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDebugEnabled() {
        return isDebugEnabled;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isInfoEnabled() {
        return isInfoEnabled;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isWarnEnabled() {
        return isWarnEnabled;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isErrorEnabled() {
        return isErrorEnabled;
    }

    protected void logIfEnable(Level level, Object message) {
        if (isEnable(level)) {
            Message msg = messageFactory.newMessage(message);
            logMessage(level, msg, msg.getThrowable());
        }
    }

    protected void logIfEnable(Level level, LogInfo logInfo) {
        if (isEnable(level)) {
            logMessage(level, logInfo);
        }
    }

    protected void logIfEnable(Level level, Object message, Throwable t) {
        if (isEnable(level)) {
            logMessage(level, messageFactory.newMessage(message), t);
        }
    }

    protected void logIfEnable(Level level, String message, Object... params) {
        if (isEnable(level)) {
            Message msg = messageFactory.newMessage(message, params);
            logMessage(level, msg, msg.getThrowable());
        }
    }

    protected void logIfEnable(Level level, String message, Throwable t) {
        if (isEnable(level)) {
            logMessage(level, messageFactory.newMessage(message), t);
        }
    }

    public boolean isEnable(Level level) {
        if (level == null) {
            return isInfoEnabled();
        }
        switch (level.getStandardLevel()) {
        case ERROR:
            return isErrorEnabled();
        case WARN:
            return isWarnEnabled();
        case INFO:
            return isInfoEnabled();
        case ALL:
        case TRACE:
            return isTraceEnabled();
        case DEBUG:
            return isDebugEnabled();
        case OFF:
            return false;
        }
        return isInfoEnabled();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void trace(Object msg) {
        logIfEnable(Level.TRACE, msg);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void trace(String msg, Object... arguments) {
        logIfEnable(Level.TRACE, msg, arguments);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void trace(String msg, Throwable t) {
        logIfEnable(Level.TRACE, msg, t);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void trace(LogInfo logInfo) {
        logIfEnable(Level.TRACE, logInfo);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void debug(Object msg) {
        logIfEnable(Level.DEBUG, msg);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void debug(String msg, Object... arguments) {
        logIfEnable(Level.DEBUG, msg, arguments);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void debug(String msg, Throwable t) {
        logIfEnable(Level.DEBUG, msg, t);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void debug(LogInfo logInfo) {
        logIfEnable(Level.DEBUG, logInfo);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void info(Object msg) {
        logIfEnable(Level.INFO, msg);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void info(String msg, Object... arguments) {
        logIfEnable(Level.INFO, msg, arguments);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void info(String msg, Throwable t) {
        logIfEnable(Level.INFO, msg, t);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void info(LogInfo logInfo) {
        logIfEnable(Level.INFO, logInfo);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void warn(Object msg) {
        logIfEnable(Level.WARN, msg);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void warn(String msg, Object... arguments) {
        logIfEnable(Level.WARN, msg, arguments);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void warn(String msg, Throwable t) {
        logIfEnable(Level.WARN, msg, t);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void warn(LogInfo logInfo) {
        logIfEnable(Level.WARN, logInfo);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void error(Object msg) {
        logIfEnable(Level.ERROR, msg);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void error(String msg, Object... arguments) {
        logIfEnable(Level.ERROR, msg, arguments);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void error(String msg, Throwable t) {
        logIfEnable(Level.ERROR, msg, t);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void error(LogInfo logInfo) {
        logIfEnable(Level.ERROR, logInfo);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MessageFactory getMessageFactory() {
        return messageFactory;
    }

}
