package com.harmony.umbrella.data.query;

import java.util.Set;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import com.harmony.umbrella.data.query.QueryBuilder.FetchAttributes;
import com.harmony.umbrella.data.query.QueryBuilder.JoinAttributes;

/**
 * 查询条件包
 * 
 * @author wuxii@foxmail.com
 */
public interface QueryBundle<M> {

    /**
     * 查询的实体
     * 
     * @return entity class
     */
    Class<M> getEntityClass();

    /**
     * 查询的条件
     * 
     * @return 查询条件
     */
    Specification<M> getCondition();

    /**
     * 查询的特性
     * 
     * @return query feature
     */
    int getQueryFeature();

    /**
     * 抓取的字段
     * 
     * @return fetch attrs
     */
    FetchAttributes getFetchAttributes();

    /**
     * join的字段
     * 
     * @return join attrs
     */
    JoinAttributes getJoinAttributes();

    /**
     * 分组条件
     * 
     * @return 分组条件
     */
    Set<String> getGrouping();

    /**
     * 分页条件的页码
     * 
     * @return page number
     */
    int getPageNumber();

    /**
     * 分页条件的页面内容数
     * 
     * @return page size
     */
    int getPageSize();

    /**
     * 排序条件
     * 
     * @return sort
     */
    Sort getSort();

}
