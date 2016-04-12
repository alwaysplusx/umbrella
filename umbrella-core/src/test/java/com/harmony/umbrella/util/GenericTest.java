package com.harmony.umbrella.util;

import static org.junit.Assert.*;

import org.junit.Test;

import com.harmony.umbrella.context.ApplicationContext;

public class GenericTest {

    @Test
    public void testGetSuperGeneric() {
        assertEquals(ApplicationContext.class, GenericUtils.getSuperGeneric(C.class, 0));
    }

    @Test
    public void testGetInterfaceGeneric() {
        assertEquals(Integer.class, GenericUtils.getTargetGeneric(B.class, I.class, 0));
    }

    public static interface I<T> {

    }

    public static class A<E> implements I<E> {

    }

    public static class B implements I<Integer> {

    }

    public static class C extends A<ApplicationContext> {

    }

}
