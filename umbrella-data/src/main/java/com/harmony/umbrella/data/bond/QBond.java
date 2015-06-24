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

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import com.harmony.umbrella.data.query.SpecificationTransform.JpaUtils;
import com.harmony.umbrella.util.Assert;
import com.harmony.umbrella.util.StringUtils;

/**
 * 辅助查询工具类, 包含了查询的语句, 统计的语句, 删除的语句. 以及各个查询条件的where语句(where 语句为准备语句)
 * <p>
 * 使用时候通过{@linkplain QBond#getQuery()}获取查询条件并可以将查询的条件{@linkplain #getParams()}
 * 设置为查询的参数
 * 
 * @author wuxii@foxmail.com
 */
public class QBond {

    public static final String SELECT_QUERY_STRING = "select x from %s x";

    public static final String COUNT_QUERY_STRING = "select count(x) from %s x";

    public static final String DELETE_QUERY_STRING = "delete from %s x";

    private final String entityName;
    private final String query;
    private final Map<String, Object> params;

    public QBond(Class<?> domainClass, String query, Map<String, Object> params) {
        this(JpaUtils.getEntityName(domainClass), query, params);
    }

    public QBond(String entityName, String query, Map<String, Object> params) {
        Assert.notBlank(entityName, "entity name must not be null");
        this.entityName = entityName;
        this.query = whereClause(query);
        if (params != null && !params.isEmpty()) {
            this.params = Collections.unmodifiableMap(params);
        } else {
            this.params = Collections.emptyMap();
        }
    }

    /**
     * 传入的query条件语句加上where语句
     * 
     * @param query
     *            查询条件
     */
    private String whereClause(String query) {
        if (StringUtils.isBlank(query))
            return "";
        String lowerQuery = query.toLowerCase().trim();
        if (lowerQuery.startsWith("where ")) {
            return query.trim();
        }
        return "where " + query.trim();
    }

    public String getEntityName() {
        return entityName;
    }

    /**
     * 查询的HQL
     */
    public String getQuery() {
        return String.format(SELECT_QUERY_STRING, entityName) + " " + query;
    }

    /**
     * 统计的HQL
     */
    public String getDeleteQuery() {
        return String.format(DELETE_QUERY_STRING, entityName) + " " + query;
    }

    /**
     * 删除的HQL
     */
    public String getCountQuery() {
        return String.format(COUNT_QUERY_STRING, entityName) + " " + query;
    }

    /**
     * 查询的参数条件keys
     */
    public Iterator<String> paramKeys() {
        return params.keySet().iterator();
    }

    /**
     * 是否有查询条件
     */
    public boolean hasWhereClause() {
        return StringUtils.isNotBlank(query);
    }

    /**
     * 查询参数对应的值
     * 
     * @param key
     *            查询参数名称
     * @return 查询参数值
     */
    public Object getValue(String key) {
        return params.get(key);
    }

    /**
     * 查询参数Map
     * 
     * @return 参数Map
     */
    public Map<String, Object> getParams() {
        return params;
    }

}
