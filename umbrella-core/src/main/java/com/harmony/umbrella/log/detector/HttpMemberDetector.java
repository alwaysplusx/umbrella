package com.harmony.umbrella.log.detector;

import com.harmony.umbrella.log.template.HttpMember;

/**
 * @author wuxii@foxmail.com
 */
public class HttpMemberDetector implements MemberDetector {

    @Override
    public boolean support(Class<?> type) {
        return type != null && HttpMember.class.isAssignableFrom(type);
    }

    @Override
    public Object get(String memberName, Object target) {
        return ((HttpMember) target).get(memberName);
    }

}
