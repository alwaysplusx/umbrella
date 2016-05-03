package com.harmony.umbrella.ws;

import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebService;

import com.harmony.umbrella.ws.service.Message;

/**
 * @author wuxii@foxmail.com
 */
@WebService(serviceName = "UserService", targetNamespace = "http://www.umbrella.com/user")
public interface UserService {

    Message accessUser(@WebParam(name = "type") String type, @WebParam(name = "user") User user);

    List<User> findUser(@WebParam(name = "name") String name);

}
