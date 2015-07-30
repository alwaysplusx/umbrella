# WebService 简要介绍

## Server Side

### WebService Interface `HelloService.java`

```java
@WebService(serviceName = "HelloService", targetNamespace = "http://www.harmony.com/hi")
public interface HelloService {

    @WebMethod
    @WebResult(name = "result")
    String sayHi(@WebParam(name = "name") String name);

}
```

### WebService Service Bean `HelloWebService.java`
```java
@WebService(serviceName = "HelloService", targetNamespace = "http://www.harmony.com/hi")
public class HelloWebService implements HelloService {

    @Override
    @WebResult(name = "result")
    public String sayHi(@WebParam(name = "namea") String name) {
        return "Hi " + name;
    }

}
```
### Publish Service
```java
public class ServerPublishTest {

    private static final String ADDRESS = "http://localhost:8080/hi";

    public static void main(String[] args) {
        String address = ADDRESS;
        if (args.length > 0) {
            address = args[0];
        }
        // JAXWS way, if other javax.xml.ws.spi.Provider in classpath will do provider way
        // for example: cxf -> org.apache.cxf.jaxws22.spi.ProviderImpl
        Endpoint.publish(address, new HelloWebService());

        /*
        // Publish WebService in CXF way
        JaxWsServerFactoryBean serverFactoryBean = new JaxWsServerFactoryBean();
        serverFactoryBean.setAddress(ADDRESS);
        serverFactoryBean.setServiceClass(HelloWebService.class);
        // or set service bean
        // serverFactoryBean.setServiceBean(new HelloWebService());
        serverFactoryBean.create();
        */
    }
}
```

## JAX-WS

### 生成客户端
```
E:\Temp>wsimport -encoding utf-8 -p com.harmony.test.ws -keep http://localhost:8080/hi?wsdl
正在解析 WSDL...

正在生成代码...

正在编译代码...

E:\Temp>
```
[生成的代码](docs/jaxws/com/harmony/test/ws)

```java
@Test
public void testJAXWS() throws Exception {
    Service service = Service.create(new URL("http://localhost:8080/hi?wsdl"), new QName("http://www.harmony.com/test/hi", "HelloService"));
    HelloService helloService = service.getPort(HelloService.class);
    assertEquals("Hi wuxii", helloService.sayHi("wuxii"));
}
```
> JAXWS对地址配置要求以?wsdl结尾

## Apache CXF

### 生成客户端
```
E:\Temp>wsdl2java -p com.harmony.test.ws -encoding utf-8 http://localhost:8080/hi?wsdl

E:\Temp>
```
[生成的代码](docs/cxf/com/harmony/test/ws)

```java
@Test
public void testCXF() {
    JaxWsProxyFactoryBean factoryBean = new JaxWsProxyFactoryBean();
    factoryBean.setAddress(ADDRESS);
    HelloService helloService = factoryBean.create(HelloService.class);
    assertEquals("Hi wuxii", helloService.sayHi("wuxii"));
}
```
## 简易使用法

### 生成客户端
> 与CXF相同

```java
@Test
public void test() {
    HelloService helloService = JaxWsProxyBuilder.create()//
                                                 .setAddress(ADDRESS)//
                                                 .build(HelloService.class);
    assertEquals("Hi wuxii", helloService.sayHi("wuxii"));
}
```

# 扩展功能

## 抽象调用过程

&nbsp;&nbsp;&nbsp;&nbsp;通过抽象调用过程，将调用所需要的数据作为参数(执行上下文)。来实现客户端通过组装执行上下文来实现交互，亦可通过JMS等方式将执行上下文发送到JMS容器来执行最终的调用。该部分扩展统一了调用的入口便于接口的扩展与调用的追溯。

### 直接调用

![直接调用](docs/images/图片1.png)

### 生产消费模式调用

![生成消费模式调用](docs/images/图片2.png)

## 使用扩展功能

### 核心API

