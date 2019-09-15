package com.harmony.umbrella.autoconfigure.log;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author wuxii@foxmail.com
 */
@ConfigurationProperties(prefix = "harmony.log")
public class LogProperties {

    private String level;

    private Interceptor interceptor = new Interceptor();

    public Interceptor getInterceptor() {
        return interceptor;
    }

    public void setInterceptor(Interceptor interceptor) {
        this.interceptor = interceptor;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public static class Interceptor {

        // 日志拦截的切点
        String pointcut;
        
        // boolean annotationMode;

        // Class<?> serializer;

        // Class<?> formatter;
        public String getPointcut() {
            return pointcut;
        }

        public void setPointcut(String pointcut) {
            this.pointcut = pointcut;
        }

    }

}