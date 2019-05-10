package com.harmony.umbrella.template.spel;

import com.harmony.umbrella.template.Expression;
import com.harmony.umbrella.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.EvaluationException;

/**
 * @author wuxii
 */
@Slf4j
public class SpelTemplate<T extends Expression> implements Template<T> {

    private T expression;
    private org.springframework.expression.Expression spel;

    public SpelTemplate(T expression, org.springframework.expression.Expression spel) {
        this.expression = expression;
        this.spel = spel;
    }

    @Override
    public T getExpression() {
        return expression;
    }

    @Override
    public Object getValue(Object rootObject) {
        try {
            return rootObject instanceof EvaluationContext
                    ? spel.getValue((EvaluationContext) rootObject)
                    : spel.getValue(rootObject);
        } catch (EvaluationException e) {
            if (log.isDebugEnabled()) {
                log.warn("{}, evaluation expression failed", expression, e);
            }
            return "${" + expression.getText() + "}";
        }
    }

    @Override
    public String toString() {
        return "SpelTemplate{" +
                "expression=" + expression +
                '}';
    }
}
