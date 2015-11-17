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

import com.harmony.umbrella.util.ClassUtils.ClassFilter;

/**
 * 提供资源缓存的管理类
 * 
 * @author wuxii@foxmail.com
 */
public class ResourceManager {

    /**
     * 所有资源的缓存
     */
    private final Map<String, List<Resource>> resourcesCache = new HashMap<String, List<Resource>>();

    /**
     * 类对象的资源缓存
     */
    private final Map<String, List<Class<?>>> classCache = new HashMap<String, List<Class<?>>>();

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

    /**
     * 扫描包下的类
     * 
     * @param packageName
     *            包名
     * @return 所有类
     */
    public Class<?>[] getClasses(String packageName) {
        List<Class<?>> classes = classCache.get(packageName);
        if (classes == null) {
            synchronized (classCache) {
                classes = classCache.get(packageName);
                if (classes == null) {
                    classes = new ArrayList<Class<?>>();
                    Collections.addAll(classes, ResourceScaner.scanPackage(packageName));
                    classCache.put(packageName, classes);
                }
            }
        }
        return classes.toArray(new Class[classes.size()]);
    }

    /**
     * 扫描包下的类
     * 
     * @param packageName
     *            包名
     * @return 所有类
     */
    public Class<?>[] getClasses(String... packageName) {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        for (String pkg : packageName) {
            Collections.addAll(classes, getClasses(pkg));
        }
        return classes.toArray(new Class[classes.size()]);
    }

    /**
     * 获取包下特定过滤条件的资源
     * 
     * @param packageName
     *            包名
     * @param filter
     *            过滤条件
     * @return 包下所有符合过滤条件的类
     */
    public Class<?>[] getClasses(String packageName, ClassFilter filter) {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        for (Class<?> clazz : getClasses(packageName)) {
            if (filter.accept(clazz)) {
                classes.add(clazz);
            }
        }
        return classes.toArray(new Class[classes.size()]);
    }

    /**
     * 获取包下特定过滤条件的资源
     * 
     * @param packageName
     *            包名
     * @param filter
     *            过滤条件
     * @return 包下所有符合过滤条件的类
     */
    public Class<?>[] getClasses(String[] packageName, ClassFilter filter) {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        for (Class<?> clazz : getClasses(packageName)) {
            if (filter.accept(clazz)) {
                classes.add(clazz);
            }
        }
        return classes.toArray(new Class[classes.size()]);
    }

    /**
     * 扫描路径path下的所有资源
     * 
     * @param path
     *            路径
     * @return 路径下的所有资源
     */
    public Resource[] getResources(String path) {
        List<Resource> resources = resourcesCache.get(path);
        if (resources == null) {
            synchronized (resourcesCache) {
                resources = resourcesCache.get(path);
                if (resources == null) {
                    resources = new ArrayList<Resource>();
                    Collections.addAll(resources, ResourceScaner.scanResources(path));
                    resourcesCache.put(path, resources);
                }
            }
        }
        return resources.toArray(new Resource[resources.size()]);
    }

    /**
     * 指定路径下的所有资源
     * 
     * @param path
     *            资源路径
     * @return 资源路径下的所有资源
     */
    public Resource[] getResources(String... path) {
        List<Resource> resources = new ArrayList<Resource>();
        for (String p : path) {
            Collections.addAll(resources, getResources(p));
        }
        return resources.toArray(new Resource[resources.size()]);
    }

    /**
     * 扫描路径下符合条件的资源
     * 
     * @param path
     *            路径名称
     * @param filter
     *            过滤条件
     * @return 符合条件的资源
     */
    public Resource[] getResources(String path, ResourceFilter filter) {
        List<Resource> resources = new ArrayList<Resource>();
        for (Resource resource : getResources(path)) {
            if (filter.accept(resource)) {
                resources.add(resource);
            }
        }
        return resources.toArray(new Resource[resources.size()]);
    }

    /**
     * 扫描路径下符合条件的资源
     * 
     * @param path
     *            路径名称
     * @param filter
     *            过滤条件
     * @return 符合条件的资源
     */
    public Resource[] getResources(String[] path, ResourceFilter filter) {
        List<Resource> resources = new ArrayList<Resource>();
        for (Resource resource : getResources(path)) {
            if (filter.accept(resource)) {
                resources.add(resource);
            }
        }
        return resources.toArray(new Resource[resources.size()]);
    }

    /**
     * 清除扫描资源过的资源
     */
    public void clearResource() {
        synchronized (resourcesCache) {
            resourcesCache.clear();
        }
    }

    /**
     * 清除指定路径下的缓存资源
     * 
     * @param path
     *            指定的缓存路径
     */
    public void clearResource(String path) {
        synchronized (resourcesCache) {
            resourcesCache.remove(path);
        }
    }

    /**
     * 清除所有扫描缓存的类信息
     */
    public void cleanClass() {
        synchronized (classCache) {
            classCache.clear();
        }
    }

    /**
     * 清除指定报名下的类信息
     * 
     * @param path
     *            指定包名
     */
    public void cleanClass(String path) {
        synchronized (classCache) {
            classCache.remove(path);
        }
    }

    /**
     * 资源过滤器
     * 
     * @author wuxii@foxmail.com
     */
    public interface ResourceFilter {

        /**
         * 过滤资源，通过过滤条件返回true, 不通过返回false
         * 
         * @param resource
         *            待过滤的资源
         * @return 通过true, 不通过false
         */
        boolean accept(Resource resource);

    }
}
