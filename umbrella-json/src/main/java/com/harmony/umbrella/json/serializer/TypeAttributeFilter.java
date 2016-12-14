package com.harmony.umbrella.json.serializer;

import java.util.HashSet;
import java.util.Set;

import com.harmony.umbrella.core.Member;
import com.harmony.umbrella.json.annotation.JsonGroup;

/**
 * @author wuxii@foxmail.com
 */
public class TypeAttributeFilter extends MemberFilterFilter {

    private Class<?> type;

    private FilterMode mode;

    private Set<Class> groups = new HashSet<Class>();

    public TypeAttributeFilter(Class<?> type, Class<?>... groups) {
        this(type, FilterMode.EXCLUDE, groups);
    }

    public TypeAttributeFilter(Class<?> type, FilterMode mode, Class<?>... groups) {
        this.type = type;
        this.mode = mode;

    }

    @Override
    protected boolean accept(Member member, Object target) {
        if (!type.isInstance(target)) {
            return true;
        }
        JsonGroup ann = member.getAnnotation(JsonGroup.class);
        if (ann == null) {
            return true;
        }
        // TODO 按组过滤
        return false;
    }

    public Set<Class> getGroups() {
        return groups;
    }

}
