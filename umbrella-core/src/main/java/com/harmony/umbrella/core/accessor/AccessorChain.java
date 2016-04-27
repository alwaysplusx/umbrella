package com.harmony.umbrella.core.accessor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import com.harmony.umbrella.util.Assert;

/**
 * @author wuxii@foxmail.com
 */
public class AccessorChain {

    private static final Set<Accessor> ACCESSORS = new HashSet<Accessor>();

    static {
        ACCESSORS.add(ArrayAccessor.INSTANCE);
        ACCESSORS.add(ClassFieldAccessor.INSTANCE);
        ACCESSORS.add(ClassMethodAccessor.INSTANCE);
        ACCESSORS.add(ListAccessor.INSTANCE);
        ACCESSORS.add(MapAccessor.INSTANCE);
        ACCESSORS.add(ReflectionAccessor.INSTANCE);
    }

    private List<Accessor> accessors;

    public AccessorChain(List<Accessor> accessor) {
        this.accessors = accessor;
    }

    public static AccessorChain createDefaultChain() {
        return createChain(defaultAccessors());
    }

    public static AccessorChain createChain(List<Accessor> accessors) {
        return new AccessorChain(accessors);
    }

    private static List<Accessor> defaultAccessors() {
        return Arrays.asList(ACCESSORS.toArray(new Accessor[ACCESSORS.size()]));
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
        for (Accessor ac : accessors) {
            if (ac.isAccessible(token, target)) {
                ac.set(token, target, value);
                return;
            }
        }
        throw new IllegalArgumentException(target + " " + token + " not suitable accessor");
    }

    private Object getTokenValue(String token, Object target) {
        for (Accessor ac : accessors) {
            if (ac.isAccessible(token, target)) {
                return ac.get(token, target);
            }
        }
        throw new IllegalArgumentException(target + " " + token + " not suitable accessor");
    }

    public List<Accessor> getAccessors() {
        return accessors;
    }

    public void setAccessors(List<Accessor> accessors) {
        this.accessors = accessors;
    }

}
