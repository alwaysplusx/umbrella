package com.harmony.umbrella.context.ee;

import java.io.IOException;
import java.net.URL;

import com.harmony.umbrella.context.ApplicationContext;
import com.harmony.umbrella.context.ApplicationContextException;
import com.harmony.umbrella.context.ContextProvider;
import com.harmony.umbrella.util.PropertiesUtils;

/**
 * {@linkplain ContextProvider}EJB环境支持
 *
 * @author wuxii@foxmail.com
 */
public class EJBContextProvider extends ContextProvider {

    @Override
    public ApplicationContext createApplicationContext() {
        return createApplicationContext(null);
    }

    @Override
    public ApplicationContext createApplicationContext(URL url) {
        if (url == null) {
            return EJBApplicationContext.create();
        }
        try {
            return EJBApplicationContext.create(PropertiesUtils.loadProperties(url));
        } catch (IOException e) {
            throw new ApplicationContextException("cannot create application url not exists", e);
        }
    }

}
