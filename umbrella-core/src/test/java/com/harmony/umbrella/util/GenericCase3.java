package com.harmony.umbrella.util;

import static org.junit.Assert.assertEquals;

import java.io.Serializable;
import java.util.Date;

import org.junit.Test;

import com.harmony.umbrella.util.GenericUtils.GenericTree;

/**
 * @author wuxii@foxmail.com
 */
public class GenericCase3 {

    @Test
    public void test() {
        GenericTree tree = GenericUtils.parse(D.class);
        assertEquals(0, tree.getGenerics().length);
        // C<Integer, Date>
        assertEquals(Integer.class, tree.getSuperGeneric().getGeneric(0).getJavaType());
        assertEquals(Date.class, tree.getSuperGeneric().getGeneric(1).getJavaType());
        // B<Date>
        assertEquals(Date.class, tree.getSuperGeneric().getInterfaceGeneric(0).getGeneric(0).getJavaType());
        // A<String>
        assertEquals(String.class, tree.getSuperGeneric().getInterfaceGeneric(0).getInterfaceGeneric(0).getGeneric(0).getJavaType());

    }

    @Test
    public void test1() {
        GenericTree tree = GenericUtils.parse(D.class);

        assertEquals(Date.class, tree.getTargetGeneric(B.class, 0).getJavaType());
        assertEquals(String.class, tree.getTargetGeneric(A.class, 0).getJavaType());

        assertEquals(Integer.class, tree.getTargetGeneric(C.class, 0).getJavaType());
        assertEquals(Date.class, tree.getTargetGeneric(C.class, 1).getJavaType());
    }

    public static interface A<T> {
    }

    public static interface B<T> extends A<String> {
    }

    public static class C<T, X extends Serializable> implements B<X> {
    }

    public static class D extends C<Integer, Date> {
    }
}
