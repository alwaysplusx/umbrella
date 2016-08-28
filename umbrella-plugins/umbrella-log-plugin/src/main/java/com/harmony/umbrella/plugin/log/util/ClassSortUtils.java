package com.harmony.umbrella.plugin.log.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author wuxii@foxmail.com
 */
public class ClassSortUtils {

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static void sort(Class[] classes) {
        Arrays.sort(classes, new Comparator<Class>() {
            @Override
            public int compare(Class o1, Class o2) {
                return (!o1.isAssignableFrom(o2) && o1.equals(o2)) ? 0 : ((o1.isAssignableFrom(o2)) ? 1 : -1);
            }
        });
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static void sort(List<Class> classes) {
        Collections.sort(classes, new Comparator<Class>() {
            @Override
            public int compare(Class o1, Class o2) {
                return (!o1.isAssignableFrom(o2) && o1.equals(o2)) ? 0 : ((o1.isAssignableFrom(o2)) ? 1 : -1);
            }
        });
    }

}
