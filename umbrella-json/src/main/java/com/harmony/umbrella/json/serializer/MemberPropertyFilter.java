package com.harmony.umbrella.json.serializer;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.PropertyPreFilter;
import com.harmony.umbrella.core.Member;
import com.harmony.umbrella.util.MemberUtils;

/**
 * @author wuxii@foxmail.com
 */
public abstract class MemberPropertyFilter implements PropertyPreFilter {

    protected final boolean accepptNullMember;

    public MemberPropertyFilter(boolean accepptNullMember) {
        this.accepptNullMember = accepptNullMember;
    }

    protected abstract boolean accept(Member member, Object target);

    @Override
    public boolean apply(JSONSerializer serializer, Object object, String name) {
        if (object == null) {
            return false;
        }
        /* 
         * 1. 需要全部满足(只要一个不满足即不接受)
         * 2. 只需要满足一个
         */
        Class<?> objectClass = object.getClass();
        Member member = getObjectMember(objectClass, name);
        if (member == null) {
            return accepptNullMember;
        }
        return accept(member, member);
    }

    protected Member getObjectMember(Class<?> targetClass, String name) {
        try {
            return MemberUtils.accessMember(targetClass, name);
        } catch (Exception e) {
            return null;
        }
    }

}
