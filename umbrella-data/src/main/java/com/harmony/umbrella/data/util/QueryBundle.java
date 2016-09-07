package com.harmony.umbrella.data.util;

import com.harmony.umbrella.data.Specification;
import com.harmony.umbrella.data.domain.Sort;

/**
 * @author wuxii@foxmail.com
 */
public class QueryBundle<M> {

    Specification<M> specification;
    Class<M> entityClass;

    int pageOffSet;
    int pageSize;

    Sort sort;

}
