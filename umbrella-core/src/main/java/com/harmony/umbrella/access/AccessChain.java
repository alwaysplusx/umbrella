package com.harmony.umbrella.access;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import com.harmony.umbrella.access.impl.ArrayAccess;
import com.harmony.umbrella.access.impl.ClassFieldAccess;
import com.harmony.umbrella.access.impl.ClassMethodAccess;
import com.harmony.umbrella.access.impl.ListAccess;
import com.harmony.umbrella.access.impl.MapAccess;
import com.harmony.umbrella.access.impl.ReflectionAccess;
import com.harmony.umbrella.util.Assert;

/**
 * @author wuxii@foxmail.com
 */
public class AccessChain {

    private static final Set<Access> ACCESSES = new HashSet<Access>();

    static {
        ACCESSES.add(new ArrayAccess());
        ACCESSES.add(new ClassFieldAccess());
        ACCESSES.add(new ClassMethodAccess());
        ACCESSES.add(new ListAccess());
        ACCESSES.add(new MapAccess());
        ACCESSES.add(new ReflectionAccess());
    }

    private List<Access> access;

    public AccessChain(List<Access> access) {
        this.access = access;
    }

    public static AccessChain createDefaultChain() {
        return createChain(defaultAccessors());
    }

    public static AccessChain createChain(List<Access> access) {
        return new AccessChain(access);
    }

    public static AccessChain createChain(Access... accessors) {
        return new AccessChain(Arrays.asList(accessors));
    }

    static List<Access> defaultAccessors() {
        return Arrays.asList(ACCESSES.toArray(new Access[ACCESSES.size()]));
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
        for (Access ac : access) {
            if (ac.isAccessible(token, target)) {
                ac.set(token, target, value);
                return;
            }
        }
        throw new IllegalArgumentException(target + " " + token + " not suitable accessor");
    }

    private Object getTokenValue(String token, Object target) {
        for (Access ac : access) {
            if (ac.isAccessible(token, target)) {
                return ac.get(token, target);
            }
        }
        throw new IllegalArgumentException(target + " " + token + " not suitable accessor");
    }

    public List<Access> getAccessors() {
        return access;
    }

    public void setAccessors(List<Access> access) {
        this.access = access;
    }

}
