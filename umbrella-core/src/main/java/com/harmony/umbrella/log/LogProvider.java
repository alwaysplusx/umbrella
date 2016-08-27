package com.harmony.umbrella.log;

/**
 * 实际日志框架provider
 * 
 * @author wuxii@foxmail.com
 */
public interface LogProvider {

    /**
     * 创建日志log
     * 
     * @param className
     *            对应的写日志类
     * @return log
     */
    Log getLogger(String className);

}
