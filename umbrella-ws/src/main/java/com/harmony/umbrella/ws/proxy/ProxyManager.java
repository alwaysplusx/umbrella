package com.harmony.umbrella.ws.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.harmony.umbrella.util.ReflectionUtils;
import com.harmony.umbrella.ws.MetadataLoader;
import com.harmony.umbrella.ws.annotation.Syncable;
import com.harmony.umbrella.ws.jaxws.JaxWsCXFExecutor;

/**
 * @author wuxii@foxmail.com
 */
public class ProxyManager {

    private static ProxyManager INSTANCE;

    private MetadataLoader loader;

    private JaxWsCXFExecutor executor = new JaxWsCXFExecutor();

    private ProxyManager() {
    }

    public static ProxyManager getInstance() {
        if (INSTANCE == null) {
            synchronized (ProxyManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ProxyManager();
                }
            }
        }
        return INSTANCE;
    }

    public <T> T create(Class<T> clientClass) {
        return null;
    }

    public class ClientProxy implements InvocationHandler {

        private Object target = new Object();

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (ReflectionUtils.isObjectMethod(method)) {
                return method.invoke(target, args);
            }
            Syncable ann = method.getAnnotation(Syncable.class);
            if (ann == null) {
            }
            throw new Exception();
        }
    }

}
