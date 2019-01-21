package com.harmony.umbrella.data;

import com.harmony.umbrella.data.result.ListResult;
import com.harmony.umbrella.data.result.PageResult;
import com.harmony.umbrella.data.result.RowResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
     * 符合条件的分页结果集
     *
     * @return 符合条件的结果集
     */
    List<T> getListResult();

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
    Page<T> getPageResult();

    /**
     * 获取分页结果集
     *
     * @param pageable
     * @return
     */
    Page<T> getPageResult(Pageable pageable);

    /**
     * 统计符合条件的结果总数
     *
     * @return 结果总数
     */
    default long count() {
        return count(Selections.ofCount());
    }

    default long countDistinct() {
        return count(Selections.ofCount(true));
    }

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
    ListResult getListResult(Selections selections);

    /**
     * 获取全部结果
     *
     * @param selections 指定字段
     * @return
     */
    ListResult getAllResult(Selections selections);

    /**
     * 分页获取指定字段分页结果集
     *
     * @param selections 指定字段
     * @param pageable   pageable
     * @return
     */
    PageResult getPageResult(Selections selections, Pageable pageable);

    /**
     * 统计符合条件的结果总数
     *
     * @param selections 指定字段(只能为一个字段)
     * @return 结果总数
     */
    long count(Selections selections);

}
