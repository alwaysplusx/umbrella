package com.harmony.umbrella.context.ee;

import javax.ejb.Stateless;

/**
 * @author wuxii@foxmail.com
 */
@Stateless(mappedName = "TestBean")
public class TestBean implements TestRemote, TestLocal {

    @Override
    public String sayHi(String name) {
        return "Hi " + name;
    }

}
