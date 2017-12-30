package com.harmony.umbrella.log;

import com.harmony.umbrella.log.support.MessageFactoryFactoryBean;

/**
 * 日志基础抽象实现,负责桥接实际实现
 * 
 * @author wuxii@foxmail.com
 */
public abstract class AbstractLog implements Log {

    protected static final String FQCN = AbstractLog.class.getName();

    protected final String className;
    protected final MessageFactory messageFactory;

    public AbstractLog(String className) {
        this.className = className;
        this.messageFactory = MessageFactoryFactoryBean.getMessageFactory();
    }

    public AbstractLog(String className, MessageFactory messageFactory) {
        this.className = className;
        this.messageFactory = messageFactory;
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
     * 通过一定的属性创建相关联的log
     * 
     * @param caller
     *            log创建需要用到的相关属性
     * @return relative log
     */
    protected abstract Log relative(Object caller);

    @Override
    public String getName() {
        return className;
    }

    @Override
    public boolean isTraceEnabled() {
        return isEnabled(Level.TRACE);
    }

    @Override
    public boolean isDebugEnabled() {
        return isEnabled(Level.DEBUG);
    }

    @Override
    public boolean isInfoEnabled() {
        return isEnabled(Level.INFO);
    }

    @Override
    public boolean isWarnEnabled() {
        return isEnabled(Level.WARN);
    }

    @Override
    public boolean isErrorEnabled() {
        return isEnabled(Level.ERROR);
    }

    protected void logIfEnable(Level level, LogInfo logInfo) {
        if (isEnabled(level)) {
            logMessage(level, logInfo);
        }
    }

    protected void logIfEnable(Level level, String message, Object... params) {
        if (isEnabled(level)) {
            Message msg = messageFactory.newMessage(message, params);
            logMessage(level, msg, msg.getThrowable());
        }
    }

    protected void logIfEnable(Level level, String message, Throwable t) {
        if (isEnabled(level)) {
            logMessage(level, messageFactory.newMessage(message), t);
        }
    }

    @Override
    public void trace(Object msg) {
        logIfEnable(Level.TRACE, msg == null ? null : msg.toString());
    }

    @Override
    public void trace(String msg, Object... arguments) {
        logIfEnable(Level.TRACE, msg, arguments);
    }

    @Override
    public void trace(String msg, Throwable t) {
        logIfEnable(Level.TRACE, msg, t);
    }

    @Override
    public void debug(Object msg) {
        logIfEnable(Level.DEBUG, msg == null ? null : msg.toString());
    }

    @Override
    public void debug(String msg, Object... arguments) {
        logIfEnable(Level.DEBUG, msg, arguments);
    }

    @Override
    public void debug(String msg, Throwable t) {
        logIfEnable(Level.DEBUG, msg, t);
    }

    @Override
    public void info(Object msg) {
        logIfEnable(Level.INFO, msg == null ? null : msg.toString());
    }

    @Override
    public void info(String msg, Object... arguments) {
        logIfEnable(Level.INFO, msg, arguments);
    }

    @Override
    public void info(String msg, Throwable t) {
        logIfEnable(Level.INFO, msg, t);
    }

    @Override
    public void warn(Object msg) {
        logIfEnable(Level.WARN, msg == null ? null : msg.toString());
    }

    @Override
    public void warn(String msg, Object... arguments) {
        logIfEnable(Level.WARN, msg, arguments);
    }

    @Override
    public void warn(String msg, Throwable t) {
        logIfEnable(Level.WARN, msg, t);
    }

    @Override
    public void error(Object msg) {
        logIfEnable(Level.ERROR, msg == null ? null : msg.toString());
    }

    @Override
    public void error(String msg, Object... arguments) {
        logIfEnable(Level.ERROR, msg, arguments);
    }

    @Override
    public void error(String msg, Throwable t) {
        logIfEnable(Level.ERROR, msg, t);
    }

    public void log(LogInfo info) {
        logIfEnable(Level.toLevel(info.getLevel()), info);
    }

    @Override
    public MessageFactory getMessageFactory() {
        return messageFactory;
    }

}
