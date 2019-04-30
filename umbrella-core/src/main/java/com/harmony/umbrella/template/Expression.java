package com.harmony.umbrella.template;

/**
 * @author wuxii
 */
public interface Expression {

    /**
     * 原文本, 如${#foo.bar}
     *
     * @return 原文本
     */
    String getText();

    /**
     * 去除表达式符号的文本, 如#foo.bar
     *
     * @return 表达式
     */
    String getExpression();

    /**
     * 表达式是否是纯文本的表达式
     *
     * @return 是否是纯文本
     */
    boolean isPlainText();

}
