package com.harmony.umbrella.data.util;

import static java.util.regex.Pattern.*;
import static javax.persistence.metamodel.Attribute.PersistentAttributeType.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;
import javax.persistence.metamodel.Bindable;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.PluralAttribute;

import com.harmony.umbrella.data.domain.Sort;
import com.harmony.umbrella.data.domain.Sort.Order;
import com.harmony.umbrella.util.AnnotationUtils;
import com.harmony.umbrella.util.Assert;
import com.harmony.umbrella.util.StringUtils;

/**
 * 查询工具类
 * 
 * @author wuxii@foxmail.com
 */
public abstract class QueryUtils {

    private static final String DEFAULT_ALIAS = "x";

    private static final String IDENTIFIER = "[\\p{Alnum}._$]+";
    private static final String IDENTIFIER_GROUP = String.format("(%s)", IDENTIFIER);

    private static final String LEFT_JOIN = "left (outer )?join " + IDENTIFIER + " (as )?" + IDENTIFIER_GROUP;
    private static final Pattern LEFT_JOIN_PATTERN = Pattern.compile(LEFT_JOIN, Pattern.CASE_INSENSITIVE);

    private static final Pattern ORDER_BY = Pattern.compile(".*order\\s+by\\s+.*", CASE_INSENSITIVE);

    private static final int QUERY_JOIN_ALIAS_GROUP_INDEX = 3;

    private static final Map<PersistentAttributeType, Class<? extends Annotation>> ASSOCIATION_TYPES;

    static {
        Map<PersistentAttributeType, Class<? extends Annotation>> persistentAttributeTypes = new HashMap<PersistentAttributeType, Class<? extends Annotation>>();
        persistentAttributeTypes.put(ONE_TO_ONE, OneToOne.class);
        persistentAttributeTypes.put(ONE_TO_MANY, null);
        persistentAttributeTypes.put(MANY_TO_ONE, ManyToOne.class);
        persistentAttributeTypes.put(MANY_TO_MANY, null);
        persistentAttributeTypes.put(ELEMENT_COLLECTION, null);
        ASSOCIATION_TYPES = Collections.unmodifiableMap(persistentAttributeTypes);
    }

    /**
     * Private constructor to prevent instantiation.
     */
    private QueryUtils() {
    }

    /**
     * Adds {@literal order by} clause to the JPQL query. Uses the
     * {@link #DEFAULT_ALIAS} to bind the sorting property to.
     * 
     * @param query
     * @param sort
     * @return
     */
    public static String applySorting(String query, Sort sort) {
        return applySorting(query, sort, DEFAULT_ALIAS);
    }

    /**
     * Adds {@literal order by} clause to the JPQL query.
     * 
     * @param query
     * @param sort
     * @param alias
     * @return
     */
    public static String applySorting(String query, Sort sort, String alias) {
        Assert.hasText(query);
        if (null == sort || !sort.iterator().hasNext()) {
            return query;
        }
        StringBuilder builder = new StringBuilder(query);
        if (!ORDER_BY.matcher(query).matches()) {
            builder.append(" order by ");
        } else {
            builder.append(", ");
        }
        Set<String> aliases = getOuterJoinAliases(query);
        for (Order order : sort) {
            builder.append(getOrderClause(aliases, alias, order)).append(", ");
        }
        builder.delete(builder.length() - 2, builder.length());
        return builder.toString();
    }

    /**
     * Returns the order clause for the given {@link Order}. Will prefix the
     * clause with the given alias if the referenced property refers to a join
     * alias.
     * 
     * @param joinAliases
     *            the join aliases of the original query.
     * @param alias
     *            the alias for the root entity.
     * @param order
     *            the order object to build the clause for.
     * @return
     */
    private static String getOrderClause(Set<String> joinAliases, String alias, Order order) {
        String property = order.getProperty();
        boolean qualifyReference = !property.contains("("); // ( indicates a function
        for (String joinAlias : joinAliases) {
            if (property.startsWith(joinAlias)) {
                qualifyReference = false;
                break;
            }
        }
        String reference = qualifyReference ? String.format("%s.%s", alias, property) : property;
        String wrapped = order.isIgnoreCase() ? String.format("lower(%s)", reference) : reference;
        return String.format("%s %s", wrapped, toJpaDirection(order));
    }

