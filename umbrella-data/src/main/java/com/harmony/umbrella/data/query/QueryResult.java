package com.harmony.umbrella.data.query;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 查询结果. 该查询结果通过QueryBuilder来构建查询条件, 然后通过解析构建的查询条件得出查询结果.
 *
 * @author wuxii@foxmail.com
 */
public interface QueryResult<T> {

    /**
     * 单实体结果查询
     */
    T getSingleResult();

    /**
     * 符合查询条件的首个实体
     *
     * @return 符合条件的首个结果
     */
    T getFirstResult();

    /**
     * 符合条件的结果集
     *
     * @return 符合条件的结果集
     */
    List<T> getResultList();

    /**
     * 获取符合条件的所有集合
     *
     * @return 符合条件的集合
     */
    List<T> getAllResult();

    /**
     * 符合条件的分页结果集
     *
     * @return result page
     */
    Page<T> getResultPage();

    /**
     * 定制列的单结果查询
     *
     * @param selections  定制查询列
     * @param resultClass 期待结果类型
     * @return 符合条件的单个结果
     */
    <E> E getSingleResult(Selections<T> selections, Class<E> resultClass);

    /**
     * 定制列的符合条件的首个结果
     *
     * @param selections  定制查询列
     * @param resultClass 期待结果类型
     * @return 符合条件的首个结果
     */
    <E> E getFirstResult(Selections<T> selections, Class<E> resultClass);

    /**
     * 定制列的结果集查询
     *
     * <b>此结果集不含分页信息</b>
     *
     * @param selections  定制列
     * @param resultClass 期待结果类型
     * @return 符合条件的结果集
     */
    <E> List<E> getResultList(Selections<T> selections, Class<E> resultClass);

    /**
     * 获取符合条件的所有结果集
     *
     * @param selections  定制列
     * @param resultClass 期待的结果类型
     * @param <E>
     * @return 符合条件的结果集合
     */
    <E> List<E> getAllResult(Selections<T> selections, Class<E> resultClass);

    /**
     * 定制列的分页结果集
     *
     * @param countName   进行统计的count name
     * @param selections  定制列
     * @param resultClass 期待结果类型
     * @return 分页结果集
     */
    <E> Page<E> getResultPage(String countName, Selections<T> selections, Class<E> resultClass);

    /**
     * 获取指定列的分页结果集(统计字段带入distinct)
     *
     * @param countName   统计的countName
     * @param selections  结果选取的字段
     * @param resultClass 结果类型
     * @param <E>         结果集
     * @return
     */
    <E> Page<E> getDistinctResultPage(String countName, Selections<T> selections, Class<E> resultClass);

    /**
     * 统计符合条件的结果总数
     *
     * @return 结果总数
     */
    long count();

    /**
     * count结果
     *
     * @param countName 被统计的字段
     * @return
     */
    long count(String countName);

    /**
     * distinct count
     *
     * @param countName 被统计的字段
     * @return
     */
    long countDistinct(String countName);

}
