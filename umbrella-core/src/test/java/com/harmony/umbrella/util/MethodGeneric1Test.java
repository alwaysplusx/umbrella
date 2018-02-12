package com.harmony.umbrella.util;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.List;

import org.junit.Test;

import com.harmony.umbrella.util.GenericUtils.GenericTree;

/**
 * @author wuxii@foxmail.com
 */
public class MethodGeneric1Test {

    @Test
    public void testParse() throws Exception {
        Method method = A.class.getMethod("test", List.class, String.class);
        GenericTree[] trees = GenericUtils.parseMethod(method);
        for (GenericTree tree : trees) {
            System.out.println(tree);
        }
    }

    @Test
    public void testParse2() throws Exception {
        Method method = A.class.getMethod("test1", List.class);
        GenericTree[] trees = GenericUtils.parseMethod(method);
        for (GenericTree tree : trees) {
            System.out.println(tree);
        }
    }

    @Test
    public void testParse3() throws Exception {
        Method method = null;
        Method[] methods = D.class.getMethods();
        for (Method m : methods) {
            if (m.getName().equals("test")) {
                method = m;
                break;
            }
        }
        System.out.println(method);
        GenericTree[] trees = GenericUtils.parseMethod(method);
        for (GenericTree tree : trees) {
            System.out.println(tree);
        }
    }

    public static void main(String[] args) {
        // Method method = A.class.getMethod("test", List.class, String.class);
        // Type[] pTypes = method.getGenericParameterTypes();
        // for (Type type : pTypes) {
        // System.out.println(type);
        // }
        // // Type rType = method.getGenericReturnType();
        // // Type[] eTypes = method.getGenericExceptionTypes();
        // Field field = A.class.getField("list");
        // Type fType = field.getGenericType();
        // System.out.println(fType);
        BigDecimal decimal = new BigDecimal(Long.MAX_VALUE);
        decimal.add(BigDecimal.ONE);
        System.out.println(decimal);
    }

    public static class A {

        public List<List<A>> list;

        public void test(List<String> arg0, String arg1) {
        }

        public <T extends List<?>> T test1(T arg) {
            return null;
        }

    }

    public static interface B<T> {

        public void test(T a);

    }

    public abstract static class C<T> implements B<T> {
        @Override
        public void test(T a) {
        }
    }

    public abstract static class D extends C<String> {
    }

}
