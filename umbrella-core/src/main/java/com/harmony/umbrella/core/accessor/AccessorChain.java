package com.harmony.umbrella.core.accessor;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import com.harmony.umbrella.util.Assert;

/**
 * @author wuxii@foxmail.com
 */
public class AccessorChain {

    private static final Set<Accessor> ACCESSOR = new HashSet<Accessor>();

    static {
        ACCESSOR.add(ArrayAccessor.INSTANCE);
        ACCESSOR.add(ClassFieldAccessor.INSTANCE);
        ACCESSOR.add(ClassMethodAccessor.INSTANCE);
        ACCESSOR.add(ListAccessor.INSTANCE);
        ACCESSOR.add(MapAccessor.INSTANCE);
        ACCESSOR.add(ReflectionAccessor.INSTANCE);
    }

    public Object get(String path, Object target) {
        Object result = target;
        StringTokenizer st = new StringTokenizer(path, ".[]");
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            Object tmp = getTokenValue(token, result);
            if (tmp == null && st.hasMoreTokens()) {
                throw new IllegalStateException("got null value from " + result + " " + token);
            }
            result = tmp;
        }
        return result;
    }

    public void set(String path, Object target, Object value) {
        Assert.notBlank(path, "path is blank");
        int dotIndex = path.lastIndexOf(".");
        String token;
        if (dotIndex > 0) {
            target = get(path, target);
            token = path.substring(dotIndex + 1, path.length());
        } else {
            token = path;
        }
        setTokenValue(token, target, value);
    }

    private void setTokenValue(String token, Object target, Object value) {
        for (Accessor ac : ACCESSOR) {
            if (ac.isAccessible(token, target)) {
                ac.set(token, target, value);
                return;
            }
        }
        throw new IllegalArgumentException(target + " " + token + " not suitable accessor");
    }

    private Object getTokenValue(String token, Object target) {
        for (Accessor ac : ACCESSOR) {
            if (ac.isAccessible(token, target)) {
                return ac.get(token, target);
            }
        }
        throw new IllegalArgumentException(target + " " + token + " not suitable accessor");
    }

}
