package com.harmony.umbrella.data;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 * selection 字段
 */
public interface Selection {

    Column generate(Root<?> root, CriteriaQuery<?> query, CriteriaBuilder cb);

}