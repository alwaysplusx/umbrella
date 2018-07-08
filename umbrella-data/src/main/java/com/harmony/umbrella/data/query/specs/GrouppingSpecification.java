package com.harmony.umbrella.data.query.specs;

import java.io.Serializable;
import java.util.Collection;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import com.harmony.umbrella.data.query.Selections;

/**
 * 
 * @author wuxii@foxmail.com
 */
public class GrouppingSpecification<T> implements Specification<T>, Serializable {

    private static final long serialVersionUID = 5937892268297805525L;
    private Selections<T> selections;

    public GrouppingSpecification(Collection<String> columns) {
        this.selections = Selections.of(columns.toArray(new String[columns.size()]));
    }
    
    public GrouppingSpecification(Selections<T> selections) {
        this.selections = selections;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        query.groupBy(selections.select(root, query, cb));
        return null;
    }

}