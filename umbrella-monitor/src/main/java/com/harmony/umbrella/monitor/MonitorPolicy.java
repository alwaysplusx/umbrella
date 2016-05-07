package com.harmony.umbrella.monitor;

/**
 * 资源的监控策略, 该监控策略一般是对于{@code Resources}而言
 * <p>
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
 * 
 * @author wuxii@foxmail.com
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