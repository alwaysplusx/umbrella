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

import com.harmony.umbrella.data.query.SQLFormat;
import com.harmony.umbrella.util.Assert;
import com.harmony.umbrella.util.StringUtils;

/**
 * {@linkplain Bond}抽象
 * 
 * @author wuxii@foxmail.com
 */
public abstract class AbstractBond implements Bond {

    private static final long serialVersionUID = -3764032776378201348L;

    private static final String SQL_TEMPLATE_WITH_TABLE_ALIAS = "%s.%s %s %s";

    private static final String XQL_TEMPLATE_WITH_TABLE_ALIAS = "%s.%s %s :%s";

    private static final String SQL_TEMPLATE = "%s %s %s";

    private static final String XQL_TEMPLATE = "%s %s :%s";

    protected final String name;
    protected final Link link;

    protected Class<?> domainClass;
    protected Object value;
    protected boolean inline;

    public AbstractBond(String name, Object value, Link link) {
        Assert.notNull(link, "link must not be null");
        this.name = name;
        this.value = value;
        this.link = link;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public Link getLink() {
        return link;
    }

    @Override
    public boolean isInline() {
        return inline;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public void setInline(boolean inline) {
        this.inline = inline;
    }

    public Class<?> getDomainClass() {
        return domainClass;
    }

    public void setDomainClass(Class<?> domainClass) {
        if (this.domainClass == null) {
            this.domainClass = domainClass;
        }
    }

    /**
     * 返回value用sql值表示形式
     * 
     * @return sqlValue
     * @see SQLFormat
     */
    public String getSQLValue() {
        return isInline() ? getInlineExpression("") : SQLFormat.sqlValue(value);
    }

    @Override
    public String toSQL() {
        return String.format(SQL_TEMPLATE, name, link.desc(), getSQLValue());
    }

    @Override
    public String toSQL(String tableAlias) {
        if (StringUtils.isBlank(tableAlias)) {
            return toSQL();
        }
        return String.format(SQL_TEMPLATE_WITH_TABLE_ALIAS, tableAlias, name, link.desc(), isInline() ? getInlineExpression(tableAlias) : getSQLValue());
    }

    /**
     * sql如果是字段的内联关系的右边的字段名称
     * 
     * @param tableAlias
     *            表的别名
     * @return 内联右边字段值
     */
    public String getInlineExpression(String tableAlias) {
        if (StringUtils.isBlank(tableAlias)) {
            return (String) getValue();
        }
        return tableAlias.trim() + "." + getValue();
    }

    @Override
    public String toXQL(String nameAlias) {
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
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((domainClass == null) ? 0 : domainClass.hashCode());
        result = prime * result + ((link == null) ? 0 : link.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AbstractBond other = (AbstractBond) obj;
        if (domainClass == null) {
            if (other.domainClass != null)
                return false;
        } else if (!domainClass.equals(other.domainClass))
            return false;
        if (link == null) {
            if (other.link != null)
                return false;
        } else if (!link.equals(other.link))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return toSQL();
    }

}
