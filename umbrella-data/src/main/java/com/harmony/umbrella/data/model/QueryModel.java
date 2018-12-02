package com.harmony.umbrella.data.model;

import com.harmony.umbrella.data.QueryException;
import org.springframework.core.annotation.AnnotationUtils;

import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Bindable;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.PluralAttribute;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import static javax.persistence.metamodel.Attribute.PersistentAttributeType.*;

/**
 * @author wuxii
 */
public class QueryModel {

    private final Root<?> root;
    private final CriteriaQuery<?> query;
    private final CriteriaBuilder criteriaBuilder;

    public QueryModel(Root<?> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        this.root = root;
        this.query = query;
        this.criteriaBuilder = criteriaBuilder;
    }

    public Root getRoot() {
        return root;
    }

    public CriteriaQuery<?> getQuery() {
        return query;
    }

    public CriteriaBuilder getCriteriaBuilder() {
        return criteriaBuilder;
    }

    /**
     * 获取表达式
     *
     * @param name
     * @return
     */
    public Expression get(String name) {
        return recursiveGet(name).getExpression();
    }

    /**
     * 获取函数的表达式模型
     *
     * @param function 函数名称
     * @param name     表达式
     * @return
     */
    public Expression get(String function, String name) {
        return recursiveGet(function, name).getExpression();
    }


    /**
     * 迭代获取函数表达式链
     *
     * @param function
     * @param name
     * @return
     */
    public FunctionExpressionModel recursiveGet(String function, String name) {
        ExpressionModel expModel = recursiveGet(name);
        return new FunctionExpressionModel(expModel, function, criteriaBuilder);
    }

    /**
     * 迭代获取表达式链
     *
     * @param path 表达式名称
     * @return 表达式模型
     */
    public ExpressionModel recursiveGet(String path) {
        StringTokenizer names = new StringTokenizer(path, ".");
        ExpressionModel current = new RootExpressionModel(root, criteriaBuilder);
        String segment = null;
        try {
            while (names.hasMoreTokens()) {
                segment = names.nextToken();
                current = current instanceof StringExpressionModel
                        ? ((StringExpressionModel) current).next(segment, null)
                        : current.next(segment);
            }
        } catch (Exception e) {
            throw new QueryException("failed found " + segment + " in path " + path, e);
        }
        return current;
    }

    private static final Map<Attribute.PersistentAttributeType, Class<? extends Annotation>> ASSOCIATION_TYPES;

    static {
        Map<Attribute.PersistentAttributeType, Class<? extends Annotation>> persistentAttributeTypes = new HashMap<Attribute.PersistentAttributeType, Class<? extends Annotation>>();
        persistentAttributeTypes.put(ONE_TO_ONE, OneToOne.class);
        persistentAttributeTypes.put(ONE_TO_MANY, null);
        persistentAttributeTypes.put(MANY_TO_ONE, ManyToOne.class);
        persistentAttributeTypes.put(MANY_TO_MANY, null);
        persistentAttributeTypes.put(ELEMENT_COLLECTION, null);
        ASSOCIATION_TYPES = Collections.unmodifiableMap(persistentAttributeTypes);
    }

    static boolean requiresJoin(From<?, ?> from, String segment) {
        Bindable<?> propertyPathModel = null;
        Bindable<?> model = from.getModel();

        if (model instanceof ManagedType) {
            propertyPathModel = (Bindable<?>) ((ManagedType<?>) model).getAttribute(segment);
        } else {
            propertyPathModel = from.get(segment).getModel();
        }

        if (propertyPathModel == null && model instanceof PluralAttribute) {
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

        Annotation annotation = AnnotationUtils.getAnnotation((AnnotatedElement) member, associationAnnotation);
        return annotation == null ? true : (Boolean) AnnotationUtils.getValue(annotation, "optional");
    }

}
