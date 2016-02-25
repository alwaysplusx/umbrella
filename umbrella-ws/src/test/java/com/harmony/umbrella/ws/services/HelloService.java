package com.harmony.umbrella.ws.services;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.xml.ws.WebFault;

@Path("/hi")
@Produces("*/*")
@WebFault(name = "HelloServiceFault")
@WebService(serviceName = "HelloService", targetNamespace = "http://www.harmony.com/test/hi")
public interface HelloService {

    @GET
    @Path("/{name}")
    @WebResult(name = "result")
    @WebMethod(operationName = "sayHi")
    String sayHi(@PathParam("name") @WebParam(name = "name") String name);

}
