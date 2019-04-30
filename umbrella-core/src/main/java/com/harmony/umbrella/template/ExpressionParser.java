package com.harmony.umbrella.template;

/**
 * @author wuxii
 */
public interface ExpressionParser {

    /**
     * 将文本转为一个个的表达式, 其中有些表达式可能是纯文本形式
     *
     * @param text 待解析的文本
     * @return
     */
    Expressions parse(String text);

}
