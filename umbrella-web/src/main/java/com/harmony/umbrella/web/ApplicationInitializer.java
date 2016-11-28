package com.harmony.umbrella.web;

import com.harmony.umbrella.context.ApplicationConfiguration;

/**
 * @author wuxii@foxmail.com
 */
public interface ApplicationInitializer {

    void onStartup(ApplicationConfiguration appCfg);

}
