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
package com.harmony.umbrella.io;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.harmony.umbrella.util.ResourceScaner;
import com.harmony.umbrella.util.ClassUtils.ClassFilter;

/**
 * @author wuxii@foxmail.com
 */
public class ResourceManager {

    private Map<String, List<Resource>> resourcesCache = new HashMap<String, List<Resource>>();

    private Map<String, List<Class<?>>> classCache = new HashMap<String, List<Class<?>>>();

    private static ResourceManager instance;

    private ResourceManager() {

    }

    public static ResourceManager getInstance() {
        if (instance == null) {
            synchronized (ResourceManager.class) {
                if (instance == null) {
                    instance = new ResourceManager();
                }
            }
        }
        return instance;
    }

    public Class<?>[] getClasses(String[] packageName, ClassFilter filter) {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        for (Class<?> clazz : getClasses(packageName)) {
            if (filter.accept(clazz)) {
                classes.add(clazz);
            }
        }
        return classes.toArray(new Class[classes.size()]);
    }

    public Class<?>[] getClasses(String packageName, ClassFilter filter) {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        for (Class<?> clazz : getClasses(packageName)) {
            if (filter.accept(clazz)) {
                classes.add(clazz);
            }
        }
        return classes.toArray(new Class[classes.size()]);
    }

    public Resource[] getResources(String... path) {
        List<Resource> resources = new ArrayList<Resource>();
        for (String p : path) {
            Collections.addAll(resources, getResources(p));
        }
        return resources.toArray(new Resource[resources.size()]);
    }

    public Resource[] getResources(String path) {
        List<Resource> resources = resourcesCache.get(path);
        if (resources == null) {
            synchronized (resourcesCache) {
                if (!resourcesCache.containsKey(path)) {
                    resources = new ArrayList<Resource>();
                    Collections.addAll(resources, ResourceScaner.scanPath(path));
                    resourcesCache.put(path, resources);
                }
            }
        }
        return resources.toArray(new Resource[resources.size()]);
    }

    public Class<?>[] getClasses(String... packageName) {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        for (String pkg : packageName) {
            Collections.addAll(classes, getClasses(pkg));
        }
        return classes.toArray(new Class[classes.size()]);
    }

    public Class<?>[] getClasses(String packageName) {
        List<Class<?>> classes = classCache.get(packageName);
        if (classes == null) {
            synchronized (classCache) {
                if (!classCache.containsKey(packageName)) {
                    classes = new ArrayList<Class<?>>();
                    Collections.addAll(classes, ResourceScaner.scanPackage(packageName));
                    classCache.put(packageName, classes);
                }
            }
        }
        return classes.toArray(new Class[classes.size()]);
    }

    public void clearResource() {
        synchronized (resourcesCache) {
            resourcesCache.clear();
        }
    }

    public void clearResource(String path) {
        synchronized (resourcesCache) {
            resourcesCache.remove(path);
        }
    }

    public void cleanClass() {
        synchronized (classCache) {
            classCache.clear();
        }
    }

    public void cleanClass(String path) {
        synchronized (classCache) {
            classCache.remove(path);
        }
    }
}
