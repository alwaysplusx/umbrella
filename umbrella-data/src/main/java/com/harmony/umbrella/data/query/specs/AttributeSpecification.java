package com.harmony.umbrella.data.query.specs;

import java.io.Serializable;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import com.harmony.umbrella.data.query.QueryBuilder.Attribute;
import com.harmony.umbrella.data.query.QueryBuilder.FetchAttributes;
import com.harmony.umbrella.data.query.QueryBuilder.JoinAttributes;

/**
 * @author wuxii@foxmail.com
 */
public class AttributeSpecification<T> implements Specification<T>, Serializable {

    private static final long serialVersionUID = -6845664868430573972L;
    private FetchAttributes fetchs;
    private JoinAttributes joins;

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        if (fetchs != null && !fetchs.getAttributes().isEmpty()) {
            for (Attribute attr : fetchs.getAttributes()) {
                root.fetch(attr.name, attr.joniType == null ? JoinType.INNER : attr.joniType);
            }
        }
        if (joins != null && !joins.getAttributes().isEmpty()) {
            for (Attribute attr : joins.getAttributes()) {
                root.join(attr.name, attr.joniType == null ? JoinType.INNER : attr.joniType);
            }
        }
        return null;
    }

}
