package com.harmony.umbrella.data.query.select;

import com.harmony.umbrella.data.query.Column;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 * @author wuxii
 */
public interface SelectionGenerator<X> {

    Column generate(Root<X> root, CriteriaQuery<?> query, CriteriaBuilder cb);

}
