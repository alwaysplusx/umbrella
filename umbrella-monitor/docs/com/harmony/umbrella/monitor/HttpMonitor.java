package com.harmony.umbrella.monitor;

/**
 * http请求监视
 * 
 * @author wuxii@foxmail.com
 * @see Monitor
 */
public interface HttpMonitor extends Monitor<String> {

    /**
     * 默认url分割符号
     */
    String DEFAULT_PATH_SEPARATOR = "/";
    /**
     * 默认连接监控的路径
     */
    String DEFAULT_PATH_PATTERN = DEFAULT_PATH_SEPARATOR + "*/**";
    
}
