package com.harmony.umbrella.data.query;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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

    List<T> getAllMatchResult();

    List<T> getResultList();

    List<T> getResultList(int pageNumber, int pageSize);

    List<T> getResultList(Pageable pageable);

    Page<T> getResultPage();

    Page<T> getResultPage(int pageNumber, int pageSize);

    Page<T> getResultPage(Pageable pageable);

    long getCountResult();

}
