package com.harmony.umbrella.monitor;

/**
 * 监控器, 默认监控策略{@linkplain MonitorPolicy#WhiteList}
 * <p>
 * <ul>
 * <li>Patterns粗粒度的资源控制
 * <li>Resources细粒度的控制资源
 * <li>Policy起到中间的调节作用
 * </ul>
 * <p>
 * <p>
 * 在设定上， 通过向{@code Patterns}设置一个粗粒度的资源模版，如{@code /user/*}来监控所有路径以{@code /user} 开头的资源
 * <p>
 * 另外又可以通过{@code Resources}设置一个细粒度的资源连接，如{@code /user/getUser}来控制具体的资源。再由 {@code Policy}来控制{@code Patterns}和{@code Resources}之间的关系。
 * <p>
 * {@code Policy}的控制参见{@linkplain MonitorPolicy}
 *
 * @param <T>
 *            监控的资源类型
 * @author wuxii@foxmail.com
 * @see MonitorPolicy
 */
public interface MonitorFilter<T> {

    /**
     * 检测该资源是否收到监控
     *
     * @param resource
     *            待检测的资源
     * @return true需要监控
     */
    boolean isMonitored(T resource);

    /**
     * 监控的资源模版，资源模版包括了白名单与黑名单
     * 
     * @return
     */
    PatternManager getPatternManager();

    /**
     * 监控策略
     * 
     * @return
     */
    MonitorPolicy getMonitorPolicy();

    void setMonitorPolicy(MonitorPolicy policy);
}
