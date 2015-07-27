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
>[与CXF相同](#生成客户端-1)

```java
@Test
public void test() {
    HelloService helloService = JaxWsProxyBuilder.create()//
                                                 .setAddress(ADDRESS)//
                                                 .build(HelloService.class);
    assertEquals("Hi wuxii", helloService.sayHi("wuxii"));
}
```
