package com.harmony.umbrella.jaxws.services;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.ws.WebFault;

@WebFault(name = "HelloServiceFault")
@WebService(serviceName = "HelloService", targetNamespace = "http://www.harmony.com/test/hi")
public interface HelloService {

	@WebResult(name = "result")
	@WebMethod(operationName = "sayHi")
	String sayHi(@WebParam(name = "name") String name);

}
