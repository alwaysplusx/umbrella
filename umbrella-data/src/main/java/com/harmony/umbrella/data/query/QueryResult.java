package com.harmony.umbrella.data.query;

import java.util.List;

import org.springframework.data.domain.Page;

/**
 * @author wuxii@foxmail.com
 */
public interface QueryResult<T> {

    <E> E getColumnSingleResult(String column);

    <E> E getColumnSingleResult(String column, Class<E> resultType);

    <E> List<E> getColumnResultList(String column);

    <E> List<E> getColumnResultList(String column, Class<E> resultType);

    <E> E getFunctionResult(String function, String column);

    <E> E getFunctionResult(String function, String column, Class<E> resultType);

    <VO> VO getVoSingleResult(String[] columns, Class<VO> resultType);

    <VO> List<VO> getVoResultList(String[] columns, Class<VO> resultType);

    T getSingleResult();

    T getFirstResult();

    List<T> getAllMatchResult();

    List<T> getResultList();

    Page<T> getResultPage();

    long getCountResult();

}
