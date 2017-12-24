package com.harmony.umbrella.log.detector;

import java.lang.reflect.Array;

/**
 * @author wuxii@foxmail.com
 */
public class ArrayMemberDetector implements MemberDetector {

    @Override
    public boolean support(Class<?> type) {
        return type != null && type.isArray();
    }

    @Override
    public Object get(String memberName, Object target) {
        int index = -1;
        try {
            index = Integer.parseInt(memberName);
            return Array.get(target, index);
        } catch (NumberFormatException e) {
            throw new MemberDetectorException(memberName + " member name not a number");
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new MemberDetectorException(index + " array object index out of bounds");
        }
    }

}
