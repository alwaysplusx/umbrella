package com.harmony.umbrella.data.query;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.Assert;

import com.harmony.umbrella.data.CompositionType;

/**
 * 
 * @author wuxii@foxmail.com
 */
class LinkedSpecification<T> implements CompositionSpecification<T>, Serializable {

    private static final long serialVersionUID = -9083093297662256433L;
    private CompositionType compositionType;
    private List<CompositionSpecification> items = new ArrayList<CompositionSpecification>();

    public LinkedSpecification(CompositionType compositionType) {
        Assert.notNull(compositionType, "composition type is null");
        this.compositionType = compositionType;
    }

    @Override
    public CompositionType getCompositionType() {
        return compositionType;
    }

    public void add(CompositionSpecification<T> spec) {
        items.add(spec);
    }

    public void add(Specification<T> spec, CompositionType logical) {
        items.add(new SpecificationWrapper<T>(spec, logical));
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        if (items.isEmpty()) {
            return null;
        }
        Iterator<CompositionSpecification> it = items.iterator();
        // 忽略第一个item的组合类型
        Specification spec = it.next();
        for (; it.hasNext();) {
            CompositionSpecification i = it.next();
            spec = i.getCompositionType().combine(spec, i);
        }
        return spec.toPredicate(root, query, cb);
    }

    @Override
    public String toString() {
        if (items.isEmpty()) {
            return "";
        }
        StringBuilder out = new StringBuilder();
        Iterator<CompositionSpecification> it = items.iterator();
        out.append(it.next());
        for (; it.hasNext();) {
            CompositionSpecification next = it.next();
            out.append(" ").append(next.getCompositionType()).append(" ").append(next);
            if (it.hasNext()) {
                out.append(" ");
            }
        }
        return "(" + out.toString() + ")";
    }

    private static final class SpecificationWrapper<T> implements CompositionSpecification<T>, Serializable {

        private static final long serialVersionUID = 957944078509511795L;
        private Specification spec;
        private CompositionType compositionType;

        public SpecificationWrapper(Specification spec, CompositionType compositionType) {
            Assert.notNull(compositionType, "compositionType is null");
            this.spec = spec;
            this.compositionType = compositionType;
        }

        @Override
        public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
            return spec.toPredicate(root, query, cb);
        }

        @Override
        public String toString() {
            return spec.toString();
        }

        @Override
        public CompositionType getCompositionType() {
            return compositionType;
        }

    }

}
