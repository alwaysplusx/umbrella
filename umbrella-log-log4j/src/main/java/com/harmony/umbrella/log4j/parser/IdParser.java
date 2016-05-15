package com.harmony.umbrella.log4j.parser;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import com.harmony.umbrella.log.LogInfo;

/**
 * @author wuxii@foxmail.com
 */
public class IdParser implements LogInfoParser {

    private static final AtomicLong id = new AtomicLong();

    @Override
    public Object parse(String name, LogInfo logInfo) {
        if (name.equalsIgnoreCase("#uuid")) {
            return UUID.randomUUID().toString();
        } else if (name.equalsIgnoreCase("#id")) {
            return id.getAndIncrement();
        }
        return null;
    }

}
