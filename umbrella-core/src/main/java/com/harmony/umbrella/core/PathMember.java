package com.harmony.umbrella.core;

import static com.harmony.umbrella.util.ReflectionUtils.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.StringTokenizer;

import com.harmony.umbrella.util.Assert;
import com.harmony.umbrella.util.ReflectionUtils;

/**
 * 带路径的getter/setter 通过路径指向最终的字段
 * 
 * @author wuxii@foxmail.com
 */
public class PathMember implements Member {

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
            type = MemberAccess.getTokenType(getPathClass(), memberName);
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
                tmpClass = MemberAccess.getTokenType(tmpClass, st.nextToken());
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