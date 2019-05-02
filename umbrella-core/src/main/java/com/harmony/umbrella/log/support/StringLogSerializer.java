package com.harmony.umbrella.log.support;

import com.harmony.umbrella.log.LogInfo;
import com.harmony.umbrella.log.serializer.LogSerializer;

/**
 * @author wuxii
 */
public class StringLogSerializer implements LogSerializer {

    public static final StringLogSerializer INSTANCE = new StringLogSerializer();

    @Override
    public String serialize(LogInfo logInfo) {
        return logInfo.toString();
    }

}
