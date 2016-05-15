package com.harmony.umbrella.log4j.parser;

import java.lang.reflect.Method;

import com.harmony.umbrella.log.LogInfo;
import com.harmony.umbrella.util.ReflectionUtils;

/**
 * @author wuxii@foxmail.com
 */
public class NamedParser implements LogInfoParser {

    @Override
    public Object parse(String name, LogInfo logInfo) {
        Method method = ReflectionUtils.findReadMethod(LogInfo.class, name);
        if (method != null) {
            return ReflectionUtils.invokeMethod(method, logInfo);
        }
        return ReflectionUtils.getFieldValue(name, logInfo);
    }

}
