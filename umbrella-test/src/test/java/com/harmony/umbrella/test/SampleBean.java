package com.harmony.umbrella.test;

import javax.ejb.Stateless;

/**
 * @author wuxii@foxmail.com
 */
@Stateless
public class SampleBean implements SampleRemote {

    @Override
    public String sayHi(String name) {
        return "Hi " + name;
    }

}
