package com.harmony.umbrella.example.ws;

import java.util.ArrayList;
import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.ws.handler.MessageContext;

import com.harmony.umbrella.ws.service.Message;
import com.harmony.umbrella.ws.service.MessageContent;
import com.harmony.umbrella.ws.service.ServiceSupport;

/**
 * @author wuxii@foxmail.com
 */

@WebService(targetNamespace = "http://www.harmony.com/srm/test/user")
public class UserServiceBean extends ServiceSupport implements UserService {

    private static List<User> users = new ArrayList<User>();

    @WebResult(name = "result")
    public Message saveOrUpdateUser(@WebParam(name = "users") List<User> users) {
        MessageContext context = this.webServiceContext.getMessageContext();
        Object object = context.get(MessageContext.WSDL_INTERFACE);
        System.out.println(object);
        MessageContent content = createContent();
        if (!isValid(users, content)) {
            return error(content);
        }
        UserServiceBean.users.addAll(users);
        return success(content);
    }

}
