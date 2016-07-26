package com.harmony.umbrella.context.ee;

import java.net.URL;

import com.harmony.umbrella.context.ApplicationContext;
import com.harmony.umbrella.context.ApplicationContextProvider;

/**
 * {@linkplain ApplicationContextProvider}EJB环境支持
 *
 * @author wuxii@foxmail.com
 */
public class EJBContextProvider implements ApplicationContextProvider {

    @Override
    public ApplicationContext createApplicationContext() {
        return createApplicationContext(null);
    }

    public ApplicationContext createApplicationContext(URL url) {
        return null;
    }

}
