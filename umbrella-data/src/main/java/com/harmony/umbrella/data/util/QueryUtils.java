package com.harmony.umbrella.data.util;

import static javax.persistence.metamodel.Attribute.PersistentAttributeType.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.util.ArrayList;
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

    public static <T> Specification<T> conjunction() {
        return new SignalSpecification<T>(true);
    }

    public static <T> Specification<T> disjunction() {
        return new SignalSpecification<T>(false);
    }

    public static boolean isPaging(int page, int size) {
        return !(page < 0 || size < 1);
    }

    public static boolean isFunctionExpression(String name) {
        return name.indexOf("(") > -1 && name.indexOf(")") > -1 && name.indexOf("(") > name.indexOf(")");
    }

    public static <T> Expression<T> functionExpression(String name, Root<?> root, CriteriaBuilder builder, Class<T> type) {
        StringTokenizer st = new StringTokenizer(name);
        String f = st.nextToken("(").trim();
        String n = st.nextToken(")").trim();
        return builder.function(f, type, toExpressionRecursively(root, n));
    }

    public static <T> Expression<T> toExpressionRecursively(final From<?, ?> from, String name) {
        return toExpressionRecursively(from, new StringTokenizer(name, "."));
    }

    public static List<javax.persistence.criteria.Order> toOrders(Sort sort, Root<?> root, CriteriaBuilder cb) {
        List<javax.persistence.criteria.Order> result = new ArrayList<javax.persistence.criteria.Order>();
        for (Sort.Order order : sort) {
            Expression<?> expression = toExpressionRecursively(root, order.getProperty());
            result.add(order.isAscending() ? cb.asc(expression) : cb.desc(expression));
        }
        return result;
    }

    private static <T> Expression<T> toExpressionRecursively(From<?, ?> from, StringTokenizer st) {
        Bindable<?> propertyPathModel = null;
        Bindable<?> model = from.getModel();
        String segment = st.nextToken();
        if (model instanceof ManagedType) {
            propertyPathModel = (Bindable<?>) ((ManagedType<?>) model).getAttribute(segment);
        } else {
            propertyPathModel = from.get(segment).getModel();
        }
        if (requiresJoin(propertyPathModel, model instanceof PluralAttribute) && !isAlreadyFetched(from, segment)) {
            Join<?, ?> join = getOrCreateJoin(from, segment);
            return (Expression<T>) (st.hasMoreTokens() ? toExpressionRecursively(join, st) : join);
        } else {
            Path result = from.get(segment);
            while (st.hasMoreTokens()) {
                result = result.get(st.nextToken());
            }
            return result;
        }
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
        return annotation == null ? true : (Boolean) AnnotationUtils.getValue(annotation, "optional");
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

    /**
     * Return whether the given {@link From} contains a fetch declaration for
     * the attribute with the given name.
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

        private boolean signal;

        private SignalSpecification(boolean signal) {
            this.signal = signal;
        }

        @Override
        public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
            return signal ? cb.conjunction() : cb.disjunction();
        }

    }
}
