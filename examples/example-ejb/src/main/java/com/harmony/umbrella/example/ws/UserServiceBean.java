package com.harmony.umbrella.example.ws;

import java.util.List;

import javax.ejb.Stateless;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import com.harmony.umbrella.ws.ser.Message;
import com.harmony.umbrella.ws.ser.MessageContent;
import com.harmony.umbrella.ws.ser.ServerSupport;

/**
 * @author wuxii@foxmail.com
 */
@Stateless(mappedName = "UserServiceBean")
@WebService(targetNamespace = "http://www.harmony.com/srm/test/user")
public class UserServiceBean extends ServerSupport implements UserService {

    @WebResult(name = "result")
    public Message saveOrUpdateUser(@WebParam(name = "users") List<User> users) {
        MessageContent content = createContent();
        for (User user : users) {
            content.put(getKey(user), "操作成功");
        }
        return success(content);
    }

}
