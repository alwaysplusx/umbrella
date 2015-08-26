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

import static com.harmony.umbrella.data.query.QueryUtils.*;
import static com.harmony.umbrella.data.query.SpecificationTransform.JpaUtils.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harmony.umbrella.data.Bond;
import com.harmony.umbrella.data.BondParser;
import com.harmony.umbrella.data.QBond;
import com.harmony.umbrella.data.Specification;
import com.harmony.umbrella.data.Bond.Link;
import com.harmony.umbrella.data.bond.Bonds;
import com.harmony.umbrella.data.bond.JunctionBond;
import com.harmony.umbrella.data.bond.JunctionBond.AliasGenerator;
import com.harmony.umbrella.data.domain.Sort;
import com.harmony.umbrella.data.sql.SQLFormat;
import com.harmony.umbrella.util.Assert;
import com.harmony.umbrella.util.MethodUtils;

/**
 * @author wuxii@foxmail.com
 */
public class SpecificationTransform implements BondParser {

    protected static final String DEFAULT_ALIAS = QueryUtils.DEFAULT_ALIAS;

    protected static final String SELECT_QUERY_STRING = "select %s from %s x";

    protected static final String COUNT_QUERY_STRING = "select count(%s) from %s x";

    protected static final String DELETE_QUERY_STRING = "delete from %s";

    private static SpecificationTransform INSTANCE;

    private SpecificationTransform() {
    }

