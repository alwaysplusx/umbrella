package com.harmony.umbrella.monitor;

import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import com.harmony.umbrella.monitor.annotation.InternalProperty;
import com.harmony.umbrella.monitor.annotation.Monitor;
import com.harmony.umbrella.monitor.attack.ReflectionAttacker;

/**
 * @author wuxii@foxmail.com
 */
@Stateless(mappedName = "HiBean")
public class HiBean {

    private String name;

    @Interceptors({ LoggingInterceptor.class })
    @Monitor(
        internalProperties = { @InternalProperty(attacker = ReflectionAttacker.class, names = "name") //
    })
    public String sayHi(String name) {
        this.name = name;
        return "Hi " + name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
