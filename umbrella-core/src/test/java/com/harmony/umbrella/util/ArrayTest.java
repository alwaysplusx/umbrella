package com.harmony.umbrella.util;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author wuxii@foxmail.com
 */
public class ArrayTest {

    @Test
    public void test() {
        assertTrue(Object[].class.isAssignableFrom(Integer[].class));
        assertFalse(Object[].class.isAssignableFrom(int[].class));

        //
        assertTrue(int.class.isPrimitive());
        assertFalse(int[].class.isPrimitive());
    }

    @Test(expected = ClassCastException.class)
    public void intCastToObjectArray() {
        int[] intArray = {};
        Object[].class.cast(intArray);
    }

    @Test
    public void otherCastToObjectArray() {
        Integer[] intArray = {};
        Object[].class.cast(intArray);
    }

}
