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
package com.harmony.modules.io.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.objectweb.asm.ClassReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harmony.modules.io.PathMatchingResourcePatternResolver;
import com.harmony.modules.io.Resource;
import com.harmony.modules.io.ResourcePatternResolver;
import com.harmony.modules.utils.ClassFilter;
import com.harmony.modules.utils.ClassUtils;

/**
 * @author wuxii
 *
 */
public class ResourceScaner {

    static final String resourcePattern = "**/*";
    static final String classPattern = "**/*.class";
    private final static Logger log = LoggerFactory.getLogger(ResourceScaner.class);
    @SuppressWarnings("rawtypes")
    private static Map<String, Set> scanCache = new HashMap<String, Set>();
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
                    log.warn("can't open resource {} ", resource);
                    log.warn("", e);
                } finally {
                    try {
                        if (inStream != null) {
                            inStream.close();
                        }
                    } catch (IOException e) {
                    }
                }
            }
            scanCache.put(key, classes);
            log.debug("cache classes, cache key {}, classes {}", key, classes);
        }
        return classes.toArray(new Class[classes.size()]);
    }

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

    public static Resource[] scanPath(String... paths) throws IOException {
        Set<Resource> resources = new LinkedHashSet<Resource>();
        for (String path : paths) {
            Collections.addAll(resources, scanPath(path));
        }
        return resources.toArray(new Resource[resources.size()]);
    }

    public static Resource[] scanPath(String path) throws IOException {
        String searchPath = antPath(path);
        return scan(searchPath);
    }

    private Resource[] scanClassResource(String basePackage) {
        String searchPackage = antPackage(basePackage);
        return scan(searchPackage);
    }

    /**
     * 根据路径表达式加载资源
     * @param pathExpression
     * @return
     * @see AntPathMatcher
     */
    @SuppressWarnings("unchecked")
    public static Resource[] scan(String pathExpression) {
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

    protected static String resolveBasePackage(String basePackage) {
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

    private static String antPath(String path) {
        return ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + resolveBasePackage(path) + "/" + resourcePattern;
    }

    private static String antPackage(String pkg) {
        return ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + resolveBasePackage(pkg) + "/" + classPattern;
    }

    protected static String cleanClassName(String className) {
        return className.replace("/", ".");
    }
}
