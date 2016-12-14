package com.harmony.umbrella.util;

import static com.harmony.umbrella.util.ReflectionUtils.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.StringTokenizer;

import com.harmony.umbrella.core.Member;

/**
 * 字段或getter/setter的获取工具类
 * <p>
 * 注:getter/setter优先
 * 
 * @author wuxii@foxmail.com
 */
public class MemberUtils {

    /**
     * 通过指定的字段名称或字段路径到达最终的字段member(该路径表达式使用{@code '.'}分割)
     * 
     * @param clazz
     *            root类
     * @param name
     *            字段的路径
     * @return member
     */
    public static Member access(Class<?> clazz, String name) {
        int lastDotIndex = name.lastIndexOf(".");
        if (lastDotIndex < 0) {
            Field field = findField(clazz, name);
            if (field == null) {
                Method readMethod = findReadMethod(clazz, name);
                if (readMethod != null) {
                    return new PathMember(clazz, name, readMethod);
                }
                throw new IllegalArgumentException(clazz + " " + name + " not found");
            }
            return new PathMember(clazz, name, field);
        }
        String path = name.substring(0, lastDotIndex);
        String memberName = name.substring(lastDotIndex + 1);
        return new PathMember(clazz, path, memberName);
    }

    /**
     * 通过字段构建member
     * 
     * @param field
     *            字段
     * @return member
     */
    public static Member access(Field field) {
        return access(field.getDeclaringClass(), field);
    }

    /**
     * 通过字段与目标类构建member,field必须包含在clazz中(clazz可以是field声明类的子类)。
     * 
     * @param clazz
     *            包含该field的类
     * @param field
     *            最终接触的field
     * @return member
     */
    public static Member access(Class<?> clazz, Field field) {
        if (!contains(clazz, field)) {
            throw new IllegalArgumentException(field + " not in " + clazz);
        }
        return new PathMember(clazz, field.getName(), field);
    }

    /**
     * 通过get方法获取字段
     * 
     * @param readMethod
     *            get method
     * @return member
     * @see #access(Class, Method)
     */
    public static Member access(Method readMethod) {
        return access(readMethod.getDeclaringClass(), readMethod);
    }

    /**
     * 通过getter方法构建member,method不洗包含在clazz中(clazz可以是method声明类的子类)
     * 
     * @param clazz
     *            包含该readMethod的类
     * @param readMethod
     *            最终解除的readMethod
     * @return member
     */
    public static Member access(Class<?> clazz, Method readMethod) {
        if (!isReadMethod(readMethod)) {
            throw new IllegalArgumentException(readMethod + " not a getter method");
        }
        if (!contains(clazz, readMethod)) {
            throw new IllegalArgumentException(readMethod + " not in " + clazz);
        }
        return new PathMember(clazz, toFieldName(readMethod), readMethod);
    }

    /**
     * 判断字段是否可读
     * 
     * @param clazz
     *            root类
     * @param name
     *            字段
     * @return true 可读, false不可读
     */
    public static boolean isReadable(Class<?> clazz, String name) {
        return findField(clazz, name) != null || findReadMethod(clazz, name) != null;
    }

    /**
     * 判断字段是否可写
     * 
     * @param clazz
     *            root类
     * @param name
     *            字段
     * @return true可写, false不可写
     */
    public static boolean isWriteable(Class<?> clazz, String name) {
        return findField(clazz, name) != null || findWriterMethod(clazz, name) != null;
    }

    static Object get(String name, Object val) {
        return ReflectionUtils.getFieldValue(name, val);
    }

