package com.harmony.umbrella.web;

import com.harmony.umbrella.context.ApplicationConfiguration;

/**
 * @author wuxii@foxmail.com
 */
public interface ApplicationDestroyer {

    void onDestroy(ApplicationConfiguration appConfig);

}
