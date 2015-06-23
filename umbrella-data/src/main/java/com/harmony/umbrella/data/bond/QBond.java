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
 * @author wuxii@foxmail.com
 */
public class QBond {

    public static final String SELECT_QUERY_STRING = "select x from %s x";

    public static final String COUNT_QUERY_STRING = "select count(x) from %s x";

    public static final String DELETE_QUERY_STRING = "delete from %s x";

    private final String entityName;
    private final String query;
    private final Map<String, Object> params;

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

    private String whereClause(String query) {
        if (StringUtils.isBlank(query))
            return "";
        String lowerQuery = query.toLowerCase().trim();
        if (lowerQuery.startsWith("where ")) {
            return query.trim();
        }
        return "where " + query.trim();
    }

    public QBond(Class<?> domainClass, String query, Map<String, Object> params) {
        this(JpaUtils.getEntityName(domainClass), query, params);
    }

    public String getEntityName() {
        return entityName;
    }

    public String getQuery() {
        return String.format(SELECT_QUERY_STRING, entityName) + " " + query;
    }

    public String getDeleteQuery() {
        return String.format(DELETE_QUERY_STRING, entityName) + " " + query;
    }

    public String getCountQuery() {
        return String.format(COUNT_QUERY_STRING, entityName) + " " + query;
    }

    public Iterator<String> paramKeys() {
        return params.keySet().iterator();
    }

    public boolean hasWhereClause() {
        return StringUtils.isNotBlank(query);
    }

    public Object getValue(String key) {
        return params.get(key);
    }

    public Map<String, Object> getParams() {
        return params;
    }

}
