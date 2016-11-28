package com.harmony.umbrella.web;

import com.harmony.umbrella.context.ApplicationConfiguration;

/**
 * @author wuxii@foxmail.com
 */
public interface ApplicationListener {

    void onStartup(ApplicationConfiguration appConfig);

    void onDestroy(ApplicationConfiguration appConfig);

}
