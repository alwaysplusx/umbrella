package com.harmony.umbrella.el;

/**
 * @author wuxii@foxmail.com
 */
public interface ExpressionResolver {

    Object resolver(Expression expression, Object val);

}
