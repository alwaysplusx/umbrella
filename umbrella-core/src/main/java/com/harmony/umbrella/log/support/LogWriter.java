package com.harmony.umbrella.log.support;

import com.harmony.umbrella.log.LogInfo;

/**
 * 写日志工具.写一次日志完整的步骤可划分为
 * 
 * <pre>
 * startup
 *   write - flush
 *   write - flush
 *   ...
 * shutdown
 * </pre>
 * 
 * @author wuxii@foxmail.com
 */
public interface LogWriter {

    /**
     * 启动
     */
    void startup();

    /**
     * 写入日志
     * 
     * @param info
     *            日志信息
     */
    void write(LogInfo info);

    /**
     * 刷新写步骤
     */
    void flush();

    /**
     * 关闭
     */
    void shutdown();

}