    public static SpecificationTransform getInstance() {
        if (INSTANCE == null) {
            synchronized (SpecificationTransform.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SpecificationTransform();
                }
            }
        }
        return INSTANCE;
    }

    public String toSQL(Class<?> domainClass, Bond... bond) {
        return toSQL(getTableName(domainClass), bond);
    }

    public String toCountSQL(Class<?> domainClass, Bond... bond) {
        return toCountSQL(getTableName(domainClass), bond);
    }

    public String toDeleteSQL(Class<?> domainClass, Bond... bond) {
        return toDeleteSQL(getTableName(domainClass), bond);
    }

    @Override
    public String toSQL(String tableName, Bond... bond) {
        Assert.notBlank(tableName, "table name must not be blank");
        return toSQL(String.format(SELECT_QUERY_STRING, "*", tableName), DEFAULT_ALIAS, null, bond);
    }

    @Override
    public String toCountSQL(String tableName, Bond... bond) {
        Assert.notBlank(tableName, "table name must not be blank");
        return toSQL(String.format(COUNT_QUERY_STRING, "*", tableName), DEFAULT_ALIAS, null, bond);
    }

    @Override
    public String toDeleteSQL(String tableName, Bond... bond) {
        Assert.notBlank(tableName, "table name must not be blank");
        return toSQL(String.format(DELETE_QUERY_STRING, tableName), "", null, bond);
    }

    public QBond toQBond(Class<?> domainClass, Bond... bond) {
        return toQBond(getEntityName(domainClass), bond);
    }

    @Override
    public QBond toQBond(String entityName, Bond... bond) {
        Assert.notBlank(entityName, "entity name must not be blank");
        return toQBond(entityName, DEFAULT_ALIAS, null, bond);
    }

    @Override
    public Predicate toPredicate(Root<?> root, CriteriaBuilder cb, Bond... bond) {
        if (bond.length == 0) {
            return cb.conjunction();
        }
        Predicate predicate = toPredicate(root, cb, bond[0]);
        for (int i = 1, max = bond.length; i < max; i++) {
            predicate = cb.and(predicate, toPredicate(root, cb, bond[i]));
        }
        return predicate == null ? cb.conjunction() : predicate;
    }

    public static <T> Specification<T> toSpecification(Class<T> resultClass, final Bond... bond) {
        return toSpecification(resultClass, null, bond);
    }

    public static <T> Specification<T> toSpecification(Class<?> resultClass, final QueryProcessor processor, final Bond... bond) {
        return new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Predicate result = getInstance().toPredicate(root, cb, bond);
                if (processor != null) {
                    processor.process(query);
                }
                return result;
            }
        };
    }

    public static <T> Specification<T> toSpecification(QueryProcessor processor, final Bond... bond) {
        return toSpecification(Object.class, processor, bond);
    }

    public static <T> Specification<T> toSpecification(final Bond... bond) {
        return toSpecification(Object.class, null, bond);
    }

    @SuppressWarnings("rawtypes")
    private Predicate toPredicate(Root<?> root, CriteriaBuilder cb, Bond bond) {
        if (bond instanceof JunctionBond) {
            JunctionBond junction = ((JunctionBond) bond);
            Predicate[] predicates = toPredicate(root, cb, junction.getBonds());
            return junction.isConjunction() ? cb.or(predicates) : cb.and(predicates);
        }
        Expression expression = toExpressionRecursively(root, bond.getName());
        Object value = bond.getValue();
        if (bond.isInline()) {
            value = toExpressionRecursively(root, (String) value);
        }
        return toPredicate(root, cb, bond.getLink(), expression, value);
    }

    private Predicate[] toPredicate(Root<?> root, CriteriaBuilder cb, Iterable<Bond> bonds) {
        Iterator<Bond> iterator = bonds.iterator();
        if (!iterator.hasNext()) {
            return Arrays.asList(cb.conjunction()).toArray(new Predicate[1]);
        }
        List<Predicate> result = new ArrayList<Predicate>();
        while (iterator.hasNext()) {
            result.add(toPredicate(root, cb, iterator.next()));
        }
        return result.toArray(new Predicate[result.size()]);
    }

    @Override
    public List<Order> toJpaOrders(Sort sort, Root<?> root, CriteriaBuilder cb) {
        return QueryUtils.toJpaOrders(sort, root, cb);
    }

    @Override
    public String orderBy(Sort sort) {
        return applySorting("", sort);
    }

    @Override
    public String orderBy(Sort sort, String alias) {
        return applySorting("", sort, alias);
    }

    public String toSQL(Class<?> domainClass, Sort sort, Bond... bond) {
        return toSQL(JpaUtils.getTableName(domainClass), sort, bond);
    }

    @Override
    public String toSQL(String tableName, Sort sort, Bond... bond) {
        return toSQL(String.format(SELECT_QUERY_STRING, "*", tableName), DEFAULT_ALIAS, sort, bond);
    }

    public QBond toQBond(Class<?> domainClass, Sort sort, Bond... bond) {
        return toQBond(JpaUtils.getEntityName(domainClass), sort, bond);
    }

    @Override
    public QBond toQBond(String entityName, Sort sort, Bond... bond) {
        Assert.notBlank(entityName, "entity name must not be blank");
        return toQBond(entityName, DEFAULT_ALIAS, sort, bond);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Predicate toPredicate(Root<?> root, CriteriaBuilder cb, Link link, Expression x, Object y) {

        boolean vauleIsExpression = y instanceof Expression;

        switch (link) {

        case EQUAL:
            if (vauleIsExpression) {
                return cb.equal(x, (Expression) y);
            }
            return cb.equal(x, y);

        case NOT_EQUAL:
            if (vauleIsExpression) {
                return cb.notEqual(x, (Expression) y);
            }
            return cb.notEqual(x, y);

        case GREATER_THAN:
            if (vauleIsExpression) {
                return cb.greaterThan(x, (Expression) y);
            }
            if (y instanceof Comparable) {
                Comparable value = (Comparable) y;
                return cb.greaterThan(x, value);
            }
            break;

        case GREATER_THAN_OR_EQUAL:
            if (vauleIsExpression) {
                return cb.greaterThanOrEqualTo(x, (Expression) y);
            }
            if (y instanceof Comparable) {
                Comparable value = (Comparable) y;
                return cb.greaterThanOrEqualTo(x, value);
            }
            break;

        case LESS_THAN:
            if (vauleIsExpression) {
                return cb.lessThan(x, (Expression) y);
            }
            if (y instanceof Comparable) {
                Comparable value = (Comparable) y;
                return cb.lessThan(x, value);
            }
            break;

        case LESS_THAN_OR_EQUAL:
            if (vauleIsExpression) {
                return cb.lessThanOrEqualTo(x, (Expression) y);
            }
            if (y instanceof Comparable) {
                Comparable value = (Comparable) y;
                return cb.lessThanOrEqualTo(x, value);
            }
            break;

        case IN:
            if (vauleIsExpression) {
                return x.in((Expression) y);
            }
            if (y instanceof Collection) {
                return x.in((Collection) y);
            } else {
                return x.in(y);
            }

        case NOT_IN:
            if (vauleIsExpression) {
                return x.in((Expression) y).not();
            }
            if (y instanceof Collection) {
                return x.in((Collection) y).not();
            } else {
                return x.in(y).not();
            }

        case LIKE:
            if (vauleIsExpression) {
                return cb.like(x, (Expression) y);
            } else if (y instanceof String) {
                return cb.like(x, (String) y);
            }
            break;

        case NOT_LIKE:
            if (vauleIsExpression) {
                return cb.notLike(x, (Expression) y);
            }
            if (y instanceof String) {
                return cb.notLike(x, (String) y);
            }
            break;

        case NULL:
            return x.isNull();

        case NOT_NULL:
            return x.isNotNull();

        default:
            throw new IllegalArgumentException("unsupport link " + link);
        }
        throw new IllegalArgumentException("can't resolve bond [link=" + link + ", name=" + x + ", value=" + y + "]");
    }

    private String toSQL(String query, String tableAlias, Sort sort, Bond... bond) {
        StringBuilder buf = new StringBuilder(query);

        if (bond != null && bond.length > 0) {
            buf.append(" where ").append(Bonds.and(bond).toSQL(tableAlias));
        }
        String result = applySorting(buf.toString(), sort);
        logStatement("\nSQL:", result);
        return result;
    }

    private QBond toQBond(String entityName, String tableAlias, Sort sort, Bond... bond) {
        Map<String, Object> params = new HashMap<String, Object>();
        String query = applySorting(buildXQL(tableAlias, params, bond), sort);
        QBond qBond = new QBond(entityName, query, params);
        logStatement("\nHQL:", qBond.getQuery());
        return qBond;
    }

    private String buildXQL(String tableAlias, final Map<String, Object> params, Bond... bond) {
        if (bond == null || bond.length == 0)
            return "";

        return Bonds.and(bond).toXQL(tableAlias, new AliasGenerator() {
            Map<String, Integer> aliasIndexMap = new HashMap<String, Integer>();

            @Override
            public String generateAlias(Bond bond) {
                String name = bond.getName();
                if (name == null || bond.getLink() == Link.NULL || bond.getLink() == Link.NOT_NULL || bond.isInline())
                    return "";

                Integer index = aliasIndexMap.get(bond.getName());
                if (index == null) {
                    index = 0;
                }

                aliasIndexMap.put(name, index);

                String alias = name.replace(".", "_") + "_" + index++;
                params.put(alias, bond.getValue());

                return alias;
            }

        });
    }

    private static final Logger log = LoggerFactory.getLogger("com.harmony.umbrella.data.SQL");

    private void logStatement(String message, String sql) {
        if (log.isDebugEnabled()) {
            log.debug("{} {}", message, SQLFormat.format(sql));
        }
    }

    public static class JpaUtils {

        private static final Class<?>[] idClasses = new Class[] { Id.class, EmbeddedId.class };
        private static final Map<Class<?>, Method> idMethods = new HashMap<Class<?>, Method>();

        /**
         * 获取entity的id
         * 
         * @param object
         *            entity instance
         * @return id value
         */
        public static Object getEntityId(Object object) {
            if (!isEntity(object)) {
                throw new IllegalArgumentException("not entity object");
            }
            Method method = getIdMethod(object.getClass());
            try {
                return method.invoke(object);
            } catch (Exception e) {
                throw new IllegalStateException("can't access id property method");
            }
        }

        /**
         * 获取entity的名称
         * 
         * @param clazz
         *            entity类
         * @return entity名称
         */
        public static String getEntityName(Class<?> clazz) {
            if (!isEntityClass(clazz)) {
                throw new IllegalArgumentException("not entity class");
            }
            Entity ann = clazz.getAnnotation(Entity.class);
            if (!"".equals(ann.name())) {
                return ann.name();
            }
            return clazz.getSimpleName();
        }

        /**
         * class的table name获取顺序
         * <ul>
         * <li>{@linkplain Table#name()}</li>
         * <li>
         * {@linkplain Entity#name()}</li>
         * <li>{@linkplain Class#getSimpleName()}</li>
         * </ul>
         * 
         * @param clazz
         *            class
         * @return table name
         */
        public static String getTableName(Class<?> clazz) {
            Table ann = clazz.getAnnotation(Table.class);
            if (ann != null && !"".equals(ann.name())) {
                return ann.name();
            }
            if (isEntity(clazz)) {
                return getEntityName(clazz);
            }
            return getEntityName(clazz);
        }

        @SuppressWarnings({ "rawtypes", "unchecked" })
        private static Field idField(Class<?> clazz) {
            for (Field field : clazz.getDeclaredFields()) {
                for (Class c : idClasses) {
                    if (field.getAnnotation(c) != null) {
                        return field;
                    }
                }
            }
            return null;
        }

        @SuppressWarnings({ "rawtypes", "unchecked" })
        private static Method idMethod(Class<?> clazz) {
            for (Method method : clazz.getMethods()) {
                for (Class c : idClasses) {
                    if (method.getAnnotation(c) != null) {
                        return method;
                    }
                }
            }
            return null;
        }

        private static final Method getIdMethod(Class<?> entityClass) {
            if (idMethods.containsKey(entityClass)) {
                return idMethods.get(entityClass);
            }
            Method method = idMethod(entityClass);
            if (method != null) {
                idMethods.put(entityClass, method);
                return method;
            }
            Field field = idField(entityClass);
            if (field != null) {
                try {
                    method = MethodUtils.findReadMethod(entityClass, field);
                    idMethods.put(entityClass, method);
                    return method;
                } catch (Exception e) {
                    throw new IllegalArgumentException("no id property getter method");
                }
            }
            throw new IllegalArgumentException(entityClass + " is not a known entity.");
        }

        public static boolean isEntityClass(Class<?> claz) {
            return claz.getAnnotation(Entity.class) != null;
        }

        public static boolean isEntity(Object obj) {
            return isEntityClass(obj.getClass());
        }

    }

    /**
     * @author wuxii@foxmail.com
     */
    public interface QueryProcessor {

        void process(CriteriaQuery<?> query);

    }

}
