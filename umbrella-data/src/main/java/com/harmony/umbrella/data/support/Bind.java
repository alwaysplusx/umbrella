package com.harmony.umbrella.data.support;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.harmony.umbrella.data.Logical;

/**
 * 
 * @author wuxii@foxmail.com
 */
public class Bind<T> implements Serializable, LogicalSpecification<T> {

    private static final long serialVersionUID = 1L;
    private Logical logical;
    private List<LogicalSpecification> items = new ArrayList<LogicalSpecification>();

    public Bind() {
    }

    public Bind(Logical logical) {
        this.logical = logical;
    }

    public void add(LogicalSpecification<T> spec) {
        items.add(spec);
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
        Predicate predicate = null;
        if (items.isEmpty()) {
            return predicate;
        }
        Iterator<LogicalSpecification> it = items.iterator();
        predicate = it.next().toPredicate(root, query, cb);
        for (; it.hasNext();) {
            LogicalSpecification spec = it.next();
            Predicate right = spec.toPredicate(root, query, cb);
            if (spec.isOr()) {
                predicate = cb.or(predicate, right);
            } else {
                predicate = cb.and(predicate, right);
            }
        }
        return predicate;
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        Iterator it = items.iterator();
        out.append("[").append(logical).append("] ");
        for (; it.hasNext();) {
            out.append(it.next()).append(" ");
        }
        return out.toString();
    }

}
