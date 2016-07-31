package com.harmony.umbrella.log.db;

import com.harmony.umbrella.log.LogInfo;

/**
 * @author wuxii@foxmail.com
 */
public interface LogInfoParser {

    Object parse(String name, LogInfo logInfo);

}
