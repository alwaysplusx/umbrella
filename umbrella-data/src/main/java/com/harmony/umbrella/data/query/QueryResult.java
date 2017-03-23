package com.harmony.umbrella.data.query;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;

/**
 * 查询结果. 该查询结果通过QueryBuilder来构建查询条件, 然后通过解析构建的查询条件得出查询结果.
 * 
 * @author wuxii@foxmail.com
 */
public interface QueryResult<T> {

    /**
     * 获取列值
     * 
     * @param column
     *            列名(字段名)
     * @return column result
     */
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

    List<T> getResultList();

    Page<T> getResultPage();

    long getCountResult();

    <E> E getSingleResult(Selections<T> selections);

    <E> E getSingleResult(Selections<T> selections, Class<E> resultType);

    <E> E getFirstResult(Selections<T> selections);

    <E> E getFirstResult(Selections<T> selections, Class<E> resultType);

    <E> List<E> getResultList(Selections<T> selections);

    <E> List<E> getResultList(Selections<T> selections, Class<E> resultType);

    <E> Page<E> getResultPage(Selections<T> selections);

    <E> Page<E> getResultPage(Selections<T> selections, Class<E> resultType);

    public interface Selections<T> {

        List<Expression<?>> selection(Root<T> root, CriteriaBuilder cb);
    }

}
