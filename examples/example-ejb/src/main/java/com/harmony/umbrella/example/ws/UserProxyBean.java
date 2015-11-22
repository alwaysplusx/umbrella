/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
