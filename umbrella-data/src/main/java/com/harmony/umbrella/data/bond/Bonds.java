package com.harmony.umbrella.data.bond;

import java.util.Arrays;

import com.harmony.umbrella.data.Bond;

/**
 * Bond辅助工具类
 * 
 * @author wuxii@foxmail.com
 */
public class Bonds {
    /**
     * 将传入的{@linkplain Bond}用{@linkplain Operator#AND}关联起来
     * 
     * @param bonds
     *            待关联的{@linkplain Bond}
     * @return and条件的Bond
     */
    public static JunctionBond and(Bond... bonds) {
        return new DisjunctionBond(Arrays.asList(bonds));
    }

    /**
     * 将传入的{@linkplain Bond}用{@linkplain Operator#OR}关联起来
     * 
     * @param bonds
     *            待关联的{@linkplain Bond}
     * @return or条件的Bond
     */
    public static JunctionBond or(Bond... bonds) {
        return new ConjunctionBond(Arrays.asList(bonds));
    }

}
