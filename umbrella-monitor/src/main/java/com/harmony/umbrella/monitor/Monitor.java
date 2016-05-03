package com.harmony.umbrella.monitor;

import java.util.Set;

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
 * 在设定上， 通过向{@code Patterns}设置一个粗粒度的资源模版，如{@code /user/*}来监控所有路径以{@code /user}
 * 开头的资源
 * <p>
 * 另外又可以通过{@code Resources}设置一个细粒度的资源连接，如{@code /user/getUser}来控制具体的资源。再由
 * {@code Policy}来控制{@code Patterns}和{@code Resources}之间的关系。
 * <p>
 * {@code Policy}的控制参见{@linkplain MonitorPolicy}
 *
 * @param <T> 监控的资源类型
 * @author wuxii@foxmail.com
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
     * @param policy 监控策略
     * @see MonitorPolicy
     */
    void setPolicy(MonitorPolicy policy);

    Set<T> getResources(MonitorPolicy policy);

    Set<String> getPattern(MonitorPolicy policy);

    /**
     * 检测该资源是否收到监控
     *
     * @param resource 待检测的资源
     * @return true需要监控
     */
    boolean isMonitored(T resource);

    /**
     * 销毁资源
     */
    void destroy();

    /**
     * 资源的监控策略, 该监控策略一般是对于{@code Resources}而言
     * <p>
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
     *          <td>白名单策略，资源与resource或pattern匹配则不进入监控</td>
     *      </tr>
     *      <tr>
     *          <td>BlockList</td>
     *          <td>enabled</td>
     *          <td>enabled</td>
     *          <td>黑名单策略，资源与resource或pattern相匹配则进入监控</td>
     *      </tr>
     *    </tbody>
     *  </table>
     * </pre>
     */
    public enum MonitorPolicy {
        /**
         * 监控所有资源
         */
        All, //
        /**
         * 跳过所有监控
         */
        Skip,
        /**
         * 不符合条件的资源都进入监控
         */
        WhiteList,
        /**
         * 符合条件的资源都进入监控
         */
        BlackList
    }

}
