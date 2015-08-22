# Monitor

## 主要控制点

1. Http请求监控
2. 方法监控(类监控)
3. 混合形式的监控

### 1. Http监控
要求：需要能对指定的连接做到监控，并能获取Http请求的各种参数以及请求结果(或HttpRequest中返回时候设置的对象)
> Q1: 没办法获取通过流写回页面的结果

详细监控信息：
* 监控URL
* HTTP信息
* 返回状态
* 执行时间
* 是否成功

### 2. 方法监控
要求：需要能对指定的方法做到监控，并能监控方法的请求参数以及返回值

详细监控信息：
* 监控对象
* 监控方法
* 请求参数
* 返回参数
* 执行时间
* 是否成功

### 3. 混合监控
混合监控既包括Http部分的监控又包括方法的监控(如：struts2中的拦截器)

要求：前两者只和

## 主要API
[`com.harmony.umbrella.monitor.Graph`](src/main/java/com/harmony/umbrella/monitor/Graph.java)

监控的结果视图

#### [`com.harmony.umbrella.monitor.Monitor`](src/main/java/com/harmony/umbrella/monitor/Monitor.java)

+ [`com.harmony.umbrella.monitor.AbstractMonitor`](src/main/java/com/harmony/umbrella/monitor/AbstractMonitor.java)

主要负责控制资源是否需要监控，以及控制[监控模式](src/main/java/com/harmony/umbrella/monitor/Monitor.java)

#### [`com.harmony.umbrella.monitor.Monitor.MonitorPolicy`](src/main/java/com/harmony/umbrella/monitor/Monitor.java)

监控策略说明：
<table border="2" rules="all" cellpadding="4">
<tbody>
<tr>
<th>MonitorPolicy</th>
<th>Patterns资源模版作用</th>
<th>Resources资源作用</th>
<th>说明</th>
</tr>
<tr>
<td>ALL</td>
<td>disabled</td>
<td>disabled</td>
<td>经过监控器的资源都实施监控</td>
</tr>
<tr>
<td>Skip</td>
<td>disabled</td>
<td>disabled</td>
<td>不对任何资源监控</td>
</tr>
<tr>
<td>WhiteList</td>
<td>enabled</td>
<td>enabled</td>
<td>监控模版下的所有资源。如果资源存在白名单中的则不监控(Resources为白名单)</td>
</tr>
<tr>
<td>BlockList</td>
<td>enabled</td>
<td>enabled</td>
<td>监控模版下的所有资源。此时黑名单中的资源都监控(Resources为黑名单)</td>
</tr>
</tbody>
</table>

#### [`com.harmony.umbrella.monitor.GraphAnalyzer`](src/main/java/com/harmony/umbrella/monitor/GraphAnalyzer.java)

分析最后的监控结果(可以记录监控记录到数据库等)

#### [`com.harmony.umbrella.monitor.Attacker`](src/main/java/com/harmony/umbrella/monitor/Attacker.java)

用于获取监控对象的内部信息
