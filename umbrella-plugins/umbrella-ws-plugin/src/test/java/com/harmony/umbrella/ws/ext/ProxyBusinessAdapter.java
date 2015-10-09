/*
 * Copyright 2002-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.harmony.umbrella.ws.ext;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.xml.ws.WebServiceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harmony.umbrella.biz.AbstractBusiness;
import com.harmony.umbrella.data.domain.Model;
import com.harmony.umbrella.util.Assert;
import com.harmony.umbrella.util.StringUtils;
import com.harmony.umbrella.ws.Syncable;
import com.harmony.umbrella.ws.jaxws.JaxWsExecutorSupport;
import com.harmony.umbrella.ws.proxy.ProxySupport;
import com.harmony.umbrella.ws.support.SimpleContext;

/**
 * @author wuxii@foxmail.com
 */
public abstract class ProxyBusinessAdapter<T extends Model<ID>, ID extends Serializable> extends AbstractBusiness<T, ID> implements ProxyBusiness<T, ID> {

    private static final Logger log = LoggerFactory.getLogger(ProxySupport.class);

    /**
     * 执行上下文的发送者支持
     * 
     * @return 执行支撑
     */
    protected abstract JaxWsExecutorSupport getJaxWsExecutorSupport();

    /**
     * 将待同步对象封装为业务方法对于的请求参数数组，参数顺序需要与方法的要求相同
     * 
     * @param serviceMethod
     *            同步的业务方法
     * @param obj
     *            待同步对象
     * @return 同步方法参数数组
     */
    protected abstract Object[] packing(T obj, Map<String, Object> properties);

    @Override
    public boolean syncById(ID id) {
        return syncById(id, new HashMap<String, Object>());
    }

    @Override
    public boolean syncById(ID id, Map<String, Object> properties) {
        return sync(findOne(id), properties);
    }

    @Override
    public boolean syncInBatchById(Iterable<ID> ids) {
        return syncInBatchById(ids, new HashMap<String, Object>());
    }

    @Override
    public boolean syncInBatchById(Iterable<ID> ids, Map<String, Object> properties) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean sync(T obj) {
        return sync(obj, new HashMap<String, Object>());
    }

    @Override
    public boolean sync(T obj, Map<String, Object> properties) {
        // 对待同步的对象进行过滤，如果不需要同步或者已经同步则直接返回
        if (!filter(obj)) {
            return doSync(fetchReference(obj), properties);
        } else {
            log.info("skip sync, sync object [{}] are filtered out", obj);
        }
        return true;
    }

    @Override
    public void sync(Iterable<T> objs) {
        sync(objs, new HashMap<String, Object>());
    }

    @Override
    public void sync(Iterable<T> objs, Map<String, Object> properties) {
        for (T so : objs) {
            sync(so, properties);
        }
    }

    /**
     * @see com.harmony.dark.ws.Proxy#syncInBatch(java.lang.Iterable)
     * @see #syncInBatch(Iterable, Map)
     */
    @Override
    public boolean syncInBatch(Iterable<T> objs) {
        return syncInBatch(objs, new HashMap<String, Object>());
    }

    /**
     * 默认不开启批量同步
     * 
     * @throws UnsupportedOperationException
     * @see com.harmony.dark.ws.Proxy#syncInBatch(java.lang.Iterable)
     */
    @Override
    public boolean syncInBatch(Iterable<T> objs, Map<String, Object> properties) {
        throw new UnsupportedOperationException();
    }

    /**
     * 通过待同步对象的自身属性，判断同步对象是否需要同步，起到过滤已经同步过的对象以及不需要同步对象的作用
     * 
     * @param obj
     *            待同步对象
     * @return 返回true表示待同步对象被过滤不需要同步，false表示需要同步
     */
    protected boolean filter(T obj) {
        return false;
    }

    /**
     * 抓取待同步对象的关联对象
     * 
     * @param obj
     *            待同步的对象
     * @return 抓取后的对象
     */
    protected T fetchReference(T obj) {
        return obj;
    }

    /**
     * 将需要同步的对象通过上下文的发送者发送给接收者
     * 
     * @param obj
     *            待同步对象
     * @return true 发送成功(不代表同步成功)
     */
    protected boolean doSync(T obj, Map<String, Object> properties) {
        Class<?> serviceInterface = getServiceInterface();
        Assert.notNull(serviceInterface, "service interface is null, use @Syncable#endpoint or override getServiceInterface method");

        String serviceMethod = getServiceMethod();
        Assert.notBlank(serviceMethod, "service method is null or blank, use @Syncable#methodName or override getServiceMethod method");

        SimpleContext context = new SimpleContext(serviceInterface, serviceMethod);

        properties.put(ProxySupport.SERVICE_METHOD_NAME, serviceMethod);
        context.putAll(properties);

        this.configContext(context, properties);

        Object[] parameters = packing(obj, properties);
        log.debug("sync obj [{}] packing result [{}]", obj, parameters);
        context.setParameters(parameters == null ? new Object[0] : parameters);

        context.setAddress(getAddress());
        context.put(SYNC_OBJECT, obj);

        applySyncing(obj, properties);

        log.debug("sync obj {} -> context {}", obj, context);

        try {
            context.getMethod();
        } catch (NoSuchMethodException e) {
            throw new WebServiceException("method not find " + e.getMessage(), e);
        }

        return getJaxWsExecutorSupport().send(context);
    }

    /**
     * 将待同步的对象更新为同步中
     * 
     * @param obj
     *            待同步的对象
     * @param name
     *            同步的业务方法
     */
    protected void applySyncing(T obj, Map<String, Object> properties) {
    }

    /**
     * 配置上下文中的一些属性，{@linkplain SimpleContext#setReceiveTimeout(long)}等
     * 
     * @param context
     *            需要配置的执行上下文
     * @see com.harmony.umbrella.ws.Metadata Metadata
     */
    protected void configContext(SimpleContext context, Map<String, Object> properties) {
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

}
