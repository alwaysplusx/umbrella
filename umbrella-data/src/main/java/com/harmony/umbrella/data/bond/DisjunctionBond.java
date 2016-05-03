package com.harmony.umbrella.data.bond;

import java.util.List;

import com.harmony.umbrella.data.Bond;
import com.harmony.umbrella.data.bond.JunctionBond.Operator;

/**
 * 对内部的{@linkplain #getBonds()}通过{@linkplain Operator#AND}关联关系关联
 * 
 * @author wuxii@foxmail.com
 */
public class DisjunctionBond extends JunctionBond {

    private static final long serialVersionUID = 3020761474290974410L;

    public DisjunctionBond(List<Bond> bonds) {
        super(Operator.AND, bonds);
    }

    @Override
    public Bond not() {
        return new ConjunctionBond(bonds);
    }

}
