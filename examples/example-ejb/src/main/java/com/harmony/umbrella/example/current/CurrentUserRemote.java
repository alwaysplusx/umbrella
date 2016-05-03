package com.harmony.umbrella.example.current;

import javax.ejb.Remote;

/**
 * @author wuxii@foxmail.com
 */
@Remote
public interface CurrentUserRemote {

    String getUser();

}
