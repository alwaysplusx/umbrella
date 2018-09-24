package com.harmony.umbrella.data.query;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

/**
 * 查询结果. 该查询结果通过QueryBuilder来构建查询条件, 然后通过解析构建的查询条件得出查询结果.
 *
 * @author wuxii@foxmail.com
 */
public interface QueryResult<T> {

    /**
     * 结果集的查询条件
     *
     * @return 查询条件
     */
    QueryBundle<T> getQueryBundle();

    /**
     * 单实体结果查询
     */
    Optional<T> getSingleResult();

    /**
     * 符合查询条件的首个实体
     *
     * @return 符合条件的首个结果
     */
    Optional<T> getFirstResult();

    /**
     * 符合条件的结果集(包含bundle中的分页条件)
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
     * 获取分页结果集
     *
     * @return 分页结果集
     */
    default Page<T> getResultPage() {
        QueryBundle<T> bundle = getQueryBundle();
        return getResultPage(bundle.getPageNumber(), bundle.getPageSize());
    }

    Page<T> getResultPage(int page, int size);

    /**
     * 根据指定列获取结果
     *
     * @param selections 需要获取的列
     * @return
     */
    Optional<Result> getSingleResult(Selections<T> selections);

    Optional<Result> getFirstResult(Selections<T> selections);

    ResultList getResultList(Selections<T> selections);

    ResultList getAllResult(Selections<T> selections);

    ResultPage getPageResult(Selections<T> selections);

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
