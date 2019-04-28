package com.harmony.umbrella.log.serializer;

import com.harmony.umbrella.log.LogInfo;

/**
 * @author wuxii
 */
public interface LogDeserializer {

    boolean canRead(String text);

    LogInfo descrializer(String text);

}
