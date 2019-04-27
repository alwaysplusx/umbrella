package com.harmony.umbrella.util;

import static org.springframework.util.ReflectionUtils.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

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
public class MemberUtils {

    public static Member findMember(Class<?> clazz, String fieldName) {
        Field field = findField(clazz, fieldName);
        Method reader = findReadMethod(clazz, fieldName);
        Method writer = findWriterMethod(clazz, fieldName);
        Class<?> memberType = null;
        if (field != null) {
            memberType = field.getType();
        }
        if (memberType == null && reader != null) {
            memberType = reader.getReturnType();
        }
        if (memberType == null && writer != null) {
            memberType = writer.getParameterTypes()[0];
        }
        if (field == null && reader == null && writer == null) {
            throw new IllegalArgumentException(fieldName + " member not found in " + clazz);
        }
        return new MemberImpl(fieldName, clazz, memberType, field, reader, writer);
    }

    public static boolean isReadMethod(Method method) {
        String methodName = method.getName();
        return method.getParameterTypes().length == 0//
                && ((methodName.length() > 3 && methodName.startsWith("get")) //
                || (methodName.length() > 2 && methodName.startsWith("is")));
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
     * @param clazz     目标类
     * @param fieldName 字段名称
     * @return 字段对应的getter方法
     */
    public static Method findReadMethod(Class<?> clazz, String fieldName) {
        String[] readMethodName = readMethodNames(fieldName);
        for (String name : readMethodName) {
            Method method = ReflectionUtils.findMethod(clazz, name);
            if (method != null) {
                return method;
            }
        }
        return null;
    }

    /**
     * 通过field名称查找对应的setter方法
     *
     * @param clazz     目标类
     * @param fieldName 字段名称
     * @return 字段对应的setter方法
     * @throws NoSuchMethodException 如果未找到getter方法
     */
    public static Method findWriterMethod(Class<?> clazz, String fieldName) {
        return ReflectionUtils.findMethod(clazz, writerMethodName(fieldName), Object.class);
    }

    /**
     * 判断字段是否可读
     *
     * @param clazz root类
     * @param name  字段
     * @return true 可读, false不可读
     */
    public static boolean isReadable(Class<?> clazz, String name) {
        return ReflectionUtils.findField(clazz, name) != null || findReadMethod(clazz, name) != null;
    }

    /**
     * 判断字段是否可写
     *
     * @param clazz root类
     * @param name  字段
     * @return true可写, false不可写
     */
    public static boolean isWriteable(Class<?> clazz, String name) {
        return ReflectionUtils.findField(clazz, name) != null || findWriterMethod(clazz, name) != null;
    }

    /**
     * 强制获取target中对应的field的值
     *
     * @param field  需要获取的字段
     * @param target 目标对象
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
     * 获取val中对应属性名称为name的值, 优先通过getter方法来取值
     *
     * @param name 属性名称
     * @param val  目标对象
     * @return 属性值
     */
    public static Object getMemberValue(String name, Object val) {
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
     * 设置目标对象target的属性name的值, 优先通过setter方法来设值
     *
     * @param name   属性名称
     * @param target 目标对象
     * @param val    属性值
     */
    public static void setMemberValue(String name, Object target, Object val) {
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

    /**
     * 将字段名转为getter的方法名
     *
     * @param fieldName
     * @return
     */
    static String[] readMethodNames(String fieldName) {
        if (StringUtils.isBlank(fieldName)) {
            throw new IllegalArgumentException("field name is blank");
        }
        String name = Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
        return new String[]{"get" + name, "is" + name};
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

    private static final class MemberImpl implements Member {

        protected final String name;
        private Field field;
        private Class<?> ownerType;
        private Class<?> memberType;
        private Method readMethod;
        private Method writeMethod;

        private MemberImpl(String name, Class<?> targetType, Class<?> memberType, Field field, Method reader, Method writer) {
            this.name = name;
            this.ownerType = targetType;
            this.memberType = memberType;
            this.field = field;
            this.readMethod = reader;
            this.writeMethod = writer;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Class<?> getOwnerType() {
            return ownerType;
        }

        @Override
        public Class<?> getType() {
            return memberType;
        }

        @Override
        public Field getField() {
            return field;
        }

        @Override
        public Method getReadMethod() {
            return readMethod;
        }

        @Override
        public Method getWriteMethod() {
            return writeMethod;
        }

        @Override
        public boolean isReadable() {
            return field != null || readMethod != null;
        }

        @Override
        public boolean isWriteable() {
            return field != null || writeMethod != null;
        }

        @Override
        public <T extends Annotation> T getAnnotation(Class<T> annCls) {
            T result = null;
            if (readMethod != null) {
                result = readMethod.getAnnotation(annCls);
            }
            if (result == null) {
                result = field.getAnnotation(annCls);
            }
            return result;
        }

        @Override
        public Object get(Object obj) {
            if (!isReadable()) {
                throw new IllegalStateException(name + " member unreadable");
            }
            checkTargetType(obj);
            Object result = null;
            if (readMethod != null) {
                result = invokeMethod(readMethod, obj);
            } else if (field != null) {
                result = getFieldValue(field, obj);
            }
            return result;
        }

        @Override
        public void set(Object obj, Object val) {
            if (!isWriteable()) {
                throw new IllegalStateException(name + " member unwriteable");
            }
            checkTargetType(obj);
            if (writeMethod != null) {
                invokeMethod(writeMethod, obj, val);
            } else if (field != null) {
                setFieldValue(field, obj, val);
            }
        }

        @Override
        public Member createRelative(String name) {
            return findMember(ownerType, name);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            result = prime * result + ((ownerType == null) ? 0 : ownerType.hashCode());
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
            MemberImpl other = (MemberImpl) obj;
            if (name == null) {
                if (other.name != null)
                    return false;
            } else if (!name.equals(other.name))
                return false;
            if (ownerType == null) {
                if (other.ownerType != null)
                    return false;
            } else if (!ownerType.equals(other.ownerType))
                return false;
            return true;
        }

        private void checkTargetType(Object obj) {
            if (obj == null) {
                throw new IllegalArgumentException("target obj is null");
            }
            if (!ownerType.isAssignableFrom(obj.getClass())) {
                throw new IllegalArgumentException("target are not assignable from " + ownerType);
            }
        }

    }

}