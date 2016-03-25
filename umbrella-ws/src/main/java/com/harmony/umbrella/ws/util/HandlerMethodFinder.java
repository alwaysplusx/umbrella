/*
 * Copyright 2013-2015 wuxii@foxmail.com.
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

import static com.harmony.umbrella.util.StringUtils.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.harmony.umbrella.UmbrellaProperties;
import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;
import com.harmony.umbrella.io.ResourceManager;
import com.harmony.umbrella.util.ClassUtils;
import com.harmony.umbrella.util.ClassUtils.ClassFilter;
import com.harmony.umbrella.util.ClassUtils.ClassFilterFeature;
import com.harmony.umbrella.ws.annotation.Handler;
import com.harmony.umbrella.ws.annotation.Handler.HandleMethod;
import com.harmony.umbrella.ws.Phase;
import com.harmony.umbrella.ws.WebServiceAbortException;

/**
 * @author wuxii@foxmail.com
 */
public class HandlerMethodFinder {

    private static final Log log = Logs.getLog(HandlerMethodFinder.class);

    private final Class<?>[] handlerClasses;

    /**
     * handler的缓存池
     */
    @SuppressWarnings("rawtypes")
    private Map<String, Collection> handlerCache = new HashMap<String, Collection>();

    /**
     * 扫描的包
     */
    private final String basePackage;

    public HandlerMethodFinder(String basePackage) {
        this.basePackage = basePackage;
        this.handlerClasses = this.getAllHandlerClass(basePackage);
    }

    /**
     * basePackage为当前classpath
     */
    public HandlerMethodFinder() {
        this(UmbrellaProperties.DEFAULT_PACKAGE);
    }

    /**
     * {@linkplain #basePackage}下符合serviceMethod周期为Phase的处理方法
     * 
     * @param serviceMethod
     *            当前执行的方法
     * @param phase
     *            执行周期
     * @return 拦截serviceMethod的处理方法
     */
    public HandleMethodInvoker[] findHandleMethods(Method serviceMethod, Phase phase) {

        List<HandleMethodInvoker> result = new LinkedList<HandleMethodInvoker>();

        for (Method handleMethod : getHandleMethods(serviceMethod, phase)) {

            HandleMethodReflectInvoker hmi = new HandleMethodReflectInvoker(handleMethod.getDeclaringClass(), handleMethod, phase);

            Class<?>[] parameterTypes = handleMethod.getParameterTypes();
            hmi.setEndWithMap(Map.class == parameterTypes[parameterTypes.length - 1]);

            result.add(hmi);

        }

        return result.toArray(new HandleMethodInvoker[result.size()]);
    }

