package com.harmony.umbrella.util;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;

import com.harmony.umbrella.util.GenericUtils.GenericTree.Generic;

/**
 * 泛型工具类
 * 
 * @author wuxii@foxmail.com
 */
public class GenericUtils {

    // FIXME 方法的泛型解析
    public static GenericTree[] parseMethod(Method method) {
        return parseMethod(method, method.getDeclaringClass());
    }

    public static GenericTree[] parseMethod(Method method, Class<?> origin) {
        Type[] types = method.getGenericParameterTypes();
        List<GenericTree> result = new ArrayList<>(types.length);
        for (Type type : types) {
            GenericTree tmp = null;
            if (type instanceof Class) {
                tmp = buildGenericTree((Class) type, null);
            } else if (type instanceof ParameterizedType) {
                Type rawType = ((ParameterizedType) type).getRawType();
                tmp = buildGenericTree((Class) rawType, type);
            } else if (type instanceof TypeVariable) {
                ((TypeVariable) type).getBounds();
                ((TypeVariable) type).getName();
            } else {
                throw new IllegalArgumentException("");
            }
            recursiveBuild(tmp);
            recursiveCalculateRelevance(tmp);
            result.add(tmp);
        }
        return result.toArray(new GenericTree[result.size()]);
    }

    public static GenericTree parse(Class<?> clazz) {
        GenericTree result = buildGenericTree(clazz, null);
        recursiveBuild(result);
        recursiveCalculateRelevance(result);
        return result;
    }

    private static GenericTree recursiveBuild(GenericTree type) {
        Class<?> clazz = type.getJavaType();
        if (clazz == null//
                || clazz == Object.class) {
            return type;
        }

        if (!clazz.isInterface()) {
            GenericTree superGenericTree = buildGenericTree(clazz.getSuperclass(), clazz.getGenericSuperclass());
            recursiveBuild(superGenericTree);
            superGenericTree.subOrImpl = type;
            type.superGeneric = superGenericTree;
        }

        Class<?>[] interfaces = clazz.getInterfaces();
        Type[] genericInterfaces = clazz.getGenericInterfaces();
        for (int i = 0, max = interfaces.length; i < max; i++) {
            GenericTree interfaceGenericTree = buildGenericTree(interfaces[i], genericInterfaces[i]);
            recursiveBuild(interfaceGenericTree);
            interfaceGenericTree.subOrImpl = type;
            type.interfacesGenerics.add(interfaceGenericTree);
        }

        return type;
    }

    /**
     * 构建类的泛型
     * 
     * @param clazz
     *            class
     * @param genericType
     *            当前类包含泛型内容
     * @return
     */
    private static GenericTree buildGenericTree(Class<?> clazz, Type genericType) {
        GenericTree tree = new GenericTree(clazz);
        List<Generic> generics = new ArrayList<>();
        List<String> genericNames = genericNames(clazz);
        if (genericType == null || genericType == clazz) {
            // 无泛型通过类上的泛型文本解析得到泛型的名称(无法获得java类)
            for (int i = 0, max = genericNames.size(); i < max; i++) {
                generics.add(tree.new Generic(genericNames.get(i), i));
            }
        } else if (genericType instanceof ParameterizedType) {
            // 类? 接口?
            Type[] types = ((ParameterizedType) genericType).getActualTypeArguments();
            for (int i = 0, max = types.length; i < max; i++) {
                Type generic = types[i];
                String name = generic instanceof TypeVariable ? ((TypeVariable) generic).getName() : genericNames.get(i);
                generics.add(tree.new Generic(types[i], name, i));
            }
        }
        tree.generics = generics;
        return tree;
    }

    private static List<String> genericNames(Class<?> clazz) {
        List<String> names = new ArrayList<>();
        String genericString = clazz.toGenericString();
        int index = genericString.indexOf("<");
        if (index != -1) {
            String[] array = genericString.substring(index + 1, genericString.length() - 1).split(",");
            for (String s : array) {
                names.add(s.trim());
            }
        }
        return names;
    }

    private static void recursiveCalculateRelevance(GenericTree type) {

        if (type.generics != null && !type.generics.isEmpty() //
                && type.subOrImpl != null && type.subOrImpl.generics != null && !type.subOrImpl.generics.isEmpty()) {
            for (Generic generic : type.generics) {
                if (!generic.isResolved()) {
                    generic.resolve(type.subOrImpl);
                }
            }
        }

        if (!type.interfacesGenerics.isEmpty() && type.generics != null) {
            for (GenericTree ig : type.interfacesGenerics) {
                if (ig.generics == null || ig.generics.isEmpty()) {
                    continue;
                }
                for (Generic generic : ig.generics) {
                    if (!generic.isResolved()) {
                        generic.resolve(type);
                    }
                }
            }
        }

        if (type.superGeneric != null) {
            recursiveCalculateRelevance(type.superGeneric);
        }

        for (GenericTree ig : type.interfacesGenerics) {
            recursiveCalculateRelevance(ig);
        }

    }

