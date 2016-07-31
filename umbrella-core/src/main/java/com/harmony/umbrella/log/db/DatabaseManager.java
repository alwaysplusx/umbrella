package com.harmony.umbrella.log.db;

import com.harmony.umbrella.log.LogInfo;

/**
 * @author wuxii@foxmail.com
 */
public interface DatabaseManager {

    void startup();

    void shutdown();

    void write(LogInfo logInfo);

    void flush();

}
