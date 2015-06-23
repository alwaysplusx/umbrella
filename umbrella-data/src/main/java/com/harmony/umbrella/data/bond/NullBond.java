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
