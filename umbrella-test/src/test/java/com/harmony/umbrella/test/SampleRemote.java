package com.harmony.umbrella.test;

import javax.ejb.Remote;

/**
 * @author wuxii@foxmail.com
 */
@Remote
public interface SampleRemote {

    String sayHi(String name);

}
