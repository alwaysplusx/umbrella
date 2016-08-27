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

    /**
     * 带格式化的消息模版记录trace的日志
     * 
     * @param msg
     *            日志信息
     * @param arguments
     *            模版内容
     */
    void trace(String msg, Object... arguments);

    /**
     * 记录带异常的日志信息
     * 
     * @param msg
     *            日志信息
     * @param t
     *            异常信息
     */
    void trace(String msg, Throwable t);

    /**
     * 记录日志内容
     * 
     * @param logInfo
     *            日志内容
     */
    void trace(LogInfo logInfo);

    /**
     * 检测是否为debug可用
     * 
     * @return deubg flag
     */
    boolean isDebugEnabled();

    /**
     * 记录debug级别的日志
     * 
     * @param msg
     *            日志内容
     */
    void debug(Object msg);

    /**
     * 记录debug级别的日志
     * 
     * @param msg
     *            日志消息模版
     * @param arguments
     *            模版内容
     */
    void debug(String msg, Object... arguments);

    /**
     * 记录debug级别的日志
     * 
     * @param msg
     *            日志消息
     * @param t
     *            异常信息
     */
    void debug(String msg, Throwable t);

    /**
     * 记录debug级别的日志
     * 
     * @param logInfo
     *            日志内容
     */
    void debug(LogInfo logInfo);

    /**
     * 检查info级别的日志
     * 
     * @return info可用标识
     */
    boolean isInfoEnabled();

    /**
     * 记录info级别的日志
     * 
     * @param msg
     *            日志内容
     */
    void info(Object msg);

    /**
     * 记录info级别的日志
     * 
     * @param msg
     *            日志模版
     * @param arguments
     *            模版内容
     */
    void info(String msg, Object... arguments);

    /**
     * 记录info级别的日志
     * 
     * @param msg
     *            日志消息
     * @param t
     *            异常信息
     */
    void info(String msg, Throwable t);

    /**
     * 记录info级别的日志
     * 
     * @param logInfo
     *            日志内容
     */
    void info(LogInfo logInfo);

    /**
     * 检查warn级别是否可用
     * 
     * @return warn标识
     */
    boolean isWarnEnabled();

    /**
     * 记录warn级别的日志
     * 
     * @param msg
     *            警告信息
     */
    void warn(Object msg);

    /**
     * 记录warn级别的日志
     * 
     * @param msg
     *            警告信息模版
     * @param arguments
     *            模版内容
     */
    void warn(String msg, Object... arguments);

    /**
     * 记录warn级别的日志
     * 
     * @param msg
     *            警告内容
     * @param t
     *            异常信息
     */
    void warn(String msg, Throwable t);

    /**
     * 记录warn级别的日志
     * 
     * @param logInfo
     *            警告内容
     */
    void warn(LogInfo logInfo);

    /**
     * 检查error级别是否可用
     * 
     * @return error标识
     */
    boolean isErrorEnabled();

    /**
     * 记录error级别的日志
     * 
     * @param msg
     *            error信息
     */
    void error(Object msg);

    /**
     * 记录error级别的日志
     * 
     * @param msg
     *            error异常内容
     * @param arguments
     *            模版内容
     */
    void error(String msg, Object... arguments);

    /**
     * 记录error级别的日志
     * 
     * @param msg
     *            error消息
     * @param t
     *            异常信息
     */
    void error(String msg, Throwable t);

    /**
     * 记录error级别的日志
     * 
     * @param logInfo
     *            error内容
     */
    void error(LogInfo logInfo);

    /**
     * 消息格式化工厂
     * 
     * @return 消息格式化工厂
     */
    MessageFactory getMessageFactory();

}
