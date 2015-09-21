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

import static com.harmony.umbrella.util.ClassUtils.*;
import static com.harmony.umbrella.util.StringUtils.*;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harmony.umbrella.Constants;
import com.harmony.umbrella.io.util.ResourceScaner;
import com.harmony.umbrella.util.ClassUtils.ClassFilter;
import com.harmony.umbrella.util.ClassUtils.ClassFilterFeature;
import com.harmony.umbrella.ws.Handler;
import com.harmony.umbrella.ws.Handler.HandleMethod;
import com.harmony.umbrella.ws.Phase;
import com.harmony.umbrella.ws.WebServiceAbortException;

/**
 * @author wuxii@foxmail.com
 */
public class HandlerMethodFinder {

    private static final Logger log = LoggerFactory.getLogger(HandlerMethodFinder.class);
    private static final String ALL_HANDLER_CLASS = Handler.class.getName() + ".CLASS";
    @SuppressWarnings("rawtypes")
    private Map<String, Set> handlerCache = new HashMap<String, Set>();
    private final String basePackage;

    public HandlerMethodFinder(String basePackage) {
        this.basePackage = basePackage;
    }

    /**
     * basePackage为当前classpath
     */
    public HandlerMethodFinder() {
        this(Constants.DEFAULT_PACKAGE);
    }

    /**
     * basePackage下的所有带有annotation的{@linkplain Handler}, 并{@linkplain Handler}中
     * {@linkplain Handler#value()} {@linkplain Handler#handles()}不全为空的class
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
    public Class<?>[] getAllHandlerClass() {
        if (handlerCache.containsKey(ALL_HANDLER_CLASS)) {
            Set<Class<?>> classes = (Set<Class<?>>) handlerCache.get(ALL_HANDLER_CLASS);
            return classes.toArray(new Class[classes.size()]);
        }
        Set<Class<?>> handlerClasses = new LinkedHashSet<Class<?>>();
        Class<?>[] classes = ResourceScaner.scanPackage(basePackage, new ClassFilter() {
            @Override
            public boolean accept(Class<?> clazz) {
                try {
                    if (!ClassFilterFeature.NEWABLE.accept(clazz))
                        return false;
                    if (clazz.getAnnotation(Handler.class) == null)
                        return false;
                    Handler handler = clazz.getAnnotation(Handler.class);
                    if (handler.value().length == 0 && handler.handles().length == 0)
                        return false;
                    return true;
                } catch (Throwable e) {
                    return false;
                }
            }
        });
        if (classes.length > 0) {
            Collections.addAll(handlerClasses, classes);
            handlerCache.put(ALL_HANDLER_CLASS, handlerClasses);
        }
        log.info("all @Handler classes {}", handlerClasses);
        return handlerClasses.toArray(new Class[handlerClasses.size()]);
    }

    /**
     * basePackage下所有{@linkplain Handler}的{@linkplain Handler#value()}或者
     * {@linkplain Handler#handles()}值包含serviceClass的类
     * 
     * @param serviceClass
     * @return
     */
    @SuppressWarnings("unchecked")
    public Class<?>[] findHandlerClass(Class<?> serviceClass) {
        String cacheKey = cacheKey(serviceClass, null);
        Set<Class<?>> classes = handlerCache.get(cacheKey);
        if (classes == null) {
            classes = new LinkedHashSet<Class<?>>();
            for (Class<?> clazz : getAllHandlerClass()) {
                Handler handler = clazz.getAnnotation(Handler.class);
                if (isMatchHandler(serviceClass, handler))
                    classes.add(clazz);
            }
            log.info("{} @Handler classes -> {}]", cacheKey, classes);
            handlerCache.put(cacheKey, classes);
        }
        return classes.toArray(new Class[classes.size()]);
    }

    /**
     * 判断Handler中handles或value中是否有serviceClass
     */
    private boolean isMatchHandler(Class<?> serviceClass, Handler handler) {
        Class<?>[] classes = handler.value();
        for (Class<?> clazz : classes) {
            if (canonicalNameEquals(serviceClass, clazz))
                return true;
        }
        String[] handlers = handler.handles();
        for (String h : handlers) {
            if (serviceClass.getName().equals(h))
                return true;
        }
        return false;
    }

    /**
     * {@linkplain #basePackage}下符合serviceMethod周期为Phase的处理方法
     * 
     * @param serviceMethod
     *            当前执行的方法
     * @param phase
     *            执行周期
     * @return
     */
    public HandleMethodInvoker[] findHandleMethods(Method serviceMethod, Phase phase) {
        List<HandleMethodInvoker> result = new LinkedList<HandleMethodInvoker>();
        for (Class<?> clazz : getAllHandlerClass()) {
            Collections.addAll(result, findHandleMethods(serviceMethod, clazz, phase));
        }
        log.debug("{}.{} handle method {}", getMethodId(serviceMethod), phase, result);
        return result.toArray(new HandleMethodInvoker[result.size()]);
    }

