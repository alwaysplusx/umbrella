package com.harmony.umbrella.data.bond;

import java.util.List;

import com.harmony.umbrella.data.Bond;

/**
 * 对内部的{@linkplain #getBonds()}通过{@linkplain Operator#OR}关联关系关联
 * 
 * @author wuxii@foxmail.com
 */
public class ConjunctionBond extends JunctionBond {

    private static final long serialVersionUID = 2831201212896620165L;

    public ConjunctionBond(List<Bond> bonds) {
        super(Operator.OR, bonds);
    }

    @Override
    public Bond not() {
        return new DisjunctionBond(bonds);
    }

}
