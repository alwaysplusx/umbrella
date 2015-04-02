package com.harmony.modules.jaxws;

import javax.xml.ws.Endpoint;

public class HelloServiceImpl implements HelloService {

    public static void main(String[] args) {
        Endpoint.publish("http://localhost:8080/hi", new HelloServiceImpl());
    }

    @Override
    public String sayHi(String name) {
        return "Hi " + name;
    }

}
