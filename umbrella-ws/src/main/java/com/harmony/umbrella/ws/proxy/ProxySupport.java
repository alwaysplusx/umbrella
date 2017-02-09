package com.harmony.umbrella.ws.proxy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.ws.WebServiceException;

import org.springframework.util.Assert;

import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;
import com.harmony.umbrella.util.StringUtils;
import com.harmony.umbrella.ws.Context;
import com.harmony.umbrella.ws.annotation.Syncable;
import com.harmony.umbrella.ws.jaxws.JaxWsExecutorSupport;
import com.harmony.umbrella.ws.support.SimpleContext;

/**
 * 客户端代理的抽象类
 * <p/>
 * 配合使用{@linkplain Syncable}来实现在代码编写阶段的配置， 配置同步业务的接口以及业务方法
 * <p/>
 * 如果不希望使用{@linkplain Syncable}则可以通过覆盖方法{@linkplain #getServiceInterface()}
 * ...等，来实现子类的具体服务接口以及方法名的指定
 *
 * @author wuxii@foxmail.com
 * @see Syncable
 */
public abstract class ProxySupport<T> implements Proxy<T> {

    private static final Log log = Logs.getLog(ProxySupport.class);

    /**
     * 执行上下文的发送者支持
     *
     * @return 执行支撑
     */
    protected abstract JaxWsExecutorSupport getJaxWsExecutorSupport();

    /**
     * 将待同步对象封装为业务方法对于的请求参数数组，参数顺序需要与方法的要求相同
     *
     * @param object
     *            待同步对象
     * @param properties
     *            上下文中的属性
     * @return 同步方法参数数组
     */
    protected abstract Object[] packing(T object, Map<String, Object> properties);

