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
public interface LogInfoManager {

    /**
     * 启动
     */
    void startup();

    /**
     * 关闭
     */
    void shutdown();

    /**
     * 写入日志
     * 
     * @param logInfo
     *            日志信息
     */
    void write(LogInfo logInfo);

    /**
     * 刷新写步骤
     */
    void flush();

}
