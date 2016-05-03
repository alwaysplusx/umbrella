package com.harmony.umbrella.ws.proxy;

import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;
import com.harmony.umbrella.ws.User;
import com.harmony.umbrella.ws.UserProxy;
import com.harmony.umbrella.ws.UserService;
import com.harmony.umbrella.ws.annotation.Syncable;
import com.harmony.umbrella.ws.jaxws.JaxWsExecutorSupport;
import com.harmony.umbrella.ws.service.Message;

/**
 * @author wuxii@foxmail.com
 */
@Stateless(mappedName = "UserProxyBean")
@Syncable(endpoint = UserService.class, methodName = "accessUser", address = "http://localhost:8080/user")
public class UserProxyBean extends ProxySupport<User> implements UserProxy {

    private static final Log log = Logs.getLog(UserProxyBean.class);

    @EJB
    private JaxWsExecutorSupport executorSupport;

    @Override
    protected JaxWsExecutorSupport getJaxWsExecutorSupport() {
        return executorSupport;
    }

    @Override
    public void forward(User obj, Map<String, Object> content) {
        log.info(">>>> forward, obj {}, content {}", obj, content);
    }

    @Override
    public void success(User obj, Message result, Map<String, Object> content) {
        log.info(">>>> success, obj {}, result {}, content {}", obj, result, content);
    }

    @Override
    public void failed(User obj, Throwable throwable, Map<String, Object> content) {
        log.info(">>>> failed, obj {}, reason {}, content", obj, throwable, content);
    }

    @Override
    protected Object[] packing(User obj, Map<String, Object> properties) {
        return new Object[] { "S", obj };
    }

}
