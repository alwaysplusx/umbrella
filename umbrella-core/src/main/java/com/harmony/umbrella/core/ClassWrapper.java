package com.harmony.umbrella.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.harmony.umbrella.UmbrellaProperties;
import com.harmony.umbrella.io.ResourceManager;
import com.harmony.umbrella.util.ClassUtils;
import com.harmony.umbrella.util.PropUtils;

/**
 * @author wuxii@foxmail.com
 */
@SuppressWarnings("rawtypes")
public class ClassWrapper<T> {

    private static final Class[] classes;

    static {
        classes = ResourceManager.getInstance().getClasses(PropUtils.getSystemProperty("wrapper.classes", UmbrellaProperties.DEFAULT_PACKAGE));
    }

    private static final Map<Class, Set<Class>> suberClassMap = new HashMap<Class, Set<Class>>();

    private Class<T> thisClass;

    private Set<Class> suberClasses;

    public ClassWrapper(Class<T> thisClass) {
        this.thisClass = thisClass;
    }

    public Class<?>[] getAllInterfaces() {
        return ClassUtils.getAllInterfaces(this.thisClass);
    }

    public Class<?>[] getAllSuberClasses() {
        return getSubClassesSet().toArray(new Class[suberClasses.size()]);
    }

    private Set<Class> getSubClassesSet() {
        if (suberClasses == null) {
            suberClasses = findSuberClasses(thisClass);
        }
        return suberClasses;
    }

    public Class<?> getSuperClass() {
        return thisClass.getSuperclass();
    }

    public boolean hasParent() {
        return getSuperClass() != Object.class;
    }

    @SuppressWarnings("unchecked")
    public ClassWrapper<?> getParent() {
        if (hasParent()) {
            return new ClassWrapper(getSuperClass());
        }
        return null;
    }

    public boolean isThisClass(Class<?> clazz) {
        return this.thisClass == clazz;
    }

    public boolean isSuberClassOf(Class<?> clazz) {
        return getSubClassesSet().contains(clazz);
    }

    public boolean isParentClassOf(Class<?> clazz) {
        return clazz == getSuperClass();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ClassWrapper<?> that = (ClassWrapper<?>) o;
        return thisClass != null ? thisClass.equals(that.thisClass) : that.thisClass == null;
    }

    @Override
    public int hashCode() {
        return thisClass != null ? thisClass.hashCode() : 0;
    }

    private static Set<Class> findSuberClasses(Class<?> thisClass) {
        Set<Class> suberClasses = suberClassMap.get(thisClass);
        if (suberClasses == null) {
            suberClasses = new HashSet<Class>();
            for (Class c : ClassWrapper.classes) {
                if (ClassUtils.isAssignable(thisClass, c) && thisClass != c) {
                    suberClasses.add(c);
                }
            }
            suberClassMap.put(thisClass, suberClasses);
        }
        return suberClasses;
    }

}
