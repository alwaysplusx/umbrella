package com.harmony.umbrella.data.query.specs;

import java.io.Serializable;
import java.util.Collection;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import com.harmony.umbrella.data.util.QueryUtils;

/**
 * @author wuxii@foxmail.com
 */
public class GrouppingSpecification<T> implements Specification<T>, Serializable {

    private static final long serialVersionUID = 5937892268297805525L;
    private Collection<String> groupingProperties;

    public GrouppingSpecification(Collection<String> grouping) {
        this.groupingProperties = grouping;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        for (String p : groupingProperties) {
            query.groupBy(QueryUtils.toExpressionRecursively(root, p));
        }
        return null;
    }

}
