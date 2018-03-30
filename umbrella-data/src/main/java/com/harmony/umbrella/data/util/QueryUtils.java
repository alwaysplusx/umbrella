package com.harmony.umbrella.data.util;

import static javax.persistence.metamodel.Attribute.PersistentAttributeType.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;
import javax.persistence.metamodel.Bindable;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.PluralAttribute;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.Assert;

import com.harmony.umbrella.data.query.QueryResult.Selections;

/**
 * 查询工具类
 * 
 * @author wuxii@foxmail.com
 */
public abstract class QueryUtils {

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

    public static <T> Selections<T> select(Collection<String> names) {
        return new ColumnSelections(names);
    }

    public static <T> Selections<T> select(String... names) {
        return new ColumnSelections(names);
    }

    public static <T> Selections<T> select(String function, String column) {
        return new FunctionSelections(function, column);
    }

    public static <T> Selections<T> count(boolean distinct) {
        return new CountSelections(null, distinct);
    }

    public static <T> Selections<T> count(String name, boolean distinct) {
        return new CountSelections(name, distinct);
    }

    public static <T> Specification<T> conjunction() {
        return new SignalSpecification<T>(true);
    }

    public static <T> Specification<T> disjunction() {
        return new SignalSpecification<T>(false);
    }

    public static boolean isFunctionExpression(String name) {
        return name.indexOf("(") > -1 && name.indexOf(")") > -1 && name.indexOf("(") < name.indexOf(")");
    }

    public static <T> Expression<T> functionExpression(String name, Root<?> root, CriteriaBuilder builder, Class<T> type) {
        return functionExpression(name, root, builder, type, true);
    }

    public static <T> Expression<T> functionExpression(String name, Root<?> root, CriteriaBuilder builder, Class<T> type, boolean autoJoin) {
        StringTokenizer st = new StringTokenizer(name);
        String f = st.nextToken("(").trim();
        String n = st.nextToken(")").substring(1).trim();
        return builder.function(f, type, toExpressionRecursively(root, n));
    }

    public static <T> Expression<T> toExpressionRecursively(final From<?, ?> from, String name) {
        return toExpressionRecursively(from, name, true);
    }

    public static <T> Expression<T> toExpressionRecursively(final From<?, ?> from, String name, boolean autoJoin) {
        return toExpressionRecursively(from, new StringTokenizer(name, "."), autoJoin);
    }

    public static List<javax.persistence.criteria.Order> toOrders(Sort sort, Root<?> root, CriteriaBuilder cb) {
        List<javax.persistence.criteria.Order> result = new ArrayList<javax.persistence.criteria.Order>();
        for (Sort.Order order : sort) {
            Expression<?> expression = toExpressionRecursively(root, order.getProperty());
            result.add(order.isAscending() ? cb.asc(expression) : cb.desc(expression));
        }
        return result;
    }

    public static Expression<?> parseExpression(String name, Root<?> root, CriteriaBuilder cb) {
        return parseExpression(name, root, cb, true);
    }

    public static Expression<?> parseExpression(String name, Root<?> root, CriteriaBuilder cb, boolean autoJoin) {
        return isFunctionExpression(name) ? functionExpression(name, root, cb, null, autoJoin) : toExpressionRecursively(root, name, autoJoin);
    }

    public static <T> Specification<T> all(Specification<T>... specs) {
        Assert.notEmpty(specs, "spec not allow empty");
        return new Specification<T>() {

            private static final long serialVersionUID = -8700405582932703459L;

            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Predicate predicate = null;
                for (Specification<T> spec : specs) {
                    Predicate p = spec.toPredicate(root, query, cb);
                    if (p != null) {
                        predicate = predicate == null ? p : cb.and(predicate, p);
                    }
                }
                return predicate;
            }
        };
    }

    public static boolean hasRestriction(CriteriaQuery<?> query) {
        return query.getRestriction() != null;
    }

    private static <T> Expression<T> toExpressionRecursively(From<?, ?> from, StringTokenizer st, boolean autoJoin) {
        Bindable<?> propertyPathModel = null;
        Bindable<?> model = from.getModel();
        String segment = st.nextToken();
        if (model instanceof ManagedType) {
            propertyPathModel = (Bindable<?>) ((ManagedType<?>) model).getAttribute(segment);
        } else {
            propertyPathModel = from.get(segment).getModel();
        }
        if (autoJoin && requiresJoin(propertyPathModel, model instanceof PluralAttribute) && !isAlreadyFetched(from, segment)) {
            Join<?, ?> join = getOrCreateJoin(from, segment);
            return (Expression<T>) (st.hasMoreTokens() ? toExpressionRecursively(join, st, autoJoin) : join);
        } else {
            Path result = from.get(segment);
            while (st.hasMoreTokens()) {
                result = result.get(st.nextToken());
            }
            return result;
        }

    }

    /**
     * Returns whether the given {@code propertyPathModel} requires the creation of a join. This is the case if we find
     * a non-optional association.
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
        return annotation == null ? true : (Boolean) AnnotationUtils.getValue(annotation, "optional");
    }

    /**
     * Returns an existing join for the given attribute if one already exists or creates a new one if not.
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

    /**
     * Return whether the given {@link From} contains a fetch declaration for the attribute with the given name.
     * 
     * @param from
     *            the {@link From} to check for fetches.
     * @param attribute
     *            the attribute name to check.
     * @return
     */
    private static boolean isAlreadyFetched(From<?, ?> from, String attribute) {
        for (Fetch<?, ?> f : from.getFetches()) {
            boolean sameName = f.getAttribute().getName().equals(attribute);
            if (sameName && f.getJoinType().equals(JoinType.LEFT)) {
                return true;
            }
        }
        return false;
    }

    private static final class SignalSpecification<T> implements Specification<T> {

        private static final long serialVersionUID = 2258226830302979842L;
        private boolean signal;

        private SignalSpecification(boolean signal) {
            this.signal = signal;
        }

        @Override
        public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
            return signal ? cb.conjunction() : cb.disjunction();
        }

    }

    private static final class ColumnSelections<T> implements Selections<T> {

        private final List<String> columns;

        public ColumnSelections(Collection<String> columns) {
            this.columns = Collections.unmodifiableList(new ArrayList<>(columns));
        }

        public ColumnSelections(String... columns) {
            this.columns = Arrays.asList(columns);
        }

        @Override
        public List<Expression<?>> selection(Root<T> root, CriteriaBuilder cb) {
            List<Expression<?>> cs = new ArrayList<>();
            for (String c : columns) {
                cs.add(QueryUtils.parseExpression(c, root, cb));
            }
            return cs;
        }

    }

    private static final class FunctionSelections<T> implements Selections<T> {

        private final String function;
        private final String column;

        public FunctionSelections(String function, String column) {
            this.function = function;
            this.column = column;
        }

        @Override
        public List<Expression<?>> selection(Root<T> root, CriteriaBuilder cb) {
            List<Expression<?>> result = new ArrayList<>();
            result.add(cb.function(function, null, QueryUtils.toExpressionRecursively(root, column)));
            return result;
        }

    }

    private static final class CountSelections<T> implements Selections<T> {

        private final String column;
        private final boolean distinct;

        public CountSelections(String column, boolean distinct) {
            this.column = column;
            this.distinct = distinct;
        }

        @Override
        public List<Expression<?>> selection(Root<T> root, CriteriaBuilder cb) {
            Expression exp = column == null ? root : toExpressionRecursively(root, column);
            return new ArrayList<>(Arrays.asList(distinct ? cb.countDistinct(exp) : cb.count(exp)));
        }

    }
}
