package com.harmony.umbrella.log.interceptor;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.harmony.umbrella.log.LoggingException;
import com.harmony.umbrella.log.annotation.Logging;
import com.harmony.umbrella.log.template.LoggingTemplate;

/**
 * @author wuxii@foxmail.com
 */
public class LoggingTemplateFactory {

    private static LoggingTemplateFactory INSTANCE;

    private Map<Logging, LoggingTemplate> cache = new ConcurrentHashMap<>();

    protected LoggingTemplateFactory() {
    }

    public static LoggingTemplateFactory getInstance() {
        if (INSTANCE == null) {
            synchronized (LoggingTemplateFactory.class) {
                if (INSTANCE == null) {
                    INSTANCE = new LoggingTemplateFactory();
                }
            }
        }
        return INSTANCE;
    }

    public LoggingTemplate getTemplate(Method method) {
        Logging ann = method.getAnnotation(Logging.class);
        if (ann == null) {
            throw new LoggingException("@Logging not found at " + method);
        }
        return getTemplate(ann);
    }

    public LoggingTemplate getTemplate(Logging ann) {
        return getTemplate(ann, false);
    }

    public LoggingTemplate getTemplate(Logging ann, boolean focus) {
        LoggingTemplate result = focus ? null : cache.get(ann);
        if (result == null) {
            result = new LoggingTemplate(ann);
            cache.put(ann, result);
        }
        return result;
    }

}
