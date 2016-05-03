package com.harmony.umbrella.examples.ee;

import javax.ejb.Singleton;

/**
 * @author wuxii@foxmail.com
 */
@Singleton(mappedName = "A")
public class A implements I {

    @Override
    public String sayHi() {
        return "A";
    }

}
