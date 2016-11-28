package com.harmony.umbrella.core;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.harmony.umbrella.core.annotation.Order;

public class OrderComparator implements Comparator<Object> {

    public static final OrderComparator INSTANCE = new OrderComparator();

    public static final int HIGHEST_PRECEDENCE = Integer.MIN_VALUE;

    public static final int LOWEST_PRECEDENCE = Integer.MAX_VALUE;

    protected OrderComparator() {
    }

    @Override
    public int compare(Object o1, Object o2) {
        Integer i1 = getOrder(o1);
        Integer i2 = getOrder(o2);
        return Integer.compare(i1 == null ? LOWEST_PRECEDENCE : i1, i2 == null ? LOWEST_PRECEDENCE : i2);
    }

    public static void sort(List<?> list) {
        if (list.size() > 1) {
            Collections.sort(list, INSTANCE);
        }
    }

    public static void sort(Object[] array) {
        if (array.length > 1) {
            Arrays.sort(array, INSTANCE);
        }
    }

    public static Integer getOrder(Object obj) {
        if (obj != null) {
            Class<? extends Object> cls = obj.getClass();
            Order ann = cls.getAnnotation(Order.class);
            return ann != null ? ann.value() : null;
        }
        return null;
    }

}