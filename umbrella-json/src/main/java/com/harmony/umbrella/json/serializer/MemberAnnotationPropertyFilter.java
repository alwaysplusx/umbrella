package com.harmony.umbrella.json.serializer;

import java.lang.annotation.Annotation;
import java.util.Set;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.PropertyPreFilter;
import com.harmony.umbrella.core.Member;
import com.harmony.umbrella.util.FilterMode;
import com.harmony.umbrella.util.MemberUtils;

/**
 * @author wuxii@foxmail.com
 */
public class MemberAnnotationPropertyFilter extends FilterMode<Member, Class<? extends Annotation>> implements PropertyPreFilter {

    @Override
    public boolean apply(JSONSerializer serializer, Object object, String name) {
        if (object == null) {
            return false;
        }
        Class<? extends Object> objectClass = object.getClass();
        Member member = getObjectMember(objectClass, name);
        return member != null && accept(member);
    }

    @Override
    protected boolean isMatched(Member resource, Set<Class<? extends Annotation>> patterns) {
        for (Class<? extends Annotation> annCls : patterns) {
            if (resource.getAnnotation(annCls) != null) {
                return true;
            }
        }
        return false;
    }

    protected Member getObjectMember(Class<?> targetClass, String name) {
        try {
            return MemberUtils.findMember(targetClass, name);
        } catch (Exception e) {
            return null;
        }
    }

}
