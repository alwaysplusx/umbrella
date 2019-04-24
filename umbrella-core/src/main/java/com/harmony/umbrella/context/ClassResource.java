package com.harmony.umbrella.context;

import org.springframework.core.io.Resource;
import org.springframework.util.ClassUtils;

import java.util.Collections;
import java.util.Set;

/**
 * @author wuxii
 */
public class ClassResource {

    private final ClassLoader classLoader;
    private final Resource resource;
    private final String className;
    private final String superClassName;
    private final Set<String> interfaceNames;

    public ClassResource(ClassLoader classLoader, Resource resource, String className,
                         String superClassName, Set<String> interfaceNames) {
        this.classLoader = classLoader;
        this.resource = resource;
        this.className = className;
        this.superClassName = superClassName;
        this.interfaceNames = interfaceNames;
    }

    public Resource getResource() {
        return resource;
    }

    public String getClassName() {
        return className;
    }

    public String getSuperClassName() {
        return superClassName;
    }

    public Set<String> getInterfaceNames() {
        return Collections.unmodifiableSet(interfaceNames);
    }

    public Class<?> forClass() {
        return forClass(classLoader);
    }

    public Class<?> forClass(ClassLoader loader) {
        try {
            return ClassUtils.forName(className, loader);
        } catch (Throwable e) {
        }
        return null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((className == null) ? 0 : className.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ClassResource other = (ClassResource) obj;
        if (className == null) {
            if (other.className != null)
                return false;
        } else if (!className.equals(other.className))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return className;
    }
}
