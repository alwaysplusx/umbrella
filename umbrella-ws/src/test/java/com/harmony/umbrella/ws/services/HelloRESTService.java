package com.harmony.umbrella.ws.services;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

/**
 * @author wuxii@foxmail.com
 */
@Path("/hi")
@Produces("*/*")
public class HelloRESTService implements HelloService {

    @Override
    @GET
    @Path("/{name}")
    public String sayHi(@PathParam("name") String name) {
        return "Hi " + name;
    }

}
