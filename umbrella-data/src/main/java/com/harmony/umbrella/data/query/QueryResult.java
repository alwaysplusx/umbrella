package com.harmony.umbrella.data.query;

import java.util.List;

import com.harmony.umbrella.data.domain.Page;
import com.harmony.umbrella.data.domain.Pageable;

/**
 * @author wuxii@foxmail.com
 */
public interface QueryResult<T> {

    <E> E getColumnSingleResult(String column);

    <E> E getColumnSingleResult(String column, Class<E> columnType);

    <E> List<E> getColumnResultList(String column);

    <E> List<E> getColumnResultList(String column, Class<E> columnType);

    <E> E getFunctionResult(String function, String column);

    <E> E getFunctionResult(String function, String column, Class<E> functionResultType);

    <VO> VO getVoResult(String[] columns, Class<VO> voType);

    <VO> List<VO> getVoResultList(String[] columns, Class<VO> voType);

    T getSingleResult();

    T getFirstResult();

    List<T> getResultList();

    Page<T> getResultPage();

    Page<T> getResultPage(int pageNumber, int pageSize);

    Page<T> getResultPage(Pageable pageable);

    long getCountResult();

}
