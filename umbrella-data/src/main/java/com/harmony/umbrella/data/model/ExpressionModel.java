package com.harmony.umbrella.data.model;

import javax.persistence.criteria.Expression;

/**
 * @author wuxii
 */
public interface ExpressionModel {

    /**
     * 表达式的全路径表示. e.g.: user.name, count(user.name)
     *
     * @return 查询的字段全路径表示
     */
    default String getPath() {
        if (!hasPrevious()) {
            return getName();
        }
        String path = previous().getPath();
        return isFunction()
                ? getName() + "(" + path + ")"
                : path != null ? path + "." + getName() : getName();
    }

    /**
     * 查询表达式的当前名称: 表达式链路的各个节点的名称[user].[name]
     *
     * @return 链路中的某个节点名称
     */
    String getName();

    /**
     * 查询字段的jpa表达式
     *
     * @return jpa表达式
     */
    Expression getExpression();

    /**
     * 链路表达式的前一个模型
     *
     * @return previous expression model
     */
    ExpressionModel previous();

    /**
     * 获取表达式的下一个链路
     *
     * @param name 下一个节点的名称
     * @return
     */
    ExpressionModel next(String name);

    /**
     * 此节点是否是function
     *
     * @return
     */
    boolean isFunction();

    /**
     * 是否有上一个节点
     *
     * @return
     */
    default boolean hasPrevious() {
        return previous() != null;
    }

}