    /**
     * 判断Handler中handles或value中是否有serviceClass
     */
    public boolean isMatchHandler(Class<?> serviceClass, Handler handler) {
        Class<?>[] classes = handler.value();
        for (Class<?> clazz : classes) {
            if (serviceClass.equals(clazz)) {
                return true;
            }
        }
        String[] handlers = handler.handles();
        for (String h : handlers) {
            if (serviceClass.getName().equals(h)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 主要检查handleMethod对应的方法参数是否符合serviceMethod的方法参数要求.
     * <p>
     * handlerMethod可以在最后添加一个Map参数(非必要)
     * 
     * @param serviceMethod
     *            服务方法
     * @param handleMethod
     *            拦截处理的方法
     * @param phase
     *            周期
     * @return true is match, false is not match
     * @see HandleMethod
     */
    public boolean isMatchHandleMethod(Method serviceMethod, Method handleMethod, Phase phase) {
        // 接口的方法参数签名
        final Class<?>[] serviceParamTypes = serviceMethod.getParameterTypes();
        // handler的方法参数签名
        final Class<?>[] handleParamTypes = handleMethod.getParameterTypes();

        // 根据服务的参数签名构建的匹配参数前面
        List<Class<?>> assignTypes = new LinkedList<Class<?>>();
        Collections.addAll(assignTypes, serviceParamTypes);

        if (log.isDebugEnabled()) {
            String serviceId = getMethodId(serviceMethod);
            String serviceSignature = typeString(serviceParamTypes);
            String handlerId = getMethodId(handleMethod);
            String handleSignature = typeString(handleParamTypes);
            log.debug("\ntest handle method is match in phase of {}? \n\t" //
                    + "1.service id          {} \n\t"//
                    + "2.service signature   {}\n\t"//
                    + "3.handler id          {}\n\t"//
                    + "4.handler signature   {}", //
                    phase, serviceId, serviceSignature, handlerId, handleSignature);
        }
        switch (phase) {
        case PRE_INVOKE:
            if (!isAssignable(serviceParamTypes, handleParamTypes)) {
                assignTypes.add(Map.class);
                return isAssignable(assignTypes, handleParamTypes);
            }
            return true;
        case ABORT:
            assignTypes.add(0, WebServiceAbortException.class);
            if (!isAssignable(assignTypes, handleParamTypes)) {
                assignTypes.add(Map.class);
                return isAssignable(assignTypes, handleParamTypes);
            }
            return true;
        case POST_INVOKE:
            assignTypes.add(0, getReturnType(serviceMethod));
            if (!isAssignable(assignTypes, handleParamTypes)) {
                assignTypes.add(Map.class);
                return isAssignable(assignTypes, handleParamTypes);
            }
            return true;
        case THROWING:
            assignTypes.add(0, Exception.class);
            if (!isAssignable(assignTypes, handleParamTypes)) {
                assignTypes.add(Map.class);
                return isAssignable(assignTypes, handleParamTypes);
            }
            return true;
        case FINALLY:
            assignTypes.add(0, Exception.class);
            assignTypes.add(1, getReturnType(serviceMethod));
            if (!isAssignable(assignTypes, handleParamTypes)) {
                assignTypes.add(Map.class);
                return isAssignable(assignTypes, handleParamTypes);
            }
            return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private Method[] getHandleMethods(Method serviceMethod, Phase phase) {
        String key = cacheKey(serviceMethod, phase);
        Collection<Method> result = handlerCache.get(key);
        if (result == null) {
            synchronized (handlerCache) {

                result = handlerCache.get(key);
                if (result == null) {

                    result = new HashSet<Method>();
                    Class<?> serviceClass = serviceMethod.getDeclaringClass();
                    String serviceMethodName = serviceMethod.getName();

                    for (Class<?> handlerClass : getHandlerClass(serviceClass)) {

                        for (Method handleMethod : handlerClass.getMethods()) {
                            HandleMethod ann = handleMethod.getAnnotation(HandleMethod.class);
                            // 忽略object的方法 以及 没有标注HandleMethod注解的方法
                            if (ann == null || handleMethod.getDeclaringClass().equals(Object.class)) {
                                continue;
                            }
                            // 周期相同、方法名与annotation相同或与handleMethod相同
                            if (phase.equals(ann.phase()) //
                                    && (ann.methodName().equals(serviceMethodName) || handleMethod.getName().equals(serviceMethodName))//
                                    && isMatchHandleMethod(serviceMethod, handleMethod, phase)) {
                                result.add(handleMethod);
                            }
                        }

                    }
                    // 排序
                    if (result.size() > 1) {
                        result = sortHandleMethod(result);
                    }

                    if (log.isDebugEnabled()) {
                        log.debug("{}.{} handler method -> {}", getMethodId(serviceMethod), phase, result);
                    }

                    handlerCache.put(key, result);
                }
            }
        }
        return result.toArray(new Method[result.size()]);
    }

    /**
     * basePackage下所有{@linkplain Handler}的{@linkplain Handler#value()}或者
     * {@linkplain Handler#handles()}值包含serviceClass的类
     */
    @SuppressWarnings({ "unchecked" })
    private Class<?>[] getHandlerClass(Class<?> serviceClass) {
        String cacheKey = cacheKey(serviceClass, null);
        Collection<Class<?>> classes = handlerCache.get(cacheKey);
        if (classes == null) {
            synchronized (handlerCache) {
                classes = handlerCache.get(cacheKey);

                if (classes == null) {
                    classes = new HashSet<Class<?>>();

                    for (Class<?> clazz : handlerClasses) {
                        Handler handler = clazz.getAnnotation(Handler.class);
                        if (isMatchHandler(serviceClass, handler)) {
                            classes.add(clazz);
                        }
                    }

                    if (classes.size() > 1) {
                        classes = sortHandler(classes);
                    }
                    log.debug("{} @Handler classes -> {}", cacheKey, classes);

                    handlerCache.put(cacheKey, classes);
                }

            }
        }
        return classes.toArray(new Class[classes.size()]);
    }

    private Collection<Method> sortHandleMethod(Collection<Method> handleMethods) {
        List<Method> result = new ArrayList<Method>(handleMethods);
        Collections.sort(result, new Comparator<Method>() {
            @Override
            public int compare(Method o1, Method o2) {
                Handler h1 = o1.getDeclaringClass().getAnnotation(Handler.class);
                Handler h2 = o2.getDeclaringClass().getAnnotation(Handler.class);
                return Integer.valueOf(h1.ordinal()).compareTo(Integer.valueOf(h2.ordinal()));
            }
        });
        return result;
    }

    private Collection<Class<?>> sortHandler(Collection<Class<?>> classes) {
        List<Class<?>> result = new ArrayList<Class<?>>(classes);
        Collections.sort(result, new Comparator<Class<?>>() {
            @Override
            public int compare(Class<?> o1, Class<?> o2) {
                Handler a1 = o1.getAnnotation(Handler.class);
                Handler a2 = o2.getAnnotation(Handler.class);
                return Integer.valueOf(a1.ordinal()).compareTo(Integer.valueOf(a2.ordinal()));
            }
        });
        return result;
    }

    private String typeString(Class<?>... classes) {
        StringBuilder sb = new StringBuilder();
        for (Class<?> clazz : classes) {
            sb.append(", ").append(clazz.getName());
        }
        if (sb.length() > 0) {
            sb.delete(0, 2);
        }
        return sb.toString();
    }

    /**
     * basePackage下的所有带有annotation的{@linkplain Handler}, 并{@linkplain Handler}中
     * {@linkplain Handler#value()} {@linkplain Handler#handles()}不全为空的class
     */
    private Class<?>[] getAllHandlerClass(String packageName) {
        long start = System.currentTimeMillis();
        Class<?>[] classes = ResourceManager.getInstance().getClasses(packageName, new ClassFilter() {
            @Override
            public boolean accept(Class<?> clazz) {
                try {
                    if (!ClassFilterFeature.NEWABLE.accept(clazz)) {
                        return false;
                    }
                    Handler handler = clazz.getAnnotation(Handler.class);
                    if (handler != null && (handler.value().length > 0 || handler.handles().length > 0)) {
                        log.info("accept {} as handler", clazz);
                        return true;
                    }
                } catch (Throwable e) {
                    log.warn("scan package was throw exception ", e);
                }
                return false;
            }
        });
        long use = System.currentTimeMillis() - start;
        if (use > 1000) {
            log.warn("scan package[{}] use {}ms, please optimization scan path!", packageName, use);
        } else {
            log.debug("scan package[{}] use {}ms", packageName, use);
        }
        return classes;
    }

    private String cacheKey(Object obj, Phase phase) {
        if (obj instanceof Method) {
            return ((Method) obj).toGenericString() + "." + phase;
        }
        if (obj instanceof Class) {
            return ((Class<?>) obj).getName() + ".CLASS";
        }
        return obj.toString() + "." + phase;
    }

    public String getBasePackage() {
        return basePackage;
    }

    private Class<?> getReturnType(Method method) {
        Class<?> returnType = method.getReturnType();
        return returnType == void.class ? Object.class : returnType;
    }

    private static boolean isAssignable(Class<?>[] sup, Class<?>[] sub) {
        return ClassUtils.isAssignable(sup, sub);
    }

    private static boolean isAssignable(List<Class<?>> sup, Class<?>[] sub) {
        return isAssignable(sup.toArray(new Class[sup.size()]), sub);
    }

}
