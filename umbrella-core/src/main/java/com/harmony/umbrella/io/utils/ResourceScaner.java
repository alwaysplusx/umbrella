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
package com.harmony.umbrella.io.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harmony.umbrella.asm.ClassReader;
import com.harmony.umbrella.core.ClassFilter;
import com.harmony.umbrella.io.PathMatchingResourcePatternResolver;
import com.harmony.umbrella.io.Resource;
import com.harmony.umbrella.io.ResourcePatternResolver;
import com.harmony.umbrella.utils.ClassUtils;

/**
 * @author wuxii@foxmail.com
 */
public class ResourceScaner {

    private final static Logger log = LoggerFactory.getLogger(ResourceScaner.class);
    static final String resourcePattern = ResourcePatternResolver.ALL_RESOURCE_PATTERN;
    static final String classPattern = ResourcePatternResolver.ALL_CLASS_PATTERN;
    @SuppressWarnings("rawtypes")
    private Map<String, Set> scanCache = new HashMap<String, Set>();
    private static ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
    private static ResourceScaner instance;

    private ResourceScaner() {
    }

    public static ResourceScaner getInstance() {
        if (instance == null) {
            synchronized (ResourceScaner.class) {
                if (instance == null) {
                    instance = new ResourceScaner();
                }
            }
        }
        return instance;
    }

    /**
     * 扫描basePackage下的类
     * 
     * @param basePackage
     *            包
     * @return
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public Class<?>[] scanPackage(String basePackage) throws IOException {
        String key = antPackage(basePackage) + ".CLASS";
        Set<Class<?>> classes = scanCache.get(key);
        if (classes == null) {
            classes = new LinkedHashSet<Class<?>>();
            for (Resource resource : scanClassResource(basePackage)) {
                InputStream inStream = null;
                try {
                    inStream = resource.getInputStream();
                    ClassReader cr = new ClassReader(inStream);
                    String className = cleanClassName(cr.getClassName());
                    Class<?> clazz = checkClassAccess(className);
                    if (clazz != null) {
                        classes.add(clazz);
                    }
                } catch (IOException e) {
                    log.warn("can't open resource {} ", resource, e);
                } finally {
                    try {
                        if (inStream != null) {
                            inStream.close();
                        }
                    } catch (IOException e) {
                        log.debug("", e);
                    }
                }
            }
            scanCache.put(key, classes);
            log.debug("cache classes, cache key {}, classes {}", key, classes);
        }
        return classes.toArray(new Class[classes.size()]);
    }

    /**
     * 加载包basePackage下符合{@linkplain ClassFilter}的所有类
     * 
     * @param basePackage
     *            包
     * @param filter
     *            class过滤
     * @return
     * @throws IOException
     */
    public Class<?>[] scanPackage(String basePackage, ClassFilter filter) throws IOException {
        Set<Class<?>> classes = new LinkedHashSet<Class<?>>();
        for (Class<?> clazz : scanPackage(basePackage)) {
            try {
                if (filter.accept(clazz)) {
                    classes.add(clazz);
                }
            } catch (Throwable e) {
                log.debug("", e);
            }
        }
        return classes.toArray(new Class[classes.size()]);
    }

    /**
     * 扫描paths下的资源
     * 
     * @param paths
     * @return
     * @throws IOException
     * @see AntPathMatcher
     */
    public Resource[] scanPath(String... paths) throws IOException {
        Set<Resource> resources = new LinkedHashSet<Resource>();
        for (String path : paths) {
            Collections.addAll(resources, scanPath(path));
        }
        return resources.toArray(new Resource[resources.size()]);
    }

    /**
     * 扫描paths下的资源
     * 
     * @param path
     * @return
     * @throws IOException
     * @see AntPathMatcher
     */
    public Resource[] scanPath(String path) throws IOException {
        String searchPath = antPath(path);
        return scan(searchPath);
    }

    private Resource[] scanClassResource(String basePackage) {
        String searchPackage = antPackage(basePackage);
        return scan(searchPackage);
    }

    /**
     * 根据路径表达式加载资源
     * 
     * @param pathExpression
     * @return
     * @see AntPathMatcher
     */
    @SuppressWarnings("unchecked")
    public Resource[] scan(String pathExpression) {
        Set<Resource> resources = scanCache.get(pathExpression);
        try {
            if (resources == null) {
                resources = new LinkedHashSet<Resource>();
                Collections.addAll(resources, resourcePatternResolver.getResources(pathExpression));
                scanCache.put(pathExpression, resources);
                log.debug("cache resources key {} resource {}", pathExpression, resources);
            }
        } catch (IOException e) {
        }
        return resources.toArray(new Resource[resources.size()]);
    }

    /**
     * 清除已经缓冲的资源
     * 
     * @param cacheKey
     */
    public void removeCache(String cacheKey) {
        scanCache.remove(cacheKey);
    }

    protected static String resolveBasePackage(String basePackage) {
        if (basePackage == null)
            return "";
        return basePackage.replace(".", "/");
    }

    protected static Class<?> checkClassAccess(String className) {
        ClassLoader loader = ClassUtils.getDefaultClassLoader();
        try {
            return Class.forName(className, false, loader);
        } catch (Throwable e) {
        }
        return null;
    }

    protected static String antPath(String path) {
        return ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + resolveBasePackage(path) + "/" + resourcePattern;
    }

    protected static String antPackage(String pkg) {
        return ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + resolveBasePackage(pkg) + "/" + classPattern;
    }

    protected static String cleanClassName(String className) {
        return className.replace("/", ".");
    }
}
