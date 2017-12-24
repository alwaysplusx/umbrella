package com.harmony.umbrella.log.detector;

import org.springframework.core.annotation.Order;

import com.harmony.umbrella.util.MemberUtils;

/**
 * @author wuxii@foxmail.com
 */
@Order(Integer.MIN_VALUE)
public class AnyMemberDetector implements MemberDetector {

    @Override
    public boolean support(Class<?> type) {
        return type != null;
    }

    @Override
    public Object get(String memberName, Object target) {
        try {
            return MemberUtils.getValue(memberName, target);
        } catch (Exception e) {
            throw new MemberDetectorException(memberName + " member not found", e);
        }
    }

}
