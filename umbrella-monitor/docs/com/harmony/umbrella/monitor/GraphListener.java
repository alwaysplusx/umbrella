package com.harmony.umbrella.monitor;

/**
 * 监控视图监听
 * 
 * @author wuxii@foxmail.com
 */
public interface GraphListener<T extends Graph> {

    /**
     * 视图创建完成后被调用
     * 
     * @param graph
     *            完成的监控视图
     */
    void analyze(T graph);

}
