package com.harmony.umbrella.data.query.specs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import com.harmony.umbrella.data.query.QueryBuilder.Attribute;

/**
 * @author wuxii@foxmail.com
 */
public class JoinSpecification<T> implements Specification<T>, Serializable {

    private static final long serialVersionUID = -6845664868430573972L;
    private List<Attribute> attrs;

    public JoinSpecification(List<Attribute> attrs) {
        this.attrs = new ArrayList<>(attrs);
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        for (Attribute attr : attrs) {
            root.join(attr.name, attr.joniType == null ? JoinType.LEFT : attr.joniType);
        }
        return null;
    }

}
