package com.harmony.umbrella.template;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

/**
 * @author wuxii
 */
@Getter
@Setter
@NoArgsConstructor
public class Expressions implements Iterable<Expression>, Serializable {

    /**
     * 表达式expressions对应的全文本
     */
    private String text;
    /**
     * 表达式切割后的结果
     */
    private List<Expression> expressions;

    public Expressions(String text, List<Expression> expressions) {
        Assert.notNull(expressions, "expression is not allow null");
        this.text = text;
        this.expressions = expressions;
    }

    @Override
    public Iterator<Expression> iterator() {
        return expressions.iterator();
    }

    @Override
    public String toString() {
        return text + ", item size " + expressions.size();
    }
}