    /**
     * Returns the aliases used for {@code left (outer) join}s.
     * 
     * @param query
     * @return
     */
    static Set<String> getOuterJoinAliases(String query) {
        Set<String> result = new HashSet<String>();
        Matcher matcher = LEFT_JOIN_PATTERN.matcher(query);
        while (matcher.find()) {
            String alias = matcher.group(QUERY_JOIN_ALIAS_GROUP_INDEX);
            if (StringUtils.hasText(alias)) {
                result.add(alias);
            }
        }
        return result;
    }

    private static String toJpaDirection(Order order) {
        return order.getDirection().name().toLowerCase(Locale.US);
    }

    @SuppressWarnings("unchecked")
    public static <T> Expression<T> toExpressionRecursively(From<?, ?> from, String name) {

        StringTokenizer token = new StringTokenizer(name, ".");
        Bindable<?> propertyPathModel = null;
        Expression<T> result = (Expression<T>) from;

        while (token.hasMoreTokens()) {
            Bindable<?> model = ((From<?, ?>) result).getModel();
            String currentName = token.nextToken();
            if (model instanceof ManagedType) {
                propertyPathModel = (Bindable<?>) ((ManagedType<?>) model).getAttribute(currentName);
            } else {
                propertyPathModel = (((From<?, ?>) result).get(currentName)).getModel();
            }
            if (requiresJoin(propertyPathModel, model instanceof PluralAttribute)) {
                result = (Expression<T>) getOrCreateJoin(from, currentName);
            } else {
                result = ((From<?, ?>) result).get(currentName);
            }
        }
        return result;
    }

    /**
     * Returns whether the given {@code propertyPathModel} requires the creation
     * of a join. This is the case if we find a non-optional association.
     * 
     * @param propertyPathModel
     *            must not be {@literal null}.
     * @param forPluralAttribute
     * @return
     */
    private static boolean requiresJoin(Bindable<?> propertyPathModel, boolean forPluralAttribute) {
        if (propertyPathModel == null && forPluralAttribute) {
            return true;
        }
        if (!(propertyPathModel instanceof Attribute)) {
            return false;
        }
        Attribute<?, ?> attribute = (Attribute<?, ?>) propertyPathModel;
        if (!ASSOCIATION_TYPES.containsKey(attribute.getPersistentAttributeType())) {
            return false;
        }
        Class<? extends Annotation> associationAnnotation = ASSOCIATION_TYPES.get(attribute.getPersistentAttributeType());
        if (associationAnnotation == null) {
            return true;
        }
        Member member = attribute.getJavaMember();
        if (!(member instanceof AnnotatedElement)) {
            return true;
        }
        Annotation annotation = null;
        AnnotatedElement annotatedElement = ((AnnotatedElement) member);
        try {
            annotation = annotatedElement.getAnnotation(associationAnnotation);
            if (annotation == null) {
                for (Annotation metaAnn : annotatedElement.getAnnotations()) {
                    annotation = metaAnn.annotationType().getAnnotation(associationAnnotation);
                    if (annotation != null) {
                        break;
                    }
                }
            }
        } catch (Exception ex) {
        }
        return annotation == null ? true : (Boolean) AnnotationUtils.getAnnotationValue(annotation, "optional");
    }

    /**
     * Returns an existing join for the given attribute if one already exists or
     * creates a new one if not.
     * 
     * @param from
     *            the {@link From} to get the current joins from.
     * @param attribute
     *            the {@link Attribute} to look for in the current joins.
     * @return will never be {@literal null}.
     */
    private static Join<?, ?> getOrCreateJoin(From<?, ?> from, String attribute) {
        for (Join<?, ?> join : from.getJoins()) {
            boolean sameName = join.getAttribute().getName().equals(attribute);
            if (sameName && join.getJoinType().equals(JoinType.LEFT)) {
                return join;
            }
        }
        return from.join(attribute, JoinType.LEFT);
    }

    public static List<javax.persistence.criteria.Order> toJpaOrders(Sort sort, Root<?> root, CriteriaBuilder cb) {
        List<javax.persistence.criteria.Order> result = new ArrayList<javax.persistence.criteria.Order>();
        for (Sort.Order order : sort) {
            Expression<?> expression = toExpressionRecursively(root, order.getProperty());
            result.add(order.isAscending() ? cb.asc(expression) : cb.desc(expression));
        }
        return result;
    }

}
