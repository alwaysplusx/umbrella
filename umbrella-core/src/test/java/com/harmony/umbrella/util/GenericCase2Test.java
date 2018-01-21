package com.harmony.umbrella.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.harmony.umbrella.util.GenericUtils.GenericTree;

/**
 * @author wuxii@foxmail.com
 */
public class GenericCase2Test {

    @Test
    public void test() {
        GenericTree tree = GenericUtils.parse(C.class);
        assertEquals(0, tree.getGenerics().length);
        assertEquals(String.class, tree.getSuperGeneric().getGeneric(0).getJavaType());
        assertEquals(String.class, tree.getSuperGeneric().getInterfaceGeneric(0).getGeneric(0).getJavaType());

        tree = GenericUtils.parse(B.class);
        assertEquals(1, tree.getGenerics().length);
        assertEquals("T", tree.getInterfaceGeneric(0).getGeneric(0).getName());
    }

    public static interface A<T> {
    }

    public static class B<T> implements A<T> {

    }

    public static class C extends B<String> {

    }

}
