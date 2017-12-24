package com.harmony.umbrella.log.detector;

import java.util.List;

/**
 * @author wuxii@foxmail.com
 */
public class ListMemberDetector implements MemberDetector {

    @Override
    public boolean support(Class<?> type) {
        return type != null && List.class.isAssignableFrom(type);
    }

    @Override
    public Object get(String memberName, Object target) {
        int index = -1;
        try {
            index = Integer.parseInt(memberName);
            return ((List) target).get(index);
        } catch (NumberFormatException e) {
            throw new MemberDetectorException(memberName + " member name not a number");
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new MemberDetectorException(index + " array object index out of bounds");
        }
    }

}
