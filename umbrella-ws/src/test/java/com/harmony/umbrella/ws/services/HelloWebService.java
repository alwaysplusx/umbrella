package com.harmony.umbrella.ws.services;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.ws.WebFault;

@WebFault(name = "HelloServiceFault")
@WebService(serviceName = "HelloService", targetNamespace = "http://www.harmony.com/test/hi")
public class HelloWebService implements HelloService {

    @Override
    @WebResult(name = "result")
    @WebMethod(operationName = "sayHi")
    public String sayHi(@WebParam(name = "name") String name) {
        return "Hi " + name;
    }
}
