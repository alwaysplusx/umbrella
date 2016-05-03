package com.harmony.umbrella.monitor.annotation;

/**
 * @author wuxii@foxmail.com
 */
public enum Mode {
    /**
     * 请求
     */
    IN,
    /**
     * 应答
     */
    OUT,
    /**
     * 请求+应答
     */
    INOUT;

    public boolean inRange(Mode mode) {
        return this == mode || INOUT == mode;
    }
}
