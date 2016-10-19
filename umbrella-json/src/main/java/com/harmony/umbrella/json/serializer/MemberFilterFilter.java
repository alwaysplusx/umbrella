package com.harmony.umbrella.json.serializer;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.PropertyPreFilter;
import com.harmony.umbrella.core.Member;
import com.harmony.umbrella.util.MemberUtils;

/**
 * @author wuxii@foxmail.com
 */
public abstract class MemberFilterFilter implements PropertyPreFilter {

    protected boolean allowNull;

    public MemberFilterFilter() {
    }

    public MemberFilterFilter(boolean allowNull) {
        this.allowNull = allowNull;
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
            return allowNull;
        }
        return accept(member, member);
    }

    protected Member getObjectMember(Class<?> targetClass, String name) {
        try {
            return MemberUtils.access(targetClass, name);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isAllowNull() {
        return allowNull;
    }

    public void setAllowNull(boolean allowNull) {
        this.allowNull = allowNull;
    }

}
