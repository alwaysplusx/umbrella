/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.harmony.umbrella.data.bond;

import static com.harmony.umbrella.data.bond.Bond.Link.*;

import com.harmony.umbrella.util.StringUtils;

/**
 * @author wuxii@foxmail.com
 */
public class InBond extends AbstractBond {

    private static final long serialVersionUID = 3954642916761007140L;

    private static final String IN_SQL_TEMPLATE = "%s %s (%s)";
    protected static final String IN_SQL_TEMPLATE_WITH_TABLE_ALIAS = "%s.%s %s (%s)";

    // private static final String XQL_TEMPLATE = "(%s %s (:%s))";
    // private static final String XQL_TEMPLATE_WITH_TABLE_ALIAS =
    // "(%s.%s %s (:%s))";

    public InBond(String name, Object value) {
        super(name, value, IN);
    }

    InBond(String name, Object value, Link link) {
        super(name, value, link);
    }

    private InBond(String name, Object value, Link link, boolean inline, Class<?> domainClass) {
        super(name, value, link);
        this.domainClass = domainClass;
        this.inline = inline;
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

    /*public String toXQL(String nameAlias) {
        return String.format(XQL_TEMPLATE, name, link.desc(), nameAlias);
    }

    @Override
    public String toXQL(String tableAlias, String nameAlias) {
        if (StringUtils.isBlank(tableAlias)) {
            return toXQL(nameAlias);
        }
        if (isInline()) {
            return toSQL(tableAlias);
        }
        if (StringUtils.isBlank(nameAlias)) {
            throw new IllegalArgumentException("name alias must not be null");
        }
        return String.format(XQL_TEMPLATE_WITH_TABLE_ALIAS, tableAlias, name, link.desc(), nameAlias);
    }*/

}
