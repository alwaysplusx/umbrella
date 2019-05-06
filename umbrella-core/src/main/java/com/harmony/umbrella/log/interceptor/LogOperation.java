package com.harmony.umbrella.log.interceptor;

import com.harmony.umbrella.log.Level;
import com.harmony.umbrella.log.annotation.Logging;
import com.harmony.umbrella.log.annotation.Scope;
import com.harmony.umbrella.template.Expression;
import com.harmony.umbrella.template.ExpressionParser;
import com.harmony.umbrella.template.Expressions;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author wuxii
 */
@Getter
@Builder
public class LogOperation {

    private Scope defaultScope;
    private String module;
    private String action;
    private String message;
    private Level level;
    private ExpressionOperation keyExpressionOperation;
    /**
     * 来自{@link Logging#bindings()}
     */
    private Map<String, ExpressionOperation> bindings;

    public List<ScopeExpression> parseMessage(ExpressionParser parser) {
        Expressions exps = parser.parse(message);
        List<ScopeExpression> result = new ArrayList<>();
        for (Expression exp : exps) {
            result.add(getBindingOrDefault(exp));
        }
        return result;
    }

    public ScopeExpression getKeyExpression() {
        return null;
    }

    protected ScopeExpression getBindingOrDefault(Expression exp) {
        if (exp.isPlainText() || !bindings.containsKey(exp.getExpression())) {
            return new ScopeExpression(defaultScope == null ? Scope.IN : defaultScope, exp);
        }
        ExpressionOperation binding = bindings.get(exp.getExpression());
        return new ScopeExpression(binding.getText(), binding.getScope(), exp);
    }

}