#### [`com.harmony.umbrella.ws.Context`](src/main/java/com/harmony/umbrella/ws/Context.java)

执行时候的上下文，上下文中包括：
* 待执行的web service接口
* 待执行的方法
* 执行时候所需要用到的参数
* 调用地址
* 用户名密码
* 以及其他待扩展的属性

#### [`com.harmony.umbrella.ws.jaxws.JaxWsExecutor`](src/main/java/com/harmony/umbrella/ws/jaxws/JaxWsExecutor.java)

执行交互执行者：
* `Object execute(Context)`同步执行交互
* `Future<?> executeAsync(Context)`异步执行交互

#### `com.harmony.umbrella.ws.PhaseVisitor`

对JaxWsExecutor的各个执行周期的访问控制
* `visitBefore(Context)` 执行前被调用
* `visitAbort(WebServiceAbortException, Context)`执行取消被调用
* `visitCompletion(Object, Context)`执行完成被调用
* `visitThrowing(Throwable, Context)`执行有异常被调用

### 扩展功能

#### 客户端逻辑与调用的分离

[`com.harmony.umbrella.ws.Phase`](src/main/java/com/harmony/umbrella/ws/Phase.java)

[`com.harmony.umbrella.ws.Handler`](src/main/java/com/harmony/umbrella/ws/Handler.java)

[`com.harmony.umbrella.ws.Handler.HandleMethod`](src/main/java/com/harmony/umbrella/ws/Handler.java)

[`com.harmony.umbrella.ws.visitor.PhaseValidationVisitor`](src/main/java/com/harmony/umbrella/ws/visitor/PhaseValidationVisitor.java)

