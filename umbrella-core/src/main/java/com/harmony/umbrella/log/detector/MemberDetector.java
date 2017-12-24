package com.harmony.umbrella.log.detector;

/**
 * 
 * @author wuxii@foxmail.com
 */
public interface MemberDetector {

    boolean support(Class<?> type);

    Object get(String memberName, Object target);

}
