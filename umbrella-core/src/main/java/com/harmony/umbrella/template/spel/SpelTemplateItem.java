package com.harmony.umbrella.template.spel;

import com.harmony.umbrella.template.Expression;
import com.harmony.umbrella.template.TemplateItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.EvaluationException;

/**
 * @author wuxii
 */
@Slf4j
public class SpelTemplateItem implements TemplateItem {

    private Expression expression;
    private org.springframework.expression.Expression spel;

    public SpelTemplateItem(Expression expression, org.springframework.expression.Expression spel) {
        this.expression = expression;
        this.spel = spel;
    }

    @Override
    public Expression getExpression() {
        return expression;
    }

    @Override
    public Object getValue(Object rootObject) {
        try {
            return spel.getValue(rootObject);
        } catch (EvaluationException e) {
            if (log.isDebugEnabled()) {
                log.warn("{}, evaluation expression failed", expression, e);
            }
            return "${" + expression.getText() + "}";
        }
    }

}