```java
public class JaxWsExecutorAndPhaseValTest {

    private static final String address = "http://localhost:8081/hello";
    private static final JaxWsExecutor executor = new JaxWsCXFExecutor();

    private static int count = 0;

    @BeforeClass
    public static void setUp() {
        // 发布HelloWebService服务在地址http://localhost:8081/hello上
        JaxWsServerBuilder.create().publish(HelloWebService.class, address);
    }

    @Test
    public void testHelloServicePhaseVal() {
        // 设置接口调用的上下文(服务接口, 服务方法名, 服务的参数)
        SimpleContext context = new SimpleContext(HelloService.class, "sayHi", new Object[]{"wuxii"});
        // 设置服务的所在地址
        context.setAddress(address);
        // 调用执行者执行方法， PhaseValidationVisitor用于帮助加载HelloServiceSayHiPhaseValidation实例进行接口的执行周期检验
        Object result = executor.execute(context, new PhaseValidationVisitor());
        // 对结果进行断言判断
        assertNotNull(result);
        assertEquals("Hi wuxii", result);
        assertEquals(2, count);
    }

    /**
     * 接口客户端的周期检验类
     * ps:该类拦截了{@linkplain HelloService#sayHi(String)}方法
     * 将调用分解为 前-(取消)-后-异常
     *
     * @author wuxii@foxmail.com
     */
    // @Handler注解表明这个类拦截接口HelloService的执行
    @Handler(HelloService.class)
    public static class HelloServiceSayHiPhaseValidation {

        /**
         * 在调用接口{@linkplain HelloService#sayHi(String)}前拦截调用
         *
         * @param message
         *         客户端情求时候的参数
         * @param content
         *         上下文中用户设置的内容
         * @return true表示交互可以继续执行， false交互将被终止
         */
        // Phase.PRE_INVOKE 表明执行前调用
        @HandleMethod(phase = Phase.PRE_INVOKE)
        public boolean sayHi(String message, Map<String, Object> content) {
            count++;
            return true;
        }

        /**
         * 在调用被取消时候调用
         *
         * @param exception
         *         取消异常
         * @param message
         *         客户端情求时候的参数
         * @param content
         *         上下文中用户设置的内容
         */
        @HandleMethod(phase = Phase.ABORT)
        public void sayHi(WebServiceAbortException exception, String message, Map<String, Object> content) {
        }

        /**
         * 在调用成功时候被调用
         *
         * @param result
         *         接口返回的结果
         * @param message
         *         请求的参数
         * @param content
         *         用户设置的上下文内容
         */
        @HandleMethod(phase = Phase.POST_INVOKE)
        public void sayHi(String result, String message, Map<String, Object> content) {
            count++;
        }

        /**
         * 在调用异常时候被调用
         *
         * @param e
         *         异常信息
         * @param message
         *         请求参数
         * @param content
         *         用户设置的上下文
         */
        @HandleMethod(phase = Phase.THROWING)
        public void sayHi(Throwable e, String message, Map<String, Object> content) {
        }

    }

}
```
执行的控制台输出
```
[INFO] 07/27 23:25:34,607 .buildServiceFromClass(ReflectionServiceFactoryBean.java:434) - Creating Service {http://www.harmony.com/test/hi}HelloService from class com.harmony.umbrella.ws.services.HelloService
[INFO] 07/27 23:25:34,998 .initDestination(ServerImpl.java:85) - Setting the server's publish address to be http://localhost:8081/hello
[INFO] 07/27 23:25:35,018 .doStart(Server.java:272) - jetty-8.1.15.v20140411
[INFO] 07/27 23:25:35,073 .doStart(AbstractConnector.java:338) - Started SelectChannelConnector@localhost:8081
[INFO] 07/27 23:25:35,180 .getAllHandlerClass(HandlerMethodFinder.java:99) - all @Handler classes [class com.harmony.umbrella.ws.jaxws.JaxWsExecutorAndPhaseValTest$HelloServiceSayHiPhaseValidation]
[INFO] 07/27 23:25:35,185 .buildServiceFromClass(ReflectionServiceFactoryBean.java:434) - Creating Service {http://www.harmony.com/test/hi}HelloService from class com.harmony.umbrella.ws.services.HelloService
[INFO] 07/27 23:25:35,199 .executeQuite(JaxWsCXFExecutor.java:63) - 使用代理[org.apache.cxf.jaxws.JaxWsClientProxy@61eaec38]执行交互{
  address  <-> http://localhost:8081/hello
  methodId <-> com.harmony.umbrella.ws.services.HelloService#sayHi(java.lang.String)
  username <-> null
  password <-> null
  ctimeout <-> -1
  rtimeout <-> -1
  stimeout <-> -1
}, invoker is [com.harmony.umbrella.ws.util.JaxWsInvoker@125290e5]
[INFO] 07/27 23:25:35,484 .executeQuite(JaxWsCXFExecutor.java:82) - 执行情况概要如下:{
  id:com.harmony.umbrella.ws.services.HelloService#sayHi(java.lang.String)
  requestTime:2015-07-27 23:25:35:200
  use:284
  arguments:{1:wuxii}
  result:Hi wuxii
  exception:false
}
```
#### 实用性扩展

[`com.harmony.umbrella.ws.jaxws.JaxWsCXFExecutor`](src/main/java/com/harmony/umbrella/ws/jaxws/JaxWsCXFExecutor.java)
执行者的CXF实现

[`com.harmony.umbrella.ws.Metadata`](src/main/java/com/harmony/umbrella/ws/Metadata.java)
用户名密码等元数据

[`com.harmony.umbrella.ws.MetadataLoader`](src/main/java/com/harmony/umbrella/ws/MetadataLoader.java)
元数据等加载扩展

[`com.harmony.umbrella.ws.jaxws.support.JaxWsContextReceiver`](src/main/java/com/harmony/umbrella/ws/jaxws/support/JaxWsContextReceiver.java)
执行上下文分离的接收扩展

[`com.harmony.umbrella.ws.jaxws.support.JaxWsContextSender`](src/main/java/com/harmony/umbrella/ws/jaxws/support/JaxWsContextSender.java)
执行上下文的分离发送扩展

#### 基于实际业务的扩展

[Go to](../../dark/tree/master/dark-ws/)
