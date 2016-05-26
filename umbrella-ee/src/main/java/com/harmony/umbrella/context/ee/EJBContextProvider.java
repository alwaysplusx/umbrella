package com.harmony.umbrella.context.ee;

import java.net.URL;

import com.harmony.umbrella.context.ApplicationContext;
import com.harmony.umbrella.context.ContextProvider;

/**
 * {@linkplain ContextProvider}EJB环境支持
 *
 * @author wuxii@foxmail.com
 */
public class EJBContextProvider implements ContextProvider {

    @Override
    public ApplicationContext createApplicationContext() {
        return createApplicationContext(null);
    }

    public ApplicationContext createApplicationContext(URL url) {
        return null;
    }

}
