package com.harmony.umbrella.log.jdbc;

import com.harmony.umbrella.log.LogInfo;

/**
 * @author wuxii@foxmail.com
 */
public interface DatabaseManager<T> {

    void startup();

    void shutdown();

    void write(LogInfo logInfo, T event);

    void flush();

}
