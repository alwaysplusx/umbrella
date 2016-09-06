package com.harmony.umbrella.data.support;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.harmony.umbrella.data.Logical;
import com.harmony.umbrella.data.Operator;
import com.harmony.umbrella.data.Specification;
import com.harmony.umbrella.data.util.QueryUtils;

/**
 * 
 * @author wuxii@foxmail.com
 */
public class Bind<T> implements Serializable, Specification<T> {

    private static final long serialVersionUID = 1L;
    private Logical logical;
    private List<BindItem> items = new ArrayList<BindItem>();

    private Logical defaultItemLogical;

    public Bind() {
    }

    public Bind(Logical logical) {
        this.logical = logical;
    }

    public void addCondition(String name, Object value, Operator operator) {
        addCondition(name, value, operator, defaultItemLogical);
    }

    public void addCondition(String name, Object value, Operator operator, Logical logical) {
        this.items.add(new BindItem<>(name, operator, value, logical));
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public boolean isAnd() {
        return Logical.isAnd(logical);
    }

    public boolean isOr() {
        return Logical.isOr(logical);
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        Predicate predicate = cb.conjunction();
        if (items.isEmpty()) {
            return predicate;
        }
        for (BindItem bi : items) {
            if (Logical.OR.equals(bi.logical)) {
                predicate = cb.and(predicate, bi.toPredicate(root, query, cb));
            }
        }
        return predicate;
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        Iterator<BindItem> it = items.iterator();
        out.append("[").append(logical).append("] ");
        for (; it.hasNext();) {
            out.append(it.next()).append(" ");
        }
        return out.toString();
    }

    static final class BindItem<T> implements Serializable, Specification<T> {

        private static final long serialVersionUID = 1L;
        String name;
        Operator operator;
        Object value;
        Logical logical;

        public BindItem(String name, Operator operator, Object value, Logical logical) {
            this.name = name;
            this.operator = operator;
            this.value = value;
            this.logical = logical;
        }

        @Override
        public String toString() {
            return logical + " " + name + " " + operator + " " + "?";
        }

        @Override
        public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
            Expression<Object> x = QueryUtils.toExpressionRecursively(root, name);
            return operator.explain(x, cb, value);
        }

    }
}
