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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harmony.umbrella.asm.ClassReader;
import com.harmony.umbrella.io.Resource;
import com.harmony.umbrella.io.support.PathMatchingResourcePatternResolver;
import com.harmony.umbrella.io.support.ResourcePatternResolver;
import com.harmony.umbrella.util.ClassUtils.ClassFilter;

/**
 * @author wuxii@foxmail.com
 */
public abstract class ResourceScaner {

    private final static Logger log = LoggerFactory.getLogger(ResourceScaner.class);

    private static final String RESOURCE_PATTERN = ResourcePatternResolver.ALL_RESOURCE_PATTERN;
    private static final String CLASS_PATTERN = ResourcePatternResolver.ALL_CLASS_PATTERN;

    private static ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

    /**
     * 扫描packageNames下的类
     * 
     * @param packageNames
     *            扫描的包
     * @return 该包下所有的类
     */
    public static Class<?>[] scanPackage(String... packageNames) {
        return filterResource(scanClass(packageNames), null);
    }

    /**
     * 加载包basePackage下符合{@linkplain ClassFilter}的所有类
     * 
     * @param packageNames
     *            包
     * @param filter
     *            class过滤
     * @return 包下的所有类
     */
    public static Class<?>[] scanPackage(String packageNames, ClassFilter filter) {
        return filterResource(scanClass(packageNames), filter);
    }

    /**
     * 加载包basePackage下符合{@linkplain ClassFilter}的所有类
     * 
     * @param packageNames
     *            包
     * @param filter
     *            class过滤
     * @return 包扫描到的所有类
     */
    public static Class<?>[] scanPackage(String[] packageNames, ClassFilter filter) {
        return filterResource(scanClass(packageNames), filter);
    }

    private static Class<?>[] filterResource(Resource[] resources, ClassFilter filter) {
        Collection<Class<?>> classes = new HashSet<Class<?>>(resources.length);
        for (Resource resource : resources) {
            InputStream inStream = null;
            try {
                inStream = resource.getInputStream();
                ClassReader cr = new ClassReader(inStream);
                Class<?> clazz = forName(toClassName(cr.getClassName()));
                if (clazz != null && (filter == null || filter.accept(clazz))) {
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
                }
            }
        }

        if (classes.size() > 1) {
            classes = sortClass(classes);
        }

        return classes.toArray(new Class[classes.size()]);
    }

    /**
     * 扫描paths下的所有资源
     * 
     * @param paths
     *            扫描的路径
     * @return 路径下对应的资源
     * @see AntPathMatcher
     */
    public static Resource[] scanPath(String... paths) {
        Collection<Resource> resources = new HashSet<Resource>();
        for (String path : paths) {
            Collections.addAll(resources, scan(allResourcePath(path)));
        }
        if (resources.size() > 1) {
            resources = sortResource(resources);
        }
        return resources.toArray(new Resource[resources.size()]);
    }

    /**
     * 扫描路径下的所有class资源
     * 
     * @param paths
     *            路径
     * @return 所有以{@code *.class}结尾的资源
     */
    public static Resource[] scanClass(String... paths) {
        Collection<Resource> resources = new HashSet<Resource>();

        for (String path : paths) {
            Collections.addAll(resources, scan(allClassPath(path)));
        }

        if (resources.size() > 1) {
            resources = sortResource(resources);
        }

        return resources.toArray(new Resource[resources.size()]);
    }

    private static Collection<Resource> sortResource(Collection<Resource> resources) {
        List<Resource> result = new ArrayList<Resource>(resources);
        Collections.sort(result, new Comparator<Resource>() {
            @Override
            public int compare(Resource o1, Resource o2) {
                return o1.toString().compareTo(o2.toString());
            }
        });
        return result;
    }

    @SuppressWarnings("rawtypes")
    private static Collection<Class<?>> sortClass(Collection<Class<?>> classes) {
        List<Class<?>> result = new ArrayList<Class<?>>(classes);
        Collections.sort(result, new Comparator<Class>() {
            @Override
            public int compare(Class o1, Class o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        return result;
    }

    /**
     * 根据路径表达式加载资源
     * 
     * @param pathExpression
     *            资源的路径
     * @return 该路径对于的资源
     * @see AntPathMatcher
     */
    public static Resource[] scan(String pathExpression) {
        try {
            return resourcePatternResolver.getResources(pathExpression);
        } catch (IOException e) {
            return new Resource[0];
        }
    }

    /**
     * 将path转化为，该路径下所有资源的ant path表示
     * 
     * <pre>
     * com.harmony -> classpath*:com/harmony<span>/**</span>/*
     * com/harmony -> classpath*:com/harmony<span>/**</span>/*
     * </pre>
     * 
     * @param path
     *            资源路径
     * @return 所有资源匹配路径
     */
    private static String allResourcePath(String path) {
        if (path != null && path.endsWith(RESOURCE_PATTERN)) {
            return path;
        }
        return ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + toPath(path) + "/" + RESOURCE_PATTERN;
    }

    /**
     * 将path转化为，该路径下匹配所有class的路径ant path表达式
     * 
     * <pre>
     * com.harmony -> classpath*:com/harmony<span>/**</span>/*.class
     * com/harmony -> classpath*:com/harmony<span>/**</span>/*.class
     * </pre>
     */
    private static String allClassPath(String pkg) {
        if (pkg != null && pkg.endsWith(CLASS_PATTERN)) {
            return pkg;
        }
        return ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + toPath(pkg) + "/" + CLASS_PATTERN;
    }

    private static String toPath(String basePackage) {
        if (StringUtils.isBlank(basePackage)) {
            return "";
        }
        return basePackage.replace(".", "/");
    }

    private static String toClassName(String className) {
        if (StringUtils.isBlank(className)) {
            return "";
        }
        return className.replace("/", ".");
    }

    /**
     * 检测className是否存在
     */
    private static Class<?> forName(String className) {
        try {
            return Class.forName(className, false, ClassUtils.getDefaultClassLoader());
        } catch (Exception e) {
            return null;
        }
    }

}
