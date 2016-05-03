package com.harmony.umbrella.example.ws;

import java.util.ArrayList;

import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import com.harmony.umbrella.ws.cxf.interceptor.MessageInInterceptor;
import com.harmony.umbrella.ws.cxf.interceptor.MessageOutInterceptor;
import com.harmony.umbrella.ws.jaxws.JaxWsProxyBuilder;
import com.harmony.umbrella.ws.ser.Message;

/**
 * @author wuxii@foxmail.com
 */
@Stateless(mappedName = "UserProxy")
@WebService(serviceName = "UserProxy")
public class UserProxyBean {

    @WebMethod
    public void sync(@WebParam(name = "address") String address) {
        UserService service = JaxWsProxyBuilder//
                .create()//
                .setAddress(address)//
                .addInInterceptor(new MessageInInterceptor())//
                .addOutInterceptor(new MessageOutInterceptor())//
                .build(UserService.class);

        Message result = service.saveOrUpdateUser(new ArrayList<User>());

        System.out.println(result);
    }

}
