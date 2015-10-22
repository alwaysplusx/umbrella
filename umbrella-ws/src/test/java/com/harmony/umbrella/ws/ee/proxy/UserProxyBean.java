/*
 * Copyright 2002-2015 the original author or authors.
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
package com.harmony.umbrella.ws.ee.proxy;

import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harmony.umbrella.ws.Syncable;
import com.harmony.umbrella.ws.ee.User;
import com.harmony.umbrella.ws.ee.UserProxy;
import com.harmony.umbrella.ws.ee.UserService;
import com.harmony.umbrella.ws.jaxws.JaxWsExecutorSupport;
import com.harmony.umbrella.ws.proxy.ProxySupport;
import com.harmony.umbrella.ws.ser.Message;

/**
 * @author wuxii@foxmail.com
 */
@Stateless(mappedName = "UserProxyBean")
@Syncable(endpoint = UserService.class, methodName = "accessUser", address = "http://localhost:8080/user")
public class UserProxyBean extends ProxySupport<User> implements UserProxy {

    private static final Logger log = LoggerFactory.getLogger(UserProxyBean.class);

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
