package com.harmony.umbrella.template;

/**
 * @author wuxii
 */
public interface Expressions extends Iterable<Expression> {

    /**
     * 表达式集合对应的原文本
     *
     * @return 原文本
     */
    String getText();

}
