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
package com.harmony.umbrella.io;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.harmony.umbrella.io.support.ClassResourceResolver;
import com.harmony.umbrella.io.support.PathMatchingResourcePatternResolver;
import com.harmony.umbrella.io.support.PatternClassResourceResolver;
import com.harmony.umbrella.io.support.ResourcePatternResolver;
import com.harmony.umbrella.util.Assert;
import com.harmony.umbrella.util.ClassUtils.ClassFilter;

/**
 * 资源管理器，对路径对应的资源进行缓存处理
 * 
 * @author wuxii@foxmail.com
 */
public class ResourceManager implements ResourceLoader {

    private final Map<String, Resource[]> resourcesCache = new ConcurrentHashMap<String, Resource[]>();
    private final Map<String, Class<?>[]> classCache = new ConcurrentHashMap<String, Class<?>[]>();

    private ClassResourceResolver classResourceResolver = new PatternClassResourceResolver(new PathMatchingResourcePatternResolver());

    private static ResourceManager INSTANCE;

    private ResourceManager() {
    }

    public static ResourceManager getInstance() {
        if (INSTANCE == null) {
            synchronized (ResourceManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ResourceManager();
                }
            }
        }
        return INSTANCE;
    }
    
    @Override
    public Resource getResource(String location) {
        return classResourceResolver.getResource(location);
    }

    @Override
    public ClassLoader getClassLoader() {
        return classResourceResolver.getClassLoader();
    }

    public Class<?>[] getClasses(String packageName) {
        String pattern = toClassPattern(packageName);
        return getCachedClasses(pattern);
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

    public Resource[] getResources(String path) {
        String pattern = toResourcePattern(path);
        return getCachedResources(pattern);
    }

    public Resource[] getResources(String path, ResourceFilter filter) {
        List<Resource> resources = new ArrayList<Resource>();
        for (Resource res : getResources(path)) {
            if (filter.accept(res)) {
                resources.add(res);
            }
        }
        return resources.toArray(new Resource[resources.size()]);
    }

    private Class<?>[] getCachedClasses(String pattern) {
        Class<?>[] classes = classCache.get(pattern);
        if (classes == null) {
            synchronized (classCache) {
                try {
                    classes = classCache.get(pattern);
                    if (classes == null) {
                        classes = classResourceResolver.getClassResources(pattern);
                        classCache.put(pattern, classes);
                    }
                } catch (IOException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        }
        // copy resource at last step
        Class<?>[] result = new Class[classes.length];
        System.arraycopy(classes, 0, result, 0, classes.length);
        return result;
    }

    private Resource[] getCachedResources(String pattern) {
        Resource[] resources = resourcesCache.get(pattern);
        if (resources == null) {
            synchronized (resourcesCache) {
                try {
                    resources = resourcesCache.get(pattern);
                    if (resources == null) {
                        resources = classResourceResolver.getResources(pattern);
                        resourcesCache.put(pattern, resources);
                    }
                } catch (IOException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        }
        Resource[] result = new Resource[resources.length];
        System.arraycopy(resources, 0, result, 0, resources.length);
        return result;
    }

    public static void sortClasses(Class<?>[] classes) {
        sortClasses(classes, true);
    }

    public static void sortClasses(Class<?>[] classes, final boolean asc) {
        Arrays.sort(classes, new Comparator<Class<?>>() {
            @Override
            public int compare(Class<?> o1, Class<?> o2) {
                if (asc) {
                    return o1.getName().compareTo(o2.getName());
                }
                return o2.getName().compareTo(o1.getName());
            }
        });
    }

    public static void sortResources(Resource[] resources) {
        sortResources(resources, true);
    }

    public static void sortResources(Resource[] resources, final boolean asc) {
        Arrays.sort(resources, new Comparator<Resource>() {
            @Override
            public int compare(Resource o1, Resource o2) {
                if (asc) {
                    return o1.toString().compareTo(o2.toString());
                }
                return o2.toString().compareTo(o1.toString());
            }
        });
    }

    public static String toResourcePattern(String path) {
        Assert.notNull(path, "path not allow null");
        if (path.endsWith("*")) {
            return path;
        }
        return path + "**/*";
    }

    public static String toClassPattern(String path) {
        Assert.notNull(path, "path not allow null");
        StringBuilder sb = new StringBuilder(path.replace(".", "/"));

        if (!path.startsWith(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX) //
                && !path.startsWith(ResourcePatternResolver.CLASSPATH_URL_PREFIX)) {
            sb.insert(0, ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX);
        }

        return sb.append(path.endsWith("/") ? "" : "/").append("**/*.class").toString();
    }

}
