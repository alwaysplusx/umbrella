package com.harmony.umbrella.monitor;

/**
 * 监控结果视图分析处理工具，可用于处理分析结果信息，或将监控结果保存做二次处理等。
 * <p>
 * 监控的结果信息由{@linkplain Monitor}生成
 * 
 * @deprecated replace by {@linkplain GraphListener}
 * 
 * @param <T>
 *            被解析的监控视图类型
 * @author wuxii@foxmail.com
 */
@Deprecated
public interface GraphAnalyzer<T extends Graph> {

    /**
     * 分析结果
     * 
     * @param graph
     *            监控的结果
     */
    void analyze(T graph);

}