    static Class<?> getPathType(Class<?> clazz, String name) {
        StringTokenizer st = new StringTokenizer(name, ".");
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            Class<?> tmp = getTokenType(clazz, token);
            if (tmp == null) {
                throw new IllegalArgumentException(clazz + " no such member " + token);
            }
            clazz = tmp;
        }
        return clazz;
    }

    static Class<?> getTokenType(Class<?> clazz, String token) {
        // 通过token找字段
        Field f = findField(clazz, token);
        if (f != null) {
            // 使用field的类型设置接下去要查找的token
            return clazz = f.getType();
        }
        // 未找到通过getter方法查找
        Method m = findReadMethod(clazz, token);
        if (m != null) {
            return m.getReturnType();
        }
        // 通过setter方法查找
        m = findWriterMethod(clazz, token);
        if (m != null) {
            return m.getParameterTypes()[0];
        }
        return null;
    }

    static boolean contains(Class<?> clazz, Method method) {
        Class<?> methodOwnerType = method.getDeclaringClass();
        return ClassUtils.isAssignable(methodOwnerType, clazz);
    }

    static boolean contains(Class<?> clazz, Field field) {
        Class<?> fieldOwnerType = field.getDeclaringClass();
        return ClassUtils.isAssignable(fieldOwnerType, clazz);
    }

    static String toFieldName(Method method) {
        String name = method.getName().substring(3);
        return Character.toLowerCase(name.charAt(0)) + name.substring(4);
    }

    /**
     * 带路径的getter/setter 通过路径指向最终的字段
     * 
     * @author wuxii@foxmail.com
     */
    static final class PathMember implements Member {

        protected final Class<?> rootClass;
        protected final String fullName;

        // 指向member拥有者的path
        protected final String path;
        // 通过path找到的class
        protected Class<?> pathClass;

        // path指向的最终目标
        protected final String memberName;
        private Field field;
        private Class<?> type;
        private Method readMethod;
        private Method writeMethod;

        protected PathMember(Class<?> rootClass, String path, String memberName) {
            this(rootClass, path, memberName, null);
        }

        private PathMember(Class<?> rootClass, String path, String memberName, Class<?> pathClass) {
            this.rootClass = rootClass;
            this.path = path;
            this.fullName = path + "." + memberName;
            this.memberName = memberName;
            this.pathClass = pathClass;
        }

        PathMember(Class<?> rootClass, String memberName, Field field) {
            this.rootClass = rootClass;
            this.path = "";
            this.pathClass = rootClass;
            this.fullName = memberName;
            this.memberName = memberName;
            this.field = field;
            this.type = field.getType();
        }

        PathMember(Class<?> rootClass, String memberName, Method readMethod) {
            this.rootClass = rootClass;
            this.path = "";
            this.pathClass = rootClass;
            this.fullName = memberName;
            this.memberName = memberName;
            this.readMethod = readMethod;
            this.type = readMethod.getReturnType();
        }

        @Override
        public Class<?> getOwnerType() {
            return rootClass;
        }

        @Override
        public String getName() {
            return fullName;
        }

        public String getMemberName() {
            return memberName;
        }

        @Override
        public Class<?> getType() {
            if (type == null) {
                type = MemberUtils.getTokenType(getPathClass(), memberName);
            }
            return type;
        }

        public Field getField() {
            if (field == null) {
                field = ReflectionUtils.findField(getPathClass(), memberName);
            }
            return field;
        }

        public Method getReadMethod() {
            if (readMethod == null) {
                readMethod = ReflectionUtils.findReadMethod(getPathClass(), memberName);
            }
            return readMethod;
        }

        public Method getWriteMethod() {
            if (writeMethod == null) {
                writeMethod = ReflectionUtils.findWriterMethod(getPathClass(), memberName);
            }
            return writeMethod;
        }

        @Override
        public <T extends Annotation> T getAnnotation(Class<T> annCls) {
            Method method = getReadMethod();
            T ann = method != null ? method.getAnnotation(annCls) : null;
            if (ann == null) {
                Field field = getField();
                ann = field != null ? field.getAnnotation(annCls) : null;
            }
            return ann;
        }

        public boolean isReadable() {
            return getField() != null || getReadMethod() != null;
        }

        public boolean isWriteable() {
            return getField() != null || getWriteMethod() != null;
        }

        @Override
        public Member createRelative(String name) {
            if (memberName.equals(name)) {
                return this;
            }
            return new PathMember(rootClass, path, name, pathClass);
        }

        public Object get(Object root) {
            Assert.isTrue(isReadable(), rootClass + " " + fullName + " unreadable");
            Object pathObject = getPathObject(root);
            if (pathObject == null) {
                throw new IllegalArgumentException("path object is null");
            }
            return getTokenValue(memberName, pathObject);
        }

        public void set(Object obj, Object val) {
            Assert.isTrue(isWriteable(), rootClass + " " + fullName + " unwriteable");
            // 获取$.user.name中的user
            Object pathObject = getPathObject(obj);
            if (pathObject == null) {
                throw new IllegalArgumentException("path object is null");
            }
            setTokenValue(memberName, pathObject, val);
        }

        protected Object getPathObject(Object obj) {
            Object result = obj;
            StringTokenizer st = new StringTokenizer(path, ".");
            while (st.hasMoreTokens()) {
                String token = st.nextToken();
                Object tmp = getFieldValue(token, result);
                if (tmp == null && st.hasMoreTokens()) {
                    throw new IllegalArgumentException(result + " " + token + " got null value");
                }
                result = tmp;
            }
            return result;
        }

        protected Class<?> getPathClass() {
            if (pathClass == null) {
                Class<?> tmpClass = rootClass;
                StringTokenizer st = new StringTokenizer(path, ".");
                while (st.hasMoreTokens()) {
                    tmpClass = MemberUtils.getTokenType(tmpClass, st.nextToken());
                }
                pathClass = tmpClass;
            }
            return pathClass;
        }

        protected final Object getTokenValue(String token, Object target) {
            return getFieldValue(token, target);
        }

        protected final void setTokenValue(String token, Object target, Object value) {
            setFieldValue(token, target, value);
        }

    }
}