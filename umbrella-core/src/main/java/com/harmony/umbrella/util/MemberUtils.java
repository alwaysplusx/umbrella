package com.harmony.umbrella.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.StringTokenizer;

import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import com.harmony.umbrella.core.Member;

/**
 * 字段或getter/setter的获取工具类
 * <p>
 * 注:getter/setter优先
 * 
 * @author wuxii@foxmail.com
 */
public class MemberUtils extends ReflectionUtils {

    /**
     * 通过指定的字段名称或字段路径到达最终的字段member(该路径表达式使用{@code '.'}分割)
     * 
     * @param clazz
     *            root类
     * @param name
     *            字段的路径
     * @return member
     */
    public static Member accessMember(Class<?> clazz, String name) {
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
    public static Member accessMember(Field field) {
        return accessMember(field.getDeclaringClass(), field);
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
    public static Member accessMember(Class<?> clazz, Field field) {
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
    public static Member accessMember(Method readMethod) {
        return accessMember(readMethod.getDeclaringClass(), readMethod);
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
    public static Member accessMember(Class<?> clazz, Method readMethod) {
        if (!isReadMethod(readMethod)) {
            throw new IllegalArgumentException(readMethod + " not a getter method");
        }
        if (!contains(clazz, readMethod)) {
            throw new IllegalArgumentException(readMethod + " not in " + clazz);
        }
        return new PathMember(clazz, toFieldName(readMethod), readMethod);
    }

    public static boolean isReadMethod(Method method) {
        String methodName = method.getName();
        return method.getParameterTypes().length == 0
                && ((methodName.length() > 3 && methodName.startsWith("get")) || (methodName.length() > 2 && methodName.startsWith("is")));
    }

    public static boolean isWriteMethod(Method method) {
        String methodName = method.getName();
        return methodName.length() > 3//
                && methodName.startsWith("set") //
                && method.getReturnType() != void.class //
                && method.getParameterTypes().length == 1;
    }

    /**
     * 通过field名称查找对应的getter方法
     * 
     * @param source
     *            目标类
     * @param field
     *            字段名称
     * @return 字段对应的getter方法
     */
    public static Method findReadMethod(Class<?> source, String fieldName) {
        String[] readMethodName = readMethodName(fieldName);
        for (String name : readMethodName) {
            Method method = ReflectionUtils.findMethod(source, name);
            if (method != null) {
                return method;
            }
        }
        return null;
    }

    /**
     * 通过field名称查找对应的setter方法
     * 
     * @param source
     *            目标类
     * @param field
     *            字段名称
     * @return 字段对应的setter方法
     * @throws NoSuchMethodException
     *             如果未找到getter方法
     */
    public static Method findWriterMethod(Class<?> source, String fieldName) {
        return ReflectionUtils.findMethod(source, writerMethodName(fieldName), new Class[] { Object.class });
    }

    /**
     * 将字段名转为getter的方法名
     * 
     * @param fieldName
     * @return
     */
    static String[] readMethodName(String fieldName) {
        if (StringUtils.isBlank(fieldName)) {
            throw new IllegalArgumentException("field name is blank");
        }
        String name = Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
        return new String[] { "get" + name, "is" + name };
    }

    /**
     * 将字段名转为setter的方法名
     * 
     * @param fieldName
     * @return
     */
    static String writerMethodName(String fieldName) {
        if (StringUtils.isBlank(fieldName)) {
            throw new IllegalArgumentException("field name is blank");
        }
        return "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
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
        return ReflectionUtils.findField(clazz, name) != null || findReadMethod(clazz, name) != null;
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
        return ReflectionUtils.findField(clazz, name) != null || findWriterMethod(clazz, name) != null;
    }

    /**
     * 强制获取target中对应的field的值
     * 
     * @param field
     *            需要获取的字段
     * @param target
     *            目标对象
     * @return 字段值
     */
    public static Object getFieldValue(Field field, Object target) {
        makeAccessible(field);
        try {
            return field.get(target);
        } catch (IllegalAccessException ex) {
            handleReflectionException(ex);
            throw new IllegalStateException("Unexpected reflection exception - " + ex.getClass().getName() + ": " + ex.getMessage());
        }
    }

    public static void setFieldValue(Field field, Object target, Object val) {
        makeAccessible(field);
        try {
            field.set(target, val);
        } catch (IllegalAccessException ex) {
            handleReflectionException(ex);
            throw new IllegalStateException("Unexpected reflection exception - " + ex.getClass().getName() + ": " + ex.getMessage());
        }
    }

    /**
     * 获取val中对应属性名称为name的值
     * 
     * @param name
     *            属性名称
     * @param val
     *            目标对象
     * @return 属性值
     */
    public static Object getValue(String name, Object val) {
        Assert.notNull(val, "target not allow null");
        Assert.notNull(name, "property name not allow null");
        Class<?> targetClass = val.getClass();
        Method readMethod = findReadMethod(targetClass, name);
        if (readMethod != null) {
            return invokeMethod(readMethod, val);
        }

        Field field = findField(targetClass, name);
        if (field == null) {
            throw new IllegalArgumentException(name + " field not find");
        }
        return getFieldValue(field, val);
    }

    /**
     * 设置目标对象target的属性name的值
     * 
     * @param name
     *            属性名称
     * @param target
     *            目标对象
     * @param val
     *            属性值
     */
    public static void setValue(String name, Object target, Object val) {
        Assert.notNull(name, "fieldName not allow null");
        Assert.notNull(target, "target not allow null");
        Class<?> targetClass = val.getClass();
        Method writeMethod = findWriterMethod(targetClass, name);
        if (writeMethod != null) {
            invokeMethod(writeMethod, target, val);
            return;
        }
        Field field = ReflectionUtils.findField(targetClass, name);
        if (field == null) {
            throw new IllegalArgumentException(target + ", " + name + " field not find");
        }
        setFieldValue(field, target, val);
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
        Field f = ReflectionUtils.findField(clazz, token);
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
        return methodOwnerType.isAssignableFrom(clazz);
    }

    static boolean contains(Class<?> clazz, Field field) {
        Class<?> fieldOwnerType = field.getDeclaringClass();
        return fieldOwnerType.isAssignableFrom(clazz);
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
    private static final class PathMember implements Member {

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

        @Override
        public Class<?> getType() {
            if (type == null) {
                type = MemberUtils.getTokenType(getPathClass(), memberName);
            }
            return type;
        }

        @Override
        public Field getField() {
            if (field == null) {
                field = ReflectionUtils.findField(getPathClass(), memberName);
            }
            return field;
        }

        @Override
        public Method getReadMethod() {
            if (readMethod == null) {
                readMethod = findReadMethod(getPathClass(), memberName);
            }
            return readMethod;
        }

        @Override
        public Method getWriteMethod() {
            if (writeMethod == null) {
                writeMethod = findWriterMethod(getPathClass(), memberName);
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

        @Override
        public boolean isReadable() {
            return getField() != null || getReadMethod() != null;
        }

        @Override
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

        @Override
        public Object get(Object root) {
            Assert.isTrue(isReadable(), rootClass + " " + fullName + " unreadable");
            Object pathObject = getPathObject(root);
            if (pathObject == null) {
                throw new IllegalArgumentException("path object is null");
            }
            return getTokenValue(memberName, pathObject);
        }

        @Override
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
                Object tmp = getTokenValue(token, result);
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
            return getValue(token, target);
        }

        protected final void setTokenValue(String token, Object target, Object value) {
            setValue(token, target, value);
        }

    }
}