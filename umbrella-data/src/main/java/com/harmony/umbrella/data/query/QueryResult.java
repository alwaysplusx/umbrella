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
     *
     * @return single entity result
     */
    T getSingleResult();

    /**
     * 符合查询条件的首个实体
     *
     * @return 符合条件的首个结果
     */
    T getFirstResult();

    /**
     * 获取特定范围结果集
     *
     * @return 范围结果集
     */
    List<T> getRangeResult();

    /**
     * 符合条件的结果集
     *
     * <b>此结果集不分页, 即为所有符合条件的实体</b>
     *
     * @return 符合条件的结果集
     */
    List<T> getResultList();

    /**
     * 符合条件的分页结果集
     *
     * @return result page
     */
    Page<T> getResultPage();

    /**
     * 统计符合条件的结果总数
     *
     * @return 结果总数
     */
    long countResult();

    /**
     * 获取列值并返回期待的类型
     *
     * @param column     列名
     * @param resultType 期待类型
     * @return resultType column result
     */
    <E> E getSingleResult(String column, Class<E> resultType);

    /**
     * 获取列值并返回期待的类型
     *
     * @param column     列名
     * @param resultType 期待类型
     * @return resultType column result
     */
    <E> E getSingleResult(String[] column, Class<E> resultType);

    /**
     * 获取列值并返回期待的类型
     *
     * @param column     列名
     * @param resultType 期待类型
     * @return resultType column result
     */
    <E> List<E> getResultList(String column, Class<E> resultType);

    /**
     * 获取列值并返回期待的类型
     *
     * @param column     列名
     * @param resultType 期待类型
     * @return resultType column result
     */
    <E> List<E> getResultList(String[] column, Class<E> resultType);

    /**
     * 分页的列值结果
     *
     * @param column     列名
     * @param resultType 期待类型
     * @return range column result
     */
    <E> List<E> getRangeResult(String column, Class<E> resultType);

    /**
     * 分页的列值结果
     *
     * @param column     列名
     * @param resultType 期待类型
     * @return range column result
     */
    <E> List<E> getRangeResult(String[] column, Class<E> resultType);

    /**
     * 查询计算单列的function结果
     *
     * @param function   function name
     * @param column     column name
     * @param resultType 期待的值类型
     * @return column function result
     */
    <E> E getFunctionResult(String function, String column, Class<E> resultType);

    /**
     * 定制列的单结果查询
     *
     * @param selections 定制查询列
     * @param resultType 期待结果类型
     * @return 符合条件的单个结果
     */
    <E> E getSingleResult(Selections<T> selections, Class<E> resultType);

    /**
     * 定制列的符合条件的首个结果
     *
     * @param selections 定制查询列
     * @param resultType 期待结果类型
     * @return 符合条件的首个结果
     */
    <E> E getFirstResult(Selections<T> selections, Class<E> resultType);

    /**
     * 定制列的结果集查询
     *
     * <b>此结果集不含分页信息</b>
     *
     * @param selections 定制列
     * @param resultType 期待结果类型
     * @return 符合条件的结果集
     */
    <E> List<E> getResultList(Selections<T> selections, Class<E> resultType);

    /**
     * 定制列的符合条件的分页结果集
     *
     * @param selections 定制列
     * @param resultType 期待结果类型
     * @return 分页结果集
     */
    <E> List<E> getRangeResult(Selections<T> selections, Class<E> resultType);

    /**
     * 定制列的分页结果集
     *
     * @param selections 定制列
     * @param resultType 期待结果类型
     * @return 分页结果集
     */
    <E> Page<E> getResultPage(Selections<T> selections, Class<E> resultType);

}
