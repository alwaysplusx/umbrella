package com.harmony.umbrella.context.ee;

import javax.ejb.Remote;

/**
 * @author wuxii@foxmail.com
 */
@Remote
public interface TestRemote {

    String sayHi(String name);

}
