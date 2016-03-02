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
package com.harmony.umbrella.ws.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.harmony.umbrella.io.ResourceManager;
import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;
import com.harmony.umbrella.util.ClassUtils.ClassFilter;
import com.harmony.umbrella.ws.SyncCallback;
import com.harmony.umbrella.ws.Syncable;

/**
 * 通过扫描类路径下的类，过滤标注有{@linkplain Syncable}注解的类
 * 
 * @author wuxii@foxmail.com
 */
public class CallbackFinder {

    private static final Log log = Logs.getLog(CallbackFinder.class);

    private final String basePackage;

    /**
     * 所有标注了syncable注解的class
     */
    @SuppressWarnings("rawtypes")
    private Class<? extends SyncCallback>[] callbacks;

    /**
     * 各个类对应的{@linkplain SyncCallback}缓存
     */
    @SuppressWarnings("rawtypes")
    private final Map<String, List> callbackMap = new HashMap<String, List>();

    private ResourceManager resourceManager = ResourceManager.getInstance();

    public CallbackFinder(String basePackage) {
        this.basePackage = basePackage;
    }

    /**
     * 加载类路径下所有标注有{@linkplain Syncable} 的{@linkplain SyncCallback}
     * 且服务类是serviceClass，对于的同步方法为methodName的类
     * 
     * @param serviceClass
     *            匹配的服务类
     * @param methodName
     *            服务方法
     * @return 对应的SyncCallback
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Class<SyncCallback>[] getCallbackClasses(Class<?> serviceClass, String methodName) {
        String key = serviceClass.getName() + "#" + methodName;
        List classes = callbackMap.get(key);
        if (classes == null) {
            classes = new ArrayList<Class>();
            for (Class<? extends SyncCallback> clazz : callbacks()) {
                if (isMatchCallback(clazz, serviceClass, methodName)) {
                    classes.add(clazz);
                }
            }
            callbackMap.put(key, classes);
            log.debug("{}, all callback {}", key, classes);
        }
        return (Class<SyncCallback>[]) classes.toArray(new Class[classes.size()]);
    }

    @SuppressWarnings("rawtypes")
    protected Class<? extends SyncCallback>[] callbacks() {
        if (callbacks == null) {
            callbacks = getAllCallbackClass();
        }
        return callbacks;
    }

    public void clear() {
        this.callbacks = null;
        this.callbackMap.clear();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Class<? extends SyncCallback>[] getAllCallbackClass() {
        return (Class<? extends SyncCallback>[]) resourceManager.getClasses(basePackage, new ClassFilter() {

            @Override
            public boolean accept(Class<?> clazz) {
                if (clazz == null) {
                    return false;
                }
                if (SyncCallback.class.isAssignableFrom(clazz) && clazz.getAnnotation(Syncable.class) != null) {
                    log.info("accept {} as callback", clazz);
                    return true;
                }
                return false;
            }

        });
    }

    /*
     * 匹配比较，注解中的名称，服务接口类
     */
    protected boolean isMatchCallback(Class<?> clazz, Class<?> serviceClass, String methodName) {
        Syncable ann = clazz.getAnnotation(Syncable.class);
        return ann.endpoint().isAssignableFrom(serviceClass) && ann.methodName().equals(methodName);
    }

}
