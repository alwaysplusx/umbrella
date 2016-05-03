package com.harmony.umbrella.example.ws;

import javax.xml.ws.Endpoint;

/**
 * @author wuxii@foxmail.com
 */
public class UserServiceTest {

    public static void main(String[] args) {
        Endpoint.publish("http://localhost:8080", new UserServiceBean());
    }

}
