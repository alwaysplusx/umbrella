package com.harmony.umbrella.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.ClassUtils;

import com.harmony.umbrella.asm.ClassReader;
import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;

/**
 * @author wuxii@foxmail.com
 */
public class Scanner {

    private static final Log log = Logs.getLog(Scanner.class);

    public static Class<?>[] scan(Set<String> packages, boolean init, boolean sort) {
        return scan(packages.toArray(new String[packages.size()]), init, sort);
    }

    public static Class<?>[] scan(String[] packages, boolean init, boolean sort) {

        List<Class> result = new ArrayList<>();
        ClassLoader classLoader = ClassUtils.getDefaultClassLoader();
        ResourcePatternResolver resourceLoader = new PathMatchingResourcePatternResolver(classLoader);

        for (String pkg : packages) {
            String path = pathWithWildcard(pkg);
            try {
                Resource[] resources = resourceLoader.getResources(path);
                for (Resource resource : resources) {
                    String className = null;
                    try {
                        className = readClassName(resource);
                        Class<?> clazz = Class.forName(className, init, classLoader);
                        if (!result.contains(clazz)) {
                            result.add(clazz);
                        }
                    } catch (IOException e) {
                        log.error("can't read resource {}", resource);
                    } catch (Error e) {
                        log.warn("{} in classpath jar no fully configured, {}", className, e.toString());
                    } catch (Throwable e) {
                        log.error("{}", className, e);
                    }
                }
            } catch (IOException e) {
                log.error("{} package not found", pkg, e);
            }
        }

        if (sort) {
            Collections.sort(result, new Comparator<Class>() {

                @Override
                public int compare(Class o1, Class o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });
        }
        return result.toArray(new Class[result.size()]);
    }

    private static String readClassName(Resource resource) throws IOException {
        InputStream is = resource.getInputStream();
        byte[] b = IOUtils.toByteArray(is);
        try {
            is.close();
        } catch (IOException e) {
        }
        return new ClassReader(b).getClassName().replaceAll("/", ".");
    }

    private static String pathWithWildcard(String path) {
        return ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + path.replace(".", "/") + "/**/*.class";
    }

}
