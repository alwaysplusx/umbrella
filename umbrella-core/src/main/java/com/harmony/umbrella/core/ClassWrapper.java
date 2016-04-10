package com.harmony.umbrella.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.harmony.umbrella.io.ResourceManager;
import com.harmony.umbrella.util.ClassUtils;
import com.harmony.umbrella.util.Environments;

/**
 * Class包装类, 负责加载类路径下的类依赖关系
 * 
 * 可以在包装类中找到对于的父类以及所有子类的树形关系
 * 
 * @author wuxii@foxmail.com
 */
@SuppressWarnings("rawtypes")
public class ClassWrapper<T> {

    public static final String ALL_WRAPPER_CLASS_PACKAGE = ClassWrapper.class.getName() + ".package";
    
    private static final Class[] classes;
    
    static {
        String defaultPackage = Environments.getProperty(ALL_WRAPPER_CLASS_PACKAGE, "com.harmony");
        classes = ResourceManager.getInstance().getClasses(defaultPackage);
    }

    private static final Map<Class, Set<Class>> subClassMap = new HashMap<Class, Set<Class>>();

    private Class<T> thisClass;

    private Set<Class> subClasses;

    public ClassWrapper(Class<T> thisClass) {
        this.thisClass = thisClass;
    }

    public Class<?>[] getAllInterfaces() {
        return ClassUtils.getAllInterfaces(this.thisClass);
    }

    public Class<?>[] getAllSubClasses() {
        return getSubClassesSet().toArray(new Class[subClasses.size()]);
    }

    private Set<Class> getSubClassesSet() {
        if (subClasses == null) {
            subClasses = findSubClasses(thisClass);
        }
        return subClasses;
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

    public boolean isChildClassOf(Class<?> clazz) {
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

    private static Set<Class> findSubClasses(Class<?> thisClass) {
        Set<Class> subClasses = subClassMap.get(thisClass);
        if (subClasses == null) {
            subClasses = new HashSet<Class>();
            for (Class c : ClassWrapper.classes) {
                if (ClassUtils.isAssignable(thisClass, c) && thisClass != c) {
                    subClasses.add(c);
                }
            }
            subClassMap.put(thisClass, subClasses);
        }
        return subClasses;
    }

}
