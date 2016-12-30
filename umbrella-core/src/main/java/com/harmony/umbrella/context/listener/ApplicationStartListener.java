package com.harmony.umbrella.context.listener;

import com.harmony.umbrella.context.ApplicationConfiguration;

/**
 * @author wuxii@foxmail.com
 */
public interface ApplicationStartListener extends ApplicationEventListener {

    void onStartup(ApplicationConfiguration appConfig);

}