    /**
     * 批量同步的封装
     *
     * @param objects
     *            待同步的对象
     * @param properties
     *            上下文中的属性
     * @return
     */
    protected Object[] packing(List<T> objects, Map<String, Object> properties) {
        // override in sub class
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean sync(T object) {
        return sync(object, new HashMap<String, Object>());
    }

    @Override
    public boolean sync(T object, Map<String, Object> properties) {
        // 对待同步的对象进行过滤，如果不需要同步或者已经同步则直接返回
        if (!filter(object)) {
            Packer packer = new Packer(fetchReference(object), properties);
            return doSync(packer);
        } else {
            log.info("skip sync, sync object [{}] are filtered out", object);
        }
        return true;
    }

    @Override
    public void sync(List<T> objects) {
        sync(objects, new HashMap<String, Object>());
    }

    @Override
    public void sync(List<T> objects, Map<String, Object> properties) {
        for (T so : objects) {
            sync(so, properties);
        }
    }

    /**
     * @see com.huiju.module.ws.proxy.Proxy#syncInBatch(List)
     * @see #syncInBatch(List, Map)
     */
    @Override
    public boolean syncInBatch(List<T> objects) {
        return syncInBatch(objects, new HashMap<String, Object>());
    }

    /**
     * 默认不开启批量同步
     *
     * @see com.huiju.module.ws.proxy.Proxy#syncInBatch(List)
     */
    @Override
    public boolean syncInBatch(List<T> objects, Map<String, Object> properties) {
        List<T> list = new ArrayList<T>();
        for (T object : objects) {
            if (!filter(object)) {
                list.add(fetchReference(object));
            } else {
                log.info("skip sync, sync object [{}] are filtered out", object);
            }
        }
        if (!list.isEmpty()) {
            return doSync(new Packer(list, properties));
        }
        return true;
    }

    /**
     * 将需要同步的对象通过上下文的发送者发送给接收者
     *
     * @param packer
     *            封装工具类
     * @return true 发送成功(不代表同步成功)
     */
    protected boolean doSync(Packer packer) {
        // 通过配置信息与配置方法生成默认的上下文，上下文中包括配置的服务接口，方法，服务地址
        SimpleContext context = createDefaultContext();
        // 先设置context的其他属性, 超时时间，用户名密码等
        this.configContext(context);

        // 将entity打包问接口说需要的vo
        Object[] parameters = packer.packing();
        context.setParameters(parameters == null ? new Object[0] : parameters);
        log.debug("sync object [{}] packing result [{}]", packer.object, parameters);
        try {
            // 设置完必须元素后检测是否能找到接口类的对应方法， 不能找到直接异常退出
            // 必须的元素 = 服务类 + 服务方法名 + 服务参数
            context.getMethod();
        } catch (NoSuchMethodException e) {
            throw new WebServiceException("method not find " + e.getMessage(), e);
        }
        context.put(SYNC_OBJECT, packer.object);
        // 要同步前调用applySyncing
        packer.applySyncing();
        // 将传入的属性设置到上下文中
        applyProperty(context, packer.properties);
        // 将上下文发送给jms
        return getJaxWsExecutorSupport().send(context);
    }

    private void applyProperty(Context context, Map<String, Object> properties) {
        for (Entry<String, Object> entry : properties.entrySet()) {
            context.put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * 通过配置信息与配置方法生成默认的上下文，上下文中包括配置的服务接口，方法，服务地址
     * 
     * @return
     */
    protected final SimpleContext createDefaultContext() {
        Class<?> serviceInterface = getServiceInterface();
        Assert.notNull(serviceInterface, "service interface is null, use @Syncable#endpoint or override getServiceInterface method");
        String serviceMethod = getServiceMethod();
        Assert.hasLength(serviceMethod, "service method is null or blank, use @Syncable#methodName or override getServiceMethod method");
        return new SimpleContext(serviceInterface, serviceMethod, getAddress());
    }

    /**
     * 通过待同步对象的自身属性，判断同步对象是否需要同步，起到过滤已经同步过的对象以及不需要同步对象的作用
     *
     * @param object
     *            待同步对象
     * @return 返回true表示待同步对象被过滤不需要同步，false表示需要同步
     */
    protected boolean filter(T object) {
        return false;
    }

    /**
     * 抓取待同步对象的关联对象
     *
     * @param object
     *            待同步的对象
     * @return 抓取后的对象
     */
    protected T fetchReference(T object) {
        return object;
    }

    /**
     * 将待同步的对象更新为同步中
     *
     * @param object
     *            待同步的对象
     * @param properties
     *            同步的业务方法
     */
    protected void applySyncing(T object, Map<String, Object> properties) {
        // apply syncing in sub class
    }

    /**
     * 将待同步的对象更新为同步中
     *
     * @param objects
     *            待同步的对象
     * @param properties
     *            同步的业务方法
     */
    protected void applySyncing(List<T> objects, Map<String, Object> properties) {
        for (T object : objects) {
            applySyncing(object, properties);
        }
    }

    /**
     * 配置上下文中的一些属性，{@linkplain SimpleContext#setReceiveTimeout(long)}等
     *
     * @param context
     *            需要配置的执行上下文
     * @see com.harmony.umbrella.ws.Metadata Metadata
     */
    protected void configContext(SimpleContext context) {
    }

    /**
     * 获取业务bean上的{@linkplain Syncable}注解
     *
     * @return syncable注解
     */
    protected final Syncable getSyncable() {
        return getClass().getAnnotation(Syncable.class);
    }

    /**
     * 同步的接口类
     *
     * @see Syncable#endpoint()
     */
    protected Class<?> getServiceInterface() {
        Syncable syncable = getSyncable();
        if (syncable != null) {
            return syncable.endpoint();
        }
        return null;
    }

    /**
     * 同步的方法
     *
     * @see Syncable#methodName()
     */
    protected String getServiceMethod() {
        Syncable syncable = getSyncable();
        if (syncable != null) {
            return syncable.methodName();
        }
        return null;
    }

    /**
     * 同步的地址(非必填, 可以通过接收者去自动从数据库加载)
     *
     * @see Syncable#address()
     */
    protected String getAddress() {
        Syncable syncable = getSyncable();
        if (syncable != null && StringUtils.isNotBlank(syncable.address())) {
            return syncable.address();
        }
        return null;
    }

    protected final class Packer {

        private final Object object;
        private final Map<String, Object> properties = new HashMap<String, Object>();

        protected Packer(Object object, Map<String, Object> properties) {
            this.object = object;
            this.properties.putAll(properties);
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        public Object[] packing() {
            if (object instanceof List) {
                return ProxySupport.this.packing((List) object, properties);
            } else {
                return ProxySupport.this.packing((T) object, properties);
            }
        }

        @SuppressWarnings("unchecked")
        public void applySyncing() {
            if (object instanceof List) {
                ProxySupport.this.applySyncing((List<T>) object, properties);
            } else {
                ProxySupport.this.applySyncing((T) object, properties);
            }
        }
    }

}