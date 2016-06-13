package com.harmony.umbrella.log.support;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.harmony.umbrella.log.access.AccessorHolder;
import com.harmony.umbrella.log.annotation.Scope;
import com.harmony.umbrella.log.expression.Expression;
import com.harmony.umbrella.log.expression.ValueContext;

/**
 * @author wuxii@foxmail.com
 */
public class LogValueContext implements ValueContext {

    private AccessorHolder accessorHolder = AccessorHolder.createAccessorHolder();

    boolean failFast = false;

    private final Map<String, Object> inContext = new HashMap<String, Object>();
    private final Map<String, Object> outContext = new HashMap<String, Object>();

    @Override
    public Object find(String exp, Scope scope) {
        Expression expression = new Expression(exp);
        expression.setScope(scope);
        return find(expression);
    }

    @Override
    public Object find(Expression exp) {
        Map<String, Object> context = exp.isScopeOf(Scope.IN) ? inContext : outContext;
        if (context.containsKey(exp.getExpressionText())) {
            return context.get(exp.getExpressionText());
        }
        return findValue(exp, context);
    }

    public Object findValue(Expression expression, Map<String, Object> context) {
        if (expression.isEmpty()) {
            return null;
        }
        if (expression.isText()) {
            return expression.getExpressionText();
        }
        Object value = context;
        Iterator<String> it = expression.iterator(".[]");
        try {
            while (it.hasNext()) {
                String token = it.next();
                Object tmp = accessorHolder.getValue(token, value);
                if (tmp == null && it.hasNext()) {
                    if (failFast) {
                        throw new IllegalArgumentException("got null value from " + value + " " + token);
                    }
                    return "$unknow value$";
                }
                value = tmp;
            }
        } catch (Exception e) {
            return "$inaccessible value$";
        }
        return value;
    }

    @Override
    public Map<String, Object> getInContext() {
        return inContext;
    }

    @Override
    public Map<String, Object> getOutContext() {
        return outContext;
    }

}
