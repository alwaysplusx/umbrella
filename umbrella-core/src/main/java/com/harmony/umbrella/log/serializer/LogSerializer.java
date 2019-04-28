package com.harmony.umbrella.log.serializer;

import com.harmony.umbrella.log.LogInfo;

/**
 * @author wuxii
 */
public interface LogSerializer {

    String serialize(LogInfo logInfo);

}
