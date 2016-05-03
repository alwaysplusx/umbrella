package com.harmony.umbrella.ws;

import javax.ejb.Remote;

import com.harmony.umbrella.ws.proxy.Proxy;
import com.harmony.umbrella.ws.proxy.ProxyCallback;
import com.harmony.umbrella.ws.service.Message;

/**
 * @author wuxii@foxmail.com
 */
@Remote
public interface UserProxy extends Proxy<User>, ProxyCallback<User, Message> {

}
