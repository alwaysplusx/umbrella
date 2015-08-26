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
package com.harmony.umbrella.data.query;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.harmony.umbrella.data.EntityMetadata;
import com.harmony.umbrella.util.StringUtils;

/**
 * @author copy from spring
 */
public class DefaultEntityMetadata<T, ID extends Serializable> implements EntityMetadata<T, ID> {

    private final Class<T> domainClass;

    public DefaultEntityMetadata(Class<T> domainClass) {
        this.domainClass = domainClass;
    }

    @Override
    public String getEntityName() {
        Entity entity = domainClass.getAnnotation(Entity.class);
        boolean hasName = null != entity && StringUtils.isNotEmpty(entity.name());
        return hasName ? entity.name() : domainClass.getSimpleName();
    }

    @Override
    public String getTableName() {
        Table table = domainClass.getAnnotation(Table.class);
        boolean hasName = null != table && StringUtils.isNotEmpty(table.name());
        return hasName ? table.name() : getEntityName();
    }

    @Override
    public Class<T> getJavaType() {
        return domainClass;
    }

}
