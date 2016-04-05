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
package com.harmony.umbrella.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wuxii@foxmail.com
 */
public class GenericUtils {

    @SuppressWarnings("rawtypes")
    public static Class[] getGeneric(Class clazz) {
        List<Class> list = getGenericList(clazz);
        return list.toArray(new Class[list.size()]);
    }

    @SuppressWarnings({ "rawtypes" })
    public static Map<Class, Class[]> getGenerics(Class<?> clazz) {
        Map<Class, List<Class>> genericMap = new HashMap<Class, List<Class>>();
        getAllGeneric(clazz, genericMap);
        Map<Class, Class[]> result = new HashMap<Class, Class[]>();
        for (Class cls : genericMap.keySet()) {
            List<Class> list = genericMap.get(cls);
            result.put(cls, list.toArray(new Class[list.size()]));
        }
        return result;
    }

    @SuppressWarnings("rawtypes")
    private static void getAllGeneric(Class<?> clazz, Map<Class, List<Class>> map) {
        Type[] types = getGenericTypes(clazz);
        List<Class> generics = map.get(clazz);
        if (generics == null) {
            generics = new ArrayList<Class>();
            map.put(clazz, generics);
        }
        for (Type type : types) {
            if (type instanceof ParameterizedType) {
                for (Type t : ((ParameterizedType) type).getActualTypeArguments()) {
                    if (t instanceof Class) {
                        generics.add((Class) t);
                    }
                }
            }
        }
        Class<?> superclass = clazz.getSuperclass();
        if (superclass != null && Object.class != superclass) {
            getAllGeneric(superclass, map);
        }
        if (clazz.getInterfaces().length > 0) {
            for (Class cls : clazz.getInterfaces()) {
                getAllGeneric(cls, map);
            }
        }
    }

    @SuppressWarnings("rawtypes")
    private static List<Class> getGenericList(Class clazz, List<Class> generics) {
        Type[] types = getGenericTypes(clazz);
        for (Type type : types) {
            if (type instanceof ParameterizedType) {
                for (Type t : ((ParameterizedType) type).getActualTypeArguments()) {
                    if (t instanceof Class) {
                        generics.add((Class) t);
                    }
                }
            }
        }
        return generics;
    }

    @SuppressWarnings("rawtypes")
    private static List<Class> getGenericList(Class clazz) {
        return getGenericList(clazz, new ArrayList<Class>());
    }

    private static Type[] getGenericTypes(Class<?> clazz) {
        List<Type> types = new ArrayList<Type>();
        Type genericSuperclass = clazz.getGenericSuperclass();
        if (genericSuperclass != null && genericSuperclass != Object.class) {
            types.add(genericSuperclass);
        }
        Type[] genericInterfaces = clazz.getGenericInterfaces();
        if (genericInterfaces.length > 0) {
            Collections.addAll(types, clazz.getGenericInterfaces());
        }
        return types.toArray(new Type[types.size()]);
    }

}
