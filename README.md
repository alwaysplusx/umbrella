### Harmony Umbrella

#### V0.0.2-SNAPSHOT

* umberlla-core
  * ApplicationContext JavaSE/JavaEE/Spring环境中的ApplicationContext支持

* umberlla-jaxws
  * 对于原有的JaxWsContextHandler替换为JaxWsPhaseVisitor. JaxWsExecutor不在持有外部的访问者,对于个周期的扩展更加容易.
  * PropertiesFileJaxWsContextReceiver有实际运行的项目提供配置文件. 下一步将实现更多可配置项的JaxWsContext的消费者

#### V0.0.1

* umbrella-core
  * bean创建
  * 资源扫描定位
  * 公用工具包


* umbrella-jaxws
  * 划分web service的执行周期。控制每个周期的内容
  * 基于[CXF](http://cxf.apache.org)实现web service的实际交互
  * 使用生产消费模式，控制交互时间对实际业务的影响


* umbrella-message
  * 抽象消费者，将消息的接收与实际的消费相分离
  * 将消息抽象为接口，配合各种类型的消费者使用


* umbrella-monitor
  * 使用拦截器对方法监控
  * 使用HttpFilter对Http请求监控  


* umbrella-secheduler
  * 基于[EJB TimerService](http://docs.oracle.com/javaee/6/tutorial/doc/bnboy.html)做一个简单的可扩展实现
