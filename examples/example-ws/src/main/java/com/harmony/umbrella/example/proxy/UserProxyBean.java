package com.harmony.umbrella.example.proxy;

import java.util.ArrayList;

import javax.ejb.Stateless;
import javax.jws.WebService;

import com.harmony.umbrella.example.ws.User;
import com.harmony.umbrella.example.ws.UserService;
import com.harmony.umbrella.ws.cxf.interceptor.MessageInInterceptor;
import com.harmony.umbrella.ws.cxf.interceptor.MessageOutInterceptor;
import com.harmony.umbrella.ws.jaxws.JaxWsProxyBuilder;

/**
 * @author wuxii@foxmail.com
 */
@WebService(serviceName = "UserProxy")
@Stateless(mappedName = "UserProxy")
public class UserProxyBean {

    public void test() {
        UserService service = JaxWsProxyBuilder//
                .create()//
                .setAddress("http://localhost:9001/user")//
                .addInInterceptor(new MessageInInterceptor())//
                .addOutInterceptor(new MessageOutInterceptor())//
                .build(UserService.class);

        /*System.setProperty("javax.xml.stream.XMLInputFactory", "com.harmony.umbrella.xml.UmbrellaXMLInputFactory");

        XMLInputFactory factory = XMLInputFactory.newFactory();

        System.out.println(factory);*/

        /*factory = XMLInputFactory.newFactory("com.harmony.umbrella.xml.UmbrellaXMLInputFactory", Thread.currentThread().getContextClassLoader());

        System.out.println(factory);*/

        service.saveOrUpdateUser(new ArrayList<User>());
    }

}