    /**
     * handlerClass中符合serviceMethod周期为Phase的处理方法
     * 
     * @param serviceMethod
     * @param handlerClass
     * @param phase
     * @return
     */
    protected HandleMethodInvoker[] findHandleMethods(Method serviceMethod, Class<?> handlerClass, Phase phase) {
        List<HandleMethodInvoker> result = new LinkedList<HandleMethodInvoker>();
        String name = serviceMethod.getName();
        for (Method handleMethod : handlerClass.getMethods()) {
            if (handleMethod.getDeclaringClass().equals(Object.class))
                continue;
            HandleMethod ann = handleMethod.getAnnotation(HandleMethod.class);
            // annotation不为空、周期相同、方法名与annotation相同或与handleMethod相同
            if (ann != null && phase.equals(ann.phase()) && (ann.methodName().equals(name) || handleMethod.getName().equals(name))
                    && isMatchHandleMethod(serviceMethod, handleMethod, phase)) {

                HandleMethodReflectInvoker hmi = new HandleMethodReflectInvoker(handlerClass, handleMethod, phase);
                Class<?>[] parameterTypes = handleMethod.getParameterTypes();
                hmi.setEndWithMap(canonicalNameEquals(Map.class, parameterTypes[parameterTypes.length - 1]));
                result.add(hmi);

            }
        }
        return result.toArray(new HandleMethodInvoker[result.size()]);
    }

    /**
     * 主要检查handleMethod对应的方法参数是否符合serviceMethod的方法参数要求.
     * <p>
     * handlerMethod可以在最后添加一个Map参数(非必要)
     * 
     * @param serviceMethod
     * @param handleMethod
     * @param phase
     * @return
     * @see HandleMethod
     */
    protected boolean isMatchHandleMethod(Method serviceMethod, Method handleMethod, Phase phase) {
        // 接口的方法参数类型
        final Class<?>[] serviceParamTypes = serviceMethod.getParameterTypes();
        // handler的方法参数类型
        final Class<?>[] handleParamTypes = handleMethod.getParameterTypes();
        List<Class<?>> types = new LinkedList<Class<?>>();
        Collections.addAll(types, serviceParamTypes);
        if (log.isDebugEnabled()) {
            log.debug("test handle method match\n{\n  {}.{}\n  service->{}\n  {}\n  handler->{}\n}", getMethodId(serviceMethod), phase,
                    typeString(serviceParamTypes), getMethodId(handleMethod), typeString(handleParamTypes));
        }
        switch (phase) {
        case PRE_INVOKE:
            if (!isAssignable(serviceParamTypes, handleParamTypes)) {
                types.add(Map.class);
                return isAssignable(types.toArray(new Class[types.size()]), handleParamTypes);
            }
            return true;
        case ABORT:
            types.add(0, WebServiceAbortException.class);
            if (!isAssignable(types.toArray(new Class[types.size()]), handleParamTypes)) {
                types.add(Map.class);
                return isAssignable(types.toArray(new Class[types.size()]), handleParamTypes);
            }
            return true;
        case POST_INVOKE:
            types.add(0, getReturnType(serviceMethod));
            if (!isAssignable(types.toArray(new Class[types.size()]), handleParamTypes)) {
                types.add(Map.class);
                return isAssignable(types.toArray(new Class[types.size()]), handleParamTypes);
            }
            return true;
        case THROWING:
            types.add(0, Exception.class);
            if (!isAssignable(types.toArray(new Class[types.size()]), handleParamTypes)) {
                types.add(Map.class);
                return isAssignable(types.toArray(new Class[types.size()]), handleParamTypes);
            }
            return true;
        case FINALLY:
            types.add(0, Exception.class);
            types.add(1, getReturnType(serviceMethod));
            if (!isAssignable(types.toArray(new Class[types.size()]), handleParamTypes)) {
                types.add(Map.class);
                return isAssignable(types.toArray(new Class[types.size()]), handleParamTypes);
            }
            return true;
        }
        return false;
    }

    protected Class<?> getReturnType(Method method) {
        Class<?> returnType = method.getReturnType();
        return returnType == void.class ? Object.class : returnType;
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

    private String cacheKey(Object obj, Phase phase) {
        if (obj instanceof Method) {
            return ((Method) obj).toGenericString() + "." + phase;
        }
        if (obj instanceof Class) {
            return ((Class<?>) obj).getName() + ".CLASS";
        }
        return obj.toString() + "." + phase;
    }

}
