package com.harmony.umbrella.example.ws;

import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import com.harmony.umbrella.ws.service.Message;

/**
 * @author wuxii@foxmail.com
 */
@WebService(targetNamespace = "http://www.harmony.com/srm/test/user")
public interface UserService {

    @WebResult(name = "result")
    Message saveOrUpdateUser(@WebParam(name = "users") List<User> users);

}
