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
package com.harmony.umbrella.data;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.harmony.umbrella.data.domain.Sort;

/**
 * {@linkplain Bond}解析工具
 * 
 * @author wuxii@foxmail.com
 */
public interface BondParser {

    /**
     * 将{@linkplain Sort}转为JPA {@linkplain Order}
     * 
     * @param sort
     *            排序
     * @param root
     *            {@linkplain Root}
     * @param cb
     *            {@linkplain CriteriaBuilder}
     * @return {@linkplain Order}
     */
    List<Order> toJpaOrders(Sort sort, Root<?> root, CriteriaBuilder cb);

    /**
     * 将{@linkplain Sort}转为order by 语句
     * 
     * @param sort
     *            排序
     * @return order by sql
     */
    String orderBy(Sort sort);

    /**
     * 将{@linkplain Sort}转为order by 语句
     * 
     * @param sort
     *            排序
     * @param alias
     *            表别名
     * @return order by sql
     */
    String orderBy(Sort sort, String alias);

    /**
     * 将{@linkplain Bond}解析为查询的SQL语句
     * 
     * @param tableName
     *            表名
     * @param bond
     *            bond条件
     * @return select sql
     */
    String toSQL(String tableName, Bond... bond);

    /**
     * 将{@linkplain Bond}解析为统计的SQL语句
     * 
     * @param tableName
     *            表名
     * @param bond
     *            bond条件
     * @return count sql
     */
    String toCountSQL(String tableName, Bond... bond);

    /**
     * 将{@linkplain Bond}解析为删除的SQL语句
     * 
     * @param tableName
     *            表名
     * @param bond
     *            bond条件
     * @return delete sql
     */
    String toDeleteSQL(String tableName, Bond... bond);

    /**
     * 将{@linkplain Bond}解析为{@linkplain QBond}
     * 
     * @param entityName
     *            entity名称
     * @param bond
     *            bond条件
     * @return {@linkplain QBond}
     */
    QBond toQBond(String entityName, Bond... bond);

    /**
     * {@linkplain Bond} to {@linkplain Predicate}JPA 2.0 查询条件转化
     * 
     * @param root
     *            {@linkplain Root}
     * @param cb
     *            {@linkplain CriteriaBuilder}
     * @param bond
     *            bond条件
     * @return {@linkplain Predicate}
     */
    Predicate toPredicate(Root<?> root, CriteriaBuilder cb, Bond... bond);

    /**
     * 带排序条件的{@linkplain Bond}SQL转化
     * 
     * @param tableName
     *            表名
     * @param sort
     *            排序条件
     * @param bond
     *            bond条件
     * @return select sql
     */
    String toSQL(String tableName, Sort sort, Bond... bond);

    /**
     * 带排序条件的{@linkplain Bond} -> {@linkplain QBond}转化
     * 
     * @param entityName
     *            entity名称
     * @param sort
     *            排序条件
     * @param bond
     *            bond条件
     * @return {@link QBond}
     */
    QBond toQBond(String entityName, Sort sort, Bond... bond);

}