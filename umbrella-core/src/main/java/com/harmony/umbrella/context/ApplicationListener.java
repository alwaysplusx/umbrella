package com.harmony.umbrella.context;

/**
 * @author wuxii@foxmail.com
 */
public interface ApplicationListener {

    void onStartup(ApplicationConfiguration appConfig);

    void onDestroy(ApplicationConfiguration appConfig);

}
