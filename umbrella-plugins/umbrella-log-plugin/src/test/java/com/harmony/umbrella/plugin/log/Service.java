package com.harmony.umbrella.plugin.log;

import com.harmony.umbrella.log.annotation.Logging;

/**
 * @author wuxii@foxmail.com
 */
public interface Service {

    @Logging(module = "测试模块", action = "测试action", message = "和{0}打招呼，{result}")
    String sayHi(String name);

}
