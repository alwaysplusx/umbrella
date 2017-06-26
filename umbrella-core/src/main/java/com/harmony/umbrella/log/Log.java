package com.harmony.umbrella.log;

/**
 * 暴露在外部供调用的api接口
 * <p>
 * 基本与slf4j接口部分一致
 * 
 * @author wuxii@foxmail.com
 */
public interface Log {

    /**
     * log的名称
     * 
     * @return log名称
     */
    String getName();

    /**
     * 通过一定的属性创建相关联的log
     * 
     * @param relativeProperties
     *            log创建需要用到的相关属性
     * @return relative log
     */
    Log relative(Object relativeProperties);

    /**
     * 检测trace是否可用
     * 
     * @return true 可用
     */
    boolean isTraceEnabled();

    /**
     * 记录trace级别的日志， msg使用{@linkplain Object#toString()}方式，如果msg为
     * <code>null</code>直接记录"null"字符串
     * 
     * @param msg
     *            被记录的消息
     */
    void trace(Object msg);

    void trace(String msg, Object... arguments);

    void trace(String msg, Throwable t);

    void trace(LogInfo logInfo);

    boolean isDebugEnabled();

    void debug(Object msg);

    void debug(String msg, Object... arguments);

    void debug(String msg, Throwable t);

    void debug(LogInfo logInfo);

    boolean isInfoEnabled();

    void info(Object msg);

    void info(String msg, Object... arguments);

    void info(String msg, Throwable t);

    void info(LogInfo logInfo);

    boolean isWarnEnabled();

    void warn(Object msg);

    void warn(String msg, Object... arguments);

    void warn(String msg, Throwable t);

    void warn(LogInfo logInfo);

    boolean isErrorEnabled();

    void error(Object msg);

    void error(String msg, Object... arguments);

    void error(String msg, Throwable t);

    void error(LogInfo logInfo);

    MessageFactory getMessageFactory();

}
