package com.harmony.umbrella.ws.jaxws;

import static com.harmony.umbrella.ws.jaxws.JaxWsProxyBuilder.*;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.ws.WebServiceException;

import org.springframework.util.Assert;

import com.harmony.umbrella.core.Invoker;
import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;
import com.harmony.umbrella.util.Exceptions;
import com.harmony.umbrella.ws.Context;
import com.harmony.umbrella.ws.Metadata;
import com.harmony.umbrella.ws.ProxyExecutorSupport;
import com.harmony.umbrella.ws.util.JaxWsInvoker;

/**
 * JaxWs CXF执行方式实现
 *
 * @author wuxii@foxmail.com
 */
public class JaxWsCXFExecutor extends ProxyExecutorSupport implements JaxWsExecutor {

    private static final Log log = Logs.getLog(JaxWsCXFExecutor.class);

    /**
     * 代理对象缓存池
     */
    private Map<JaxWsContextKey, Object> proxyCache = new HashMap<JaxWsContextKey, Object>();

    /**
     * 配置是否接受缓存的代理对象
     */
    private boolean cacheable = true;

    private Invoker invoker = new JaxWsInvoker();

    @SuppressWarnings("unchecked")
    @Override
    public <T> T executeQuite(Context context, Class<T> resultType) {
        Assert.notNull(context.getServiceInterface(), "service interface not set");
        Assert.hasLength(context.getAddress(), "service address not set");
        Assert.hasLength(context.getMethodName(), "service method not set");
        T result = null;
        try {
            Method method = context.getMethod();
            Object proxy = getProxy(context);
            Object[] parameters = context.getParameters();
            log.info("使用代理[{}]执行交互{}, invoker is [{}]", proxy, context, invoker);
            result = (T) invoker.invoke(proxy, method, parameters);
        } catch (NoSuchMethodException e) {
            throw new WebServiceException("未找到接口方法" + context, e);
        } catch (Throwable e) {
            // 执行失败时候移除缓存的代理服务
            this.removeProxy(context.getServiceInterface());
            throw new WebServiceException("执行交互失败", Exceptions.getRootCause(e));
        }
        return result;
    }

    /**
     * 加载代理对象， 如果{@linkplain #cacheable}
     * 允许从缓存中加载泽加载缓存中的代理对象，如果缓存中不存在则新建一个代理对象，并将代理对象放置在缓存中
     *
     * @param context
     *            执行的上下文
     * @return 代理对象
     */
    protected Object getProxy(Metadata metadata) {
        Object proxy = isCacheable() ? getProxy0(metadata) : createProxy(metadata);
        return configurationProxy(proxy, metadata);
    }

    /**
     * 缓存中获取执行上下文对应的代理服务，缓存仅在这个方法体内控制
     *
     * @param context
     *            执行的上下文
     * @return 代理对象， 如果不存在缓存中不存在则创建
     */
    private Object getProxy0(Metadata metadata) {
        // 代理的超时设置是次要因素，不考虑在代理对象的key中
        JaxWsContextKey contextKey = new JaxWsContextKey(metadata);
        Object proxy = proxyCache.get(contextKey);
        if (proxy != null) {
            return proxy;
        }
        // 对应metadata不存在缓存(有可能是密码修改等原因)
        Class<?> serviceInterface = metadata.getServiceClass();
        synchronized (proxyCache) {
            // 迭代所有已经缓存的代理服务对象
            // 根据服务名检测是否已经存在对应的缓存， 如果存在则清除原来的缓存
            // 这种情况只在更换地址、用户名、密码的情况下发生
            // 确保一个服务名下只有一个代理服务实例
            removeProxy(serviceInterface);
            proxy = createProxy(metadata);
            proxyCache.put(contextKey, proxy);
        }

        return proxy;
    }

    /**
     * 创建当前{@linkplain Context}对应的服务代理, 创建只使用基础的信息(地址、用户名密码、接口类)，不会配置其他代理特性
     *
     * @param metadata
     *            执行上下文
     * @return 代理对象
     */
    private Object createProxy(Metadata metadata) {
        long start = System.currentTimeMillis();
        Object proxy = create()//
                .setAddress(metadata.getAddress())//
                .setUsername(metadata.getUsername())//
                .setPassword(metadata.getPassword())//
                .build(metadata.getServiceClass());
        log.debug("创建代理{}服务, 耗时{}ms", metadata, System.currentTimeMillis() - start);
        return proxy;
    }

    /**
     * 移除缓存的代理对象
     */
    public void removeProxy(Class<?> serviceInterface) {
        Assert.notNull(serviceInterface, "未指定待移除的代理对象");
        synchronized (proxyCache) {
            Iterator<JaxWsContextKey> it = proxyCache.keySet().iterator();
            while (it.hasNext()) {
                JaxWsContextKey key = it.next();
                if (serviceInterface.getName().equals(key.serviceName)) {
                    it.remove();
                }
            }
        }
    }

    /**
     * 清除已经缓存的服务代理
     */
    public void clearPool() {
        synchronized (proxyCache) {
            proxyCache.clear();
        }
        log.info("清除已经缓冲的代理对象");
    }

    /**
     * 根据上下文配置代理对象,超时的时间设置
     */
    private Object configurationProxy(Object proxy, Metadata metadata) {
        setConnectionTimeout(proxy, metadata.getConnectionTimeout());
        setReceiveTimeout(proxy, metadata.getReceiveTimeout());
        setSynchronousTimeout(proxy, metadata.getSynchronousTimeout());
        return proxy;
    }

    public boolean isCacheable() {
        return cacheable;
    }

    /**
     * 设置是否从缓存中获取服务
     *
     * @param cacheable
     */
    public void setCacheable(boolean cacheable) {
        this.cacheable = cacheable;
    }

    /**
     * 设置执行交互的{@linkplain Invoker}
     * <p/>
     * 默认是{@linkplain JaxWsInvoker}
     *
     * @param invoker
     */
    public void setInvoker(Invoker invoker) {
        this.invoker = invoker;
    }

    /**
     * 代理缓存的key, 唯一键只与服务名，服务地址， 用户密码有关
     */
    private static final class JaxWsContextKey {

        private final String serviceName;
        private final String address;
        private final String username;
        private final String password;

        public JaxWsContextKey(Metadata metadata) {
            this.serviceName = metadata.getServiceName();
            this.address = metadata.getAddress();
            this.username = metadata.getUsername();
            this.password = metadata.getPassword();
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((address == null) ? 0 : address.hashCode());
            result = prime * result + ((password == null) ? 0 : password.hashCode());
            result = prime * result + ((serviceName == null) ? 0 : serviceName.hashCode());
            result = prime * result + ((username == null) ? 0 : username.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            JaxWsContextKey other = (JaxWsContextKey) obj;
            if (serviceName == null) {
                if (other.serviceName != null)
                    return false;
            } else if (!serviceName.equals(other.serviceName))
                return false;
            if (address == null) {
                if (other.address != null)
                    return false;
            } else if (!address.equals(other.address))
                return false;
            if (password == null) {
                if (other.password != null)
                    return false;
            } else if (!password.equals(other.password))
                return false;
            if (username == null) {
                if (other.username != null)
                    return false;
            } else if (!username.equals(other.username))
                return false;
            return true;
        }

    }

}
