package com.harmony.umbrella.data;

import com.harmony.umbrella.data.result.ResultList;
import com.harmony.umbrella.data.result.ResultPage;
import com.harmony.umbrella.data.result.RowResult;
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
     * 符合条件的分页结果集
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
    Page<T> getResultPage();

    /**
     * 获取分页结果集
     *
     * @param page page index
     * @param size page size
     * @return
     */
    Page<T> getResultPage(int page, int size);

    /**
     * 根据指定列获取结果
     *
     * @param selections 指定字段
     * @return
     */
    RowResult getSingleResult(Selections selections);

    /**
     * 根据指定字段查询获取结果集
     *
     * @param selections 指定字段
     * @return
     */
    RowResult getFirstResult(Selections selections);

    /**
     * 根据指定字段获取结果集
     *
     * @param selections 指定字段
     * @return
     */
    ResultList getResultList(Selections selections);

    /**
     * 获取全部结果
     *
     * @param selections 指定字段
     * @return
     */
    ResultList getAllResult(Selections selections);

    /**
     * 分页获取指定字段分页结果集
     *
     * @param page       page index
     * @param size       page size
     * @param selections 指定字段
     * @return
     */
    ResultPage getResultPage(int page, int size, Selections selections);

    /**
     * 统计符合条件的结果总数
     *
     * @return 结果总数
     */
    long count();

    /**
     * count结果
     *
     * @param name 被统计的字段
     * @return
     */
    long count(String name);

    /**
     * distinct count
     *
     * @param name 被统计的字段
     * @return
     */
    long countDistinct(String name);

}