    /**
     * 类对应的泛型树
     * 
     * @author wuxii@foxmail.com
     */
    public static class GenericTree {

        /**
         * 当前类
         */
        private Class<?> type;
        /**
         * 当前类的父类
         */
        private GenericTree superGeneric;
        /**
         * 当前类的interface
         */
        private List<GenericTree> interfacesGenerics = new ArrayList<>();
        /**
         * 当前类的泛型
         */
        private List<Generic> generics;

        private GenericTree subOrImpl;

        private GenericTree(Class<?> type) {
            this.type = type;
        }

        private GenericTree(Class<?> type, List<Generic> generics) {
            this.type = type;
            this.generics = generics;
        }

        public Class<?> getJavaType() {
            return type;
        }

        public Class<?> getSuperClass() {
            return type.getSuperclass();
        }

        public Class<?> getInterface(int index) {
            return type.getInterfaces()[index];
        }

        public GenericTree getSuperGeneric() {
            return superGeneric;
        }

        public GenericTree getInterfaceGeneric(int index) {
            return interfacesGenerics.get(index);
        }

        public Generic[] getGenerics() {
            return generics == null ? new Generic[0] : generics.toArray(new Generic[generics.size()]);
        }

        public Generic getGeneric(int index) {
            return generics.get(index);
        }

        private Class<?> getGenericType(String name) {
            if (generics != null) {
                for (Generic generic : generics) {
                    if (generic.name != null && generic.name.equals(name) //
                            && generic.type != null && generic.type instanceof Class) {
                        return (Class<?>) generic.type;
                    }
                }
            }
            return null;
        }

        private Class getGenericType(int index) {
            if (generics != null) {
                Generic generic = generics.get(index);
                Type type = generic != null ? generic.type : null;
                return type instanceof Class ? (Class) type : null;
            }
            return null;
        }

        public Generic getTargetGeneric(Class<?> target, int index) {
            return getTargetGenericTree(target).getGeneric(index);
        }

        public GenericTree getTargetGenericTree(Class<?> target) {
            return findTargetGenericTree(this, target);
        }

        private GenericTree findTargetGenericTree(GenericTree source, Class<?> target) {
            if (source.type == target) {
                return source;
            }
            GenericTree result = null;
            if (source.superGeneric != null) {
                result = findTargetGenericTree(source.superGeneric, target);
            }
            if (result == null && source.interfacesGenerics != null && !source.interfacesGenerics.isEmpty()) {
                for (GenericTree gt : source.interfacesGenerics) {
                    result = findTargetGenericTree(gt, target);
                    if (result != null) {
                        break;
                    }
                }
            }
            return result;
        }

        @Override
        public String toString() {
            StringBuilder o = new StringBuilder();
            if (generics != null && !generics.isEmpty()) {
                o.append("<");
                for (Generic g : generics) {
                    o.append(g.type != null ? g.type.getTypeName() : g.name);
                    o.append(", ");
                }
                o.delete(o.length() - 2, o.length());
                o.append(">");
            }
            return type.getTypeName() + (o != null ? o.toString() : "");
        }

        private boolean isInterface() {
            return getJavaType() != null && getJavaType().isInterface();
        }

        public class Generic {

            private GenericTree owner;
            private Type type;
            private String name;
            private int index;

            private Generic(Type type, String name, int index) {
                this.type = type;
                this.name = name;
                this.index = index;
                this.owner = GenericTree.this;
            }

            private Generic(String name, int index) {
                this.name = name;
                this.index = index;
            }

            private boolean isResolved() {
                return type instanceof Class;
            }

            private void resolve(GenericTree genericTree) {
                Class<?> rawType = null;
                if (owner.isInterface()) {
                    // interface B<T>
                    // class A<T> implements B<T>
                    // 当前的GenericTree = B.class 当前的泛型属于接口, 通过注解对应的名称来解析
                    rawType = genericTree.getGenericType(name);
                } else {
                    // class B<T extends Serializable> implements A<T>
                    // class C extends B<String>
                    // 需要在generic tree 中A的泛型类型定位为String
                    rawType = genericTree.getGenericType(index);
                }
                if (rawType != null || this.type == null) {
                    this.type = rawType;
                }
            }

            public Type getType() {
                return type;
            }

            public Class<?> getJavaType() {
                return type instanceof Class ? (Class) type : null;
            }

            public String getName() {
                return name;
            }

            public int getIndex() {
                return index;
            }

        }
    }

}
