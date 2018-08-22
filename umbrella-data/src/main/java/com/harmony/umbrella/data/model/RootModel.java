package com.harmony.umbrella.data.model;

import com.harmony.umbrella.data.util.QueryUtils;
import com.harmony.umbrella.data.util.StringExpression;
import org.springframework.core.annotation.AnnotationUtils;

import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Root;
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
public class RootModel<T> {

    public static <T> RootModel<T> of(Root<T> root, CriteriaBuilder cb) {
        return new RootModel<>(root, cb);
    }

    private Root<T> root;

    private CriteriaBuilder cb;

    public RootModel(Root<T> root) {
        this.root = root;
    }

    public RootModel(Root<T> root, CriteriaBuilder cb) {
        this.root = root;
        this.cb = cb;
    }

    public Root<T> getRoot() {
        return root;
    }

    /**
     * 获取root的表达式模型
     *
     * @return root expression model
     */
    public RootExpressionModel get() {
        return new RootExpressionModel(root, cb);
    }

    public ExpressionModel get(String name) {
        StringExpression stringExpression = toStringExpression(name);
        return stringExpression.isFunction()
                ? get(stringExpression.getFunction(), stringExpression.getExpression())
                : parseNameExpression(stringExpression.getExpression());
    }

    public FunctionExpressionModel get(String function, String name) {
        return ((StringExpressionModel) parseNameExpression(name)).asFunction(function);
    }

    protected ExpressionModel parseNameExpression(String name) {
        StringTokenizer st = new StringTokenizer(name, ".");
        String segment = st.nextToken();
        ExpressionModel current = get().next(segment);
        while (st.hasMoreTokens()) {
            segment = st.nextToken();
            current = current instanceof StringExpressionModel
                    ? ((StringExpressionModel) current).next(segment, null)
                    : current.next(segment);
        }
        return current;
    }

    protected StringExpression toStringExpression(String s) {
        return QueryUtils.parse(s);
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
