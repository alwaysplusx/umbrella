package com.harmony.umbrella.autoconfigure.log;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 
 * @author wuxii@foxmail.com
 */
@ConfigurationProperties(prefix = "harmony.log.interceptor")
public class LogInterceptorProperties {

    /**
     * 日志拦截的切点
     */
    String pointcut;
    /**
     * 只拦截含有{@linkplain Logging}注解的方法模式
     */
    boolean annotationMode;

    Class<?> serializer;

    Class<?> formatter;

    public String getPointcut() {
        return pointcut;
    }

    public void setPointcut(String pointcut) {
        this.pointcut = pointcut;
    }

    public boolean isAnnotationMode() {
        return annotationMode;
    }

    public void setAnnotationMode(boolean annotationMode) {
        this.annotationMode = annotationMode;
    }

    public Class<?> getSerializer() {
        return serializer;
    }

    public void setSerializer(Class<?> serializer) {
        this.serializer = serializer;
    }

    public Class<?> getFormatter() {
        return formatter;
    }

    public void setFormatter(Class<?> formatter) {
        this.formatter = formatter;
    }

}