package com.harmony.umbrella.examples.ee;

import javax.ejb.Stateful;

/**
 * @author wuxii@foxmail.com
 */
@Stateful(mappedName = "B")
public class B implements I {

    @Override
    public String sayHi() {
        return "B";
    }

}
