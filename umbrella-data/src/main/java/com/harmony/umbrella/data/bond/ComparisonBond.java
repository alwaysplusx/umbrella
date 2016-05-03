package com.harmony.umbrella.data.bond;

import com.harmony.umbrella.data.Bond;

/**
 * 可比较的{@linkplain Bond}. 一般通用型Bond, 可用于表示{@linkplain Link#EQUAL},
 * {@linkplain Link#GREATER_THAN}等
 * 
 * @author wuxii@foxmail.com
 */
public class ComparisonBond extends AbstractBond {

    private static final long serialVersionUID = 3853641833042801719L;

    public ComparisonBond(String name, Object value, Link link) {
        super(name, value, link);
    }

    ComparisonBond(String name, Object value, Link link, boolean inline) {
        super(name, value, link, inline, null);
    }

    private ComparisonBond(String name, Object value, Link link, boolean inline, Class<?> domainClass) {
        super(name, value, link, inline, domainClass);
    }

    @Override
    public Bond not() {
        return new ComparisonBond(name, value, link.negated(), inline, domainClass);
    }

}
