package com.harmony.umbrella.log.detector;

import java.util.Map;

/**
 * @author wuxii@foxmail.com
 */
public class MapMemberDetector implements MemberDetector {

    @Override
    public boolean support(Class<?> type) {
        return type != null && Map.class.isAssignableFrom(type);
    }

    @Override
    public Object get(String memberName, Object target) {
        return ((Map) target).get(memberName);
    }

}
