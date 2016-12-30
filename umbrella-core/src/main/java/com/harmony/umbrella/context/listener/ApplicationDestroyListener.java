package com.harmony.umbrella.context.listener;

import com.harmony.umbrella.context.ApplicationConfiguration;

/**
 * @author wuxii@foxmail.com
 */
public interface ApplicationDestroyListener extends ApplicationEventListener {

    void onDestroy(ApplicationConfiguration appConfig);

}
