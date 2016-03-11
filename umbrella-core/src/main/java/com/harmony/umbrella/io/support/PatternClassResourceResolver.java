package com.harmony.umbrella.io.support;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.harmony.umbrella.asm.ClassReader;
import com.harmony.umbrella.io.Resource;
import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;
import com.harmony.umbrella.util.ClassUtils;

/**
 * @author wuxii@foxmail.com
 */
public class PatternClassResourceResolver implements ClassResourceResolver {

    private static final Log log = Logs.getLog(PatternClassResourceResolver.class);

    private static final String CLASS_EXTENSION = ".class";

    private ResourcePatternResolver patternResolver;

    public PatternClassResourceResolver(ResourcePatternResolver resourcePatternResolver) {
        this.patternResolver = resourcePatternResolver;
    }

    @Override
    public Resource getResource(String location) {
        return patternResolver.getResource(location);
    }

    @Override
    public ClassLoader getClassLoader() {
        return patternResolver.getClassLoader();
    }

    @Override
    public Resource[] getResources(String locationPattern) throws IOException {
        return patternResolver.getResources(locationPattern);
    }

    @Override
    public Class<?>[] getClassResources(String locationPattern) throws IOException {
        Resource[] resources = null;
        if (locationPattern.endsWith(CLASS_EXTENSION)) {
            resources = patternResolver.getResources(locationPattern);
        } else {
            resources = patternResolver.getResources(addClassExtensionToPattern(locationPattern));
        }
        return toClasses(resources);
    }

    private Class<?>[] toClasses(Resource[] resources) throws IOException {
        List<Class<?>> classes = new ArrayList<Class<?>>(resources.length);
        for (Resource res : resources) {
            InputStream is = null;
            try {
                is = res.getInputStream();
                String className = new ClassReader(is).getClassName();
                Class<?> clazz = forName(className);
                if (clazz != null) {
                    classes.add(clazz);
                }
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        }
        return classes.toArray(new Class[classes.size()]);
    }

    private Class<?> forName(String className) {
        try {
            return Class.forName(className.replace("/", "."), true, ClassUtils.getDefaultClassLoader());
        } catch (NoClassDefFoundError e) {
            log.warn("{} in classpath jar no fully configured, {}", className, e.toString());
        } catch (Throwable e) {
            log.error("{}", className, e);
        }
        return null;
    }

    private String addClassExtensionToPattern(String locationPattern) {
        return locationPattern + (locationPattern.endsWith("/") ? "" : "/") + "**/*" + CLASS_EXTENSION;
    }

    public ResourcePatternResolver getPatternResolver() {
        return patternResolver;
    }

    public void setPatternResolver(ResourcePatternResolver patternResolver) {
        this.patternResolver = patternResolver;
    }

}