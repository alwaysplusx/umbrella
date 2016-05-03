package com.harmony.umbrella;

import java.util.Map;

import com.harmony.umbrella.excel.cell.BooleanCellResolver;
import com.harmony.umbrella.util.GenericUtils;

/**
 * @author wuxii@foxmail.com
 */
@SuppressWarnings("all")
public class GenericsTest {

    public static void main(String[] args) {
        Map<Class, Class[]> generics = GenericUtils.getGenerics(BooleanCellResolver.class);
        for (Class[] cs : generics.values()) {
            for (Class c : cs) {
                System.out.println(c);
            }
        }
    }

}
