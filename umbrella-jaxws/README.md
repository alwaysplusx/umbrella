### umbrella-jaxws

umbrella-jaxws是基于[CXF](http://cxf.apache.org)实现的，分装了CXF的jaxws部分功能

#### 简单示例

##### 发布一个web service服务

```java
// webservice 接口
@WebService(serviceName = "HelloService")
public interface HelloService {

    String sayHi(@WebParam(name = "name") String name);

}
// webservice 实现类
public class HelloServiceImpl implements HelloService {

    @Override
    public String sayHi(String name) {
        return "Hi " + name;
    }

}
```
以上两个类是作为服务的服务端。然后使用`umbrella-jaxws`来发布这个服务
```java
public static void main(String[] args) {
  	JaxWsServerManager.getInstance().publish(HelloServiceImpl.class, "http://localhost:8080/hi");
}
```
直接访问地址`http://localhost:8080/hi?wsdl`就能查看到发布的服务的wsdl文件


##### 作为客户端调用服务
```java
@Test
public void testAccessServer() {
    HelloService service = JaxWsProxyBuilder
                              .newProxyBuilder() // 新建一个代理建造工具
                              .build(HelloService.class, "http://localhost:8080/hi");// 新建代理服务并连接到服务地址http://localhost:8080/hi
    String result = service.sayHi("wuxii");// 调用服务方法
    assertEquals("Hi wuxii", result);
}
```

---

#### 附加功能

`umbrella-jaxws`将客户端执行交互，与真是的代码业务逻辑做剥离。形成了执行webservice的上下文`JaxWsContext`,在这上下文中包含了示例中的所有要素

* 服务接口
* 服务的业务方法
* 服务的安全策略要求
* 服务的地址等等

为了让交互与业务剥离，将使用生产消费模式。将调用者(也就是业务代码)将交互的请求作为执行的上下文，把自
身当作生产者，产生执行上下文。而后通过JMS或别的生成消费模式将消息传递给消费者，在消费者中解析上下文
并最终执行接口的交互。

为了满足交互中的记录以及信息分析的要求，引入`JaxWsContextHandler`支持对执行上下文的管控
