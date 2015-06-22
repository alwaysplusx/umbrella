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

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.harmony.umbrella.data.domain.Sort;

/**
 * @author wuxii@foxmail.com
 */
public interface BondParser {

    List<Order> toJpaOrders(Sort sort, Root<?> root, CriteriaBuilder cb);

    String orderBy(Sort sort);

    String orderBy(Sort sort, String alias);

    String toSQL(String tableName, Bond... bond);

    String toCountSQL(String tableName, Bond... bond);

    String toDeleteSQL(String tableName, Bond... bond);

    QBond toXQL(String entityName, Bond... bond);

    QBond toCountXQL(String entityName, Bond... bond);

    QBond toDeleteXQL(String entityName, Bond... bond);

    String toSQL(String tableName, Sort sort, Bond... bond);

    QBond toXQL(String entityName, Sort sort, Bond... bond);

    Predicate toPredicate(Root<?> root, CriteriaBuilder cb, Bond... bond);

}