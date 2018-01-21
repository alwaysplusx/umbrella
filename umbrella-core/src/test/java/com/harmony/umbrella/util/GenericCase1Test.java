package com.harmony.umbrella.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.harmony.umbrella.util.GenericUtils.GenericTree;

/**
 * @author wuxii@foxmail.com
 */
public class GenericCase1Test {

    @Test
    public void test() {
        GenericTree tree = GenericUtils.parse(C.class);
        assertEquals(0, tree.getGenerics().length);
        assertEquals(String.class, tree.getSuperGeneric().getGeneric(0).getJavaType());
        assertEquals(String.class, tree.getSuperGeneric().getSuperGeneric().getGeneric(0).getJavaType());

        tree = GenericUtils.parse(B.class);
        assertEquals(1, tree.getGenerics().length);
        assertEquals("T", tree.getGeneric(0).getName());
        assertEquals("T", tree.getSuperGeneric().getGeneric(0).getName());
    }

    public static class A<T> {

    }

    public static class B<T> extends A<T> {

    }

    public static class C extends B<String> {

    }

}
