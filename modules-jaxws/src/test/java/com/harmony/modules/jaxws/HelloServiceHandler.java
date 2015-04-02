package com.harmony.modules.jaxws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harmony.modules.jaxws.Handler.HandleMethod;

@Handler(serviceClass = HelloService.class)
public class HelloServiceHandler {

    private static Logger log = LoggerFactory.getLogger(HelloServiceHandler.class);

    @HandleMethod(phase = Phase.PRE_INVOKE)
    public boolean sayHi(String name) {
        log.info("hello service handler pre invoker, {} say hello", name);
        return true;
    }

}
