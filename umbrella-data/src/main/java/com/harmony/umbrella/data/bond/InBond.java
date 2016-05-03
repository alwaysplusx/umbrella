package com.harmony.umbrella.data.bond;

import static com.harmony.umbrella.data.Bond.Link.*;

import com.harmony.umbrella.data.Bond;
import com.harmony.umbrella.util.StringUtils;

/**
 * {@linkplain Link#IN}关系的{@linkplain Bond}
 * 
 * @author wuxii@foxmail.com
 */
public class InBond extends AbstractBond {

    private static final long serialVersionUID = 3954642916761007140L;

    private static final String IN_SQL_TEMPLATE = "%s %s (%s)";
    protected static final String IN_SQL_TEMPLATE_WITH_TABLE_ALIAS = "%s.%s %s (%s)";

    public InBond(String name, Object value) {
        super(name, value, IN);
    }

    InBond(String name, Object value, Link link) {
        super(name, value, link);
    }

    private InBond(String name, Object value, Link link, boolean inline, Class<?> domainClass) {
        super(name, value, link, inline, domainClass);
    }

    @Override
    public Bond not() {
        return new InBond(name, value, link.negated(), inline, domainClass);
    }

    @Override
    public String toSQL() {
        return String.format(IN_SQL_TEMPLATE, name, link.desc(), getSQLValue());
    }

    @Override
    public String toSQL(String tableAlias) {
        if (StringUtils.isBlank(tableAlias)) {
            return toSQL();
        }
        return String.format(IN_SQL_TEMPLATE_WITH_TABLE_ALIAS, tableAlias, name, link.desc(), getSQLValue());
    }

}
