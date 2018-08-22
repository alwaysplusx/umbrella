package com.harmony.umbrella.data;

import com.harmony.umbrella.data.model.ExpressionModel;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;

/**
 * 表达式释义
 *
 * @author wuxii@foxmail.com
 */
public interface ExpressionOperator {

    /**
     * 根据自定义的内容对表达式进行释义
     *
     * @param x  表达式左值
     * @param y  表达式右值
     * @param cb CriteriaBuilder
     * @return 条件断言
     */
    Predicate explain(ExpressionModel x, Object y, CriteriaBuilder cb);

}
