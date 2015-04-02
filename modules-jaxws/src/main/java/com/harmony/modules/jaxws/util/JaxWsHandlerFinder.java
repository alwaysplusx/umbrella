/*
 * Copyright 2002-2014 the original author or authors.
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
package com.harmony.modules.jaxws.util;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harmony.modules.io.utils.ResourceScaner;
import com.harmony.modules.jaxws.Handler;
import com.harmony.modules.jaxws.Handler.HandleMethod;
import com.harmony.modules.jaxws.JaxWsAbortException;
import com.harmony.modules.jaxws.Phase;
import com.harmony.modules.utils.ClassFilter;

public class JaxWsHandlerFinder {

    private static final Logger log = LoggerFactory.getLogger(JaxWsHandlerFinder.class);
    private static final String ALL_HANDLER_CLASS = Handler.class.getName();
    @SuppressWarnings("rawtypes")
    private Map<String, Set> handlerCache = new HashMap<String, Set>();
    private String basePackage;

    public static JaxWsHandlerFinder newInstance() {
        return new JaxWsHandlerFinder();
    }

    public static JaxWsHandlerFinder newInstance(String basePackage) {
        return new JaxWsHandlerFinder(basePackage);
    }

    private JaxWsHandlerFinder(String basePackage) {
        this.basePackage = basePackage;
    }

    private JaxWsHandlerFinder() {
        this("");
    }

    @SuppressWarnings("unchecked")
    public Class<?>[] getAllHandlerClass() {
        if (handlerCache.containsKey(ALL_HANDLER_CLASS)) {
            Set<Class<?>> classes = (Set<Class<?>>) handlerCache.get(ALL_HANDLER_CLASS);
            return classes.toArray(new Class[classes.size()]);
        }
        Set<Class<?>> handlerClasses = new LinkedHashSet<Class<?>>();
        try {
            Class<?>[] classes = ResourceScaner.getInstance().scanPackage(basePackage, new ClassFilter() {
                @Override
                public boolean accept(Class<?> clazz) {
                    try {
                        if (clazz.isInterface())
                            return false;
                        if (clazz.getModifiers() != Modifier.PUBLIC)
                            return false;
                        if (clazz.getAnnotation(Handler.class) == null)
                            return false;
                        Handler handler = clazz.getAnnotation(Handler.class);
                        if (handler.classes().length == 0 && handler.serviceClass().length == 0)
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
        } catch (IOException e) {
        }
        return handlerClasses.toArray(new Class[handlerClasses.size()]);
    }

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
            handlerCache.put(cacheKey, classes);
        }
        return classes.toArray(new Class[classes.size()]);
    }

    private boolean isMatchHandler(Class<?> serviceClass, Handler handler) {
        Class<?>[] classes = handler.serviceClass();
        for (Class<?> clazz : classes) {
            if (clazz.getName().equals(serviceClass.getName()))
                return true;
        }
        String[] handlers = handler.classes();
        for (String h : handlers) {
            if (serviceClass.getName().equals(h))
                return true;
        }
        return false;
    }

    private String cacheKey(Object obj, Phase phase) {
        if (obj instanceof Method) {
            return ((Method) obj).toGenericString() + "." + phase;
        }
        if (obj instanceof Class) {
            return ((Class<?>) obj).getName();
        }
        return obj.toString() + phase;
    }

    public Method[] findHandlerMethod(Class<?> serviceClass, String methodName, Class<?>[] parameterTypes, Phase phase) throws NoSuchMethodException {
        Method serviceMethod = serviceClass.getMethod(methodName, parameterTypes);
        return findHandlerMethod(serviceMethod, phase);
    }

    @SuppressWarnings("unchecked")
    public Method[] findHandlerMethod(Method serviceMethod, Phase phase) {
        String cacheKey = cacheKey(serviceMethod, phase);
        Set<Method> methods = handlerCache.get(cacheKey);
        if (methods == null) {
            methods = new LinkedHashSet<Method>();
            for (Class<?> handler : findHandlerClass(serviceMethod.getDeclaringClass())) {
                Collections.addAll(methods, filterHandleMethod(serviceMethod, handler, phase));
            }
            handlerCache.put(cacheKey, methods);
        }
        return methods.toArray(new Method[methods.size()]);
    }

    public Method[] filterHandleMethod(Method serviceMethod, Class<?> handler, Phase phase) {
        List<Method> methods = new LinkedList<Method>();
        String name = serviceMethod.getName();
        for (Method hm : handler.getMethods()) {
            if (hm.getDeclaringClass().equals(Object.class))
                continue;
            HandleMethod ann = hm.getAnnotation(HandleMethod.class);
            if (ann != null && phase.equals(ann.phase()) && (ann.methodName().equals(name) || hm.getName().equals(name))) {
                Method method = filterPhaseMethod(serviceMethod, hm, phase);
                if (method != null)
                    methods.add(method);
            }
        }
        return methods.toArray(new Method[methods.size()]);
    }

    protected Method filterPhaseMethod(Method serviceMethod, Method handlerMethod, Phase phase) {
        Class<?>[] st = serviceMethod.getParameterTypes();
        Class<?>[] ht = handlerMethod.getParameterTypes();
        switch (phase) {
        case PRE_INVOKE:
            if (st.length != ht.length) {
                log.debug("{} handler method[{}] parameter length mismatch", phase, handlerMethod);
                return null;
            }
            for (int i = 0; i < st.length; i++) {
                if (!st[i].getName().equals(ht[i].getName())) {
                    log.debug("{} handler method[{}] argument index {} mismatch", phase, handlerMethod, i + 1);
                    return null;
                }
            }
            return handlerMethod;
        case POST_INVOKE:
            if (st.length != ht.length - 1) {
                log.debug("{} handler method[{}] parameter length mismatch", phase, handlerMethod);
                return null;
            }
            if (!serviceMethod.getReturnType().isAssignableFrom(ht[0])) {
                log.debug("{} handler method[{}] reture type mismatch", phase, handlerMethod);
                return null;
            }
            for (int i = 1; i < ht.length; i++) {
                if (!st[i - 1].getName().equals(ht[i].getName())) {
                    log.debug("{} handler method[{}] argument index {} mismatch", phase, handlerMethod, i + 1);
                    return null;
                }
            }
            return handlerMethod;
        case THROWING:
            if (st.length != ht.length - 1) {
                log.debug("{} handler method[{}] parameter length mismatch", phase, handlerMethod);
                return null;
            }
            if (Throwable.class.isAssignableFrom(ht[0])) {
                log.debug("{} handler method[{}] throwable type mismatch", phase, handlerMethod);
                return null;
            }
            for (int i = 1; i < ht.length; i++) {
                if (!st[i - 1].getName().equals(ht[i].getName())) {
                    log.debug("{} handler method[{}] argument index {} mismatch", phase, handlerMethod, i + 1);
                    return null;
                }
            }
            return handlerMethod;
        case ABORT:
            if (st.length != ht.length - 1) {
                log.debug("{} handler method[{}] parameter length mismatch", phase, handlerMethod);
                return null;
            }
            if (JaxWsAbortException.class.isAssignableFrom(ht[0])) {
                log.debug("{} handler method[{}] throwable type mismatch", phase, handlerMethod);
                return null;
            }
            for (int i = 1; i < ht.length; i++) {
                if (!st[i - 1].getName().equals(ht[i].getName())) {
                    log.debug("{} handler method[{}] argument index {} mismatch", phase, handlerMethod, i + 1);
                    return null;
                }
            }
            return handlerMethod;
        case FINALLY:
            if (st.length != ht.length - 2) {
                log.debug("{} handler method[{}] parameter length mismatch", phase, handlerMethod);
                return null;
            }
            if (!Exception.class.isAssignableFrom(ht[0])) {
                log.debug("{} handler method[{}] first parameter require Exception", phase, handlerMethod);
                return null;
            }
            if (!serviceMethod.getReturnType().isAssignableFrom(ht[1])) {
                log.debug("{} handler method[{}] reture type mismatch", phase, handlerMethod);
                return null;
            }
            for (int i = 2; i < ht.length; i++) {
                if (!st[i - 2].getName().equals(ht[i].getName())) {
                    log.debug("{} handler method[{}] argument index {} mismatch", phase, handlerMethod, i + 1);
                    return null;
                }
            }
            return handlerMethod;
        }
        return null;
    }

}
