package com.harmony.umbrella.data.util;

import java.util.List;

/**
 * @author wuxii@foxmail.com
 */
public interface QueryResult<T> {

    T getSingleResult();

    T getFirstResult();

    List<T> getResultList();

    List<T> getResultPage();

}
