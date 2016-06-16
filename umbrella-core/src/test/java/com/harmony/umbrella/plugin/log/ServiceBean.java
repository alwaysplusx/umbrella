package com.harmony.umbrella.plugin.log;

/**
 * @author wuxii@foxmail.com
 */
public class ServiceBean implements Service {

    @Override
    public String sayHi(String name) {
        return "Hi " + name;
    }

}
