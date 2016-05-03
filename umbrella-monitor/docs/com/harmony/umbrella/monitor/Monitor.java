package com.harmony.umbrella.monitor;

import java.util.Collections;
import java.util.Set;

/**
 * 监控器, 默认监控策略{@linkplain MonitorPolicy#WhiteList}
 * <p>
 * <ul>
 * <li>Patterns粗粒度的资源控制
 * <li>Resources细粒度的控制资源
 * <li>Policy起到中间的调节作用
 * </ul>
 * 
 * 
 * 在设定上， 通过向{@code Patterns}设置一个粗粒度的资源模版，如{@code /user/*}来监控所有路径以{@code /user}
 * 开头的资源
 * <p>
 * 另外又可以通过{@code Resources}设置一个细粒度的资源连接，如{@code /user/getUser}来控制具体的资源。再由
 * {@code Policy}来控制{@code Patterns}和{@code Resources}之间的关系。
 * <p>
 * {@code Policy}的控制参见{@linkplain MonitorPolicy}
 * 
 * @author wuxii@foxmail.com
 * @param <T>
 *            监控的资源类型
 * @see MonitorPolicy
 */
public interface Monitor<T> {

    /**
     * 监控策略
     * <p>
     * default is {@linkplain MonitorPolicy#WhiteList}
     * 
     * @return 监控策略
     */
    MonitorPolicy getPolicy();

    /**
     * 更改监控策略, 不允许设置空
     * 
     * @param policy
     *            监控策略
     * @see MonitorPolicy
     */
    void setPolicy(MonitorPolicy policy);

    /**
     * 检测该资源是否收到监控
     * 
     * @param resource
     *            待检测的资源
     * @return true需要监控
     */
    boolean isMonitored(T resource);

    /**
     * 获取资源名单
     * <p>
     * 资源的性质还要通过{@link #getPolicy()}来进一步定性
     * <ul>
     * <li>
     * {@linkplain MonitorPolicy#WhiteList}表示为白名单资源
     * <li>
     * {@linkplain MonitorPolicy#BlockList}表示为黑名单资源
     * <li>
     * {@linkplain MonitorPolicy#All}与{@linkplain MonitorPolicy#Skip} 该资源名单无效
     * </ul>
     * 
     * @return {@linkplain Collections#unmodifiableSet(Set)}
     */
    Set<T> getResources();

    /**
     * 监控模版名单
     * 
     * @return 所有监控模版
     */
    Set<String> getPatterns();

    /**
     * 销毁资源
     */
    void destroy();

    /**
     * 资源的监控策略, 该监控策略一般是对于{@code Resources}而言
     * 
     * <pre>
     *  <table border="2" rules="all" cellpadding="4">
     *    <thead> 
     *      <tr><th align="center" colspan="5">监控策略说明</th></tr>
     *    </thead> 
     *    <tbody>
     *      <tr>
     *          <th>MonitorPolicy</th>
     *          <th>Patterns资源模版作用</th> 
     *          <th>Resources资源作用</th>
     *          <th>说明</th>
     *      </tr>
     *      <tr>
     *          <td>ALL</td>
     *          <td>disabled</td>
     *          <td>disabled</td>
     *          <td>经过监控器的资源都实施监控</td> 
     *      </tr>
     *      <tr>
     *          <td>Skip</td>
     *          <td>disabled</td>
     *          <td>disabled</td>
     *          <td>不对任何资源监控</td>
     *      </tr>
     *      <tr>
     *          <td>WhiteList</td>
     *          <td>enabled</td>
     *          <td>enabled</td>
     *          <td>监控模版下的所有资源。如果资源存在白名单中的则不监控(Resources为白名单)</td>
     *      </tr>
     *      <tr>
     *          <td>BlockList</td>
     *          <td>enabled</td>
     *          <td>enabled</td>
     *          <td>监控模版下的所有资源。此时黑名单中的资源都监控(Resources为黑名单)</td>
     *      </tr>
     *    </tbody>
     *  </table>
     * </pre>
     * 
     */
    public enum MonitorPolicy {
        /**
         * 监控所有资源
         * <p>
         * 无视监控模版{@linkplain Monitor#getPatterns()}与资源名单
         * {@linkplain Monitor#getResources()}
         */
        All, //
        /**
         * 跳过所有监控
         * <p>
         * 无视监控模版{@linkplain Monitor#getPatterns()}与资源名单
         * {@linkplain Monitor#getResources()}
         */
        Skip,
        /**
         * 监控模版{@linkplain Monitor#getPatterns()}内,
         * {@linkplain Monitor#getResources()}外的所有资源
         */
        WhiteList,
        /**
         * 监控模版{@linkplain Monitor#getPatterns()}内,
         * {@linkplain Monitor#getResources()}内的所有资源
         */
        BlockList
    }

}
