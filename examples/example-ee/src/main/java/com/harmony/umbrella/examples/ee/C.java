package com.harmony.umbrella.examples.ee;

import javax.ejb.Stateless;

/**
 * @author wuxii@foxmail.com
 */
@Stateless(mappedName = "C")
public class C implements I {

    @Override
    public String sayHi() {
        return "C";
    }

}
