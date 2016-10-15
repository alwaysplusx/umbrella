package com.harmony.umbrella.data;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author wuxii@foxmail.com
 */
public class OperatorTest {

    public static void main(String[] args) {
        Object[] arr = new Object[] { "test", 11, new Object(), "domain", "wuxii", 123, 340 };
        List<Collection> result = Operator.cuttingBySize(Arrays.asList(arr), 2);
        for (Collection v : result) {
            System.out.println(v);
        }
    }

}
