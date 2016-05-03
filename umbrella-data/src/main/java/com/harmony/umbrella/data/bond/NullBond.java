package com.harmony.umbrella.data.bond;

import static com.harmony.umbrella.data.Bond.Link.*;

import com.harmony.umbrella.data.Bond;
import com.harmony.umbrella.util.StringUtils;

/**
 * {@linkplain Link#NULL}条件的Bond
 * 
 * @author wuxii@foxmail.com
 */
public class NullBond extends AbstractBond {

    private static final long serialVersionUID = -7009203487387810846L;

    private static final String NULL_SQL_TEMPLATE = "%s %s";
    private static final String NULL_XQL_TEMPLATE = NULL_SQL_TEMPLATE;

    protected static final String NULL_SQL_TEMPLATE_WITH_TABLE_ALIAS = "%s.%s %s";

    protected static final String NULL_XQL_TEMPLATE_WITH_TABLE_ALIAS = NULL_SQL_TEMPLATE_WITH_TABLE_ALIAS;

    public NullBond(String name) {
        super(name, null, NULL);
    }

    NullBond(String name, Link link) {
        super(name, null, link);
    }

    private NullBond(String name, Link link, boolean inline, Class<?> domainClass) {
        super(name, null, link, inline, domainClass);
    }

    @Override
    public Bond not() {
        return new NullBond(name, link.negated(), inline, domainClass);
    }

    @Override
    public String toSQL() {
        return String.format(NULL_SQL_TEMPLATE, name, link.desc());
    }

    @Override
    public String toSQL(String tableAlias) {
        if (StringUtils.isBlank(tableAlias)) {
            return toSQL();
        }
        return String.format(NULL_SQL_TEMPLATE_WITH_TABLE_ALIAS, tableAlias, name, link.desc());
    }

    @Override
    public String toXQL(@Deprecated String nameAlias) {
        return String.format(NULL_XQL_TEMPLATE, name, link.desc());
    }

    @Override
    public String toXQL(String tableAlias, @Deprecated String nameAlias) {
        return String.format(NULL_XQL_TEMPLATE_WITH_TABLE_ALIAS, tableAlias, name, link.desc());
    }

}
