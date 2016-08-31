package com.harmony.umbrella.plugin.log.expression;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import com.harmony.umbrella.log.annotation.Logging.Scope;
import com.harmony.umbrella.plugin.log.access.TypedAccessorChain;
import com.harmony.umbrella.plugin.log.access.TypedAccessorChainImpl;

/**
 * @author wuxii@foxmail.com
 */
public class LogValueContext implements ValueContext {

    private TypedAccessorChain accessorChain = TypedAccessorChainImpl.createDefault();

    boolean failFast;

    private final Map<String, Object> inContext = new HashMap<String, Object>();
    private final Map<String, Object> outContext = new HashMap<String, Object>();

    @Override
    public Object find(Expression exp) {
        return findValue(exp, exp.isScopeOf(Scope.IN) ? inContext : outContext);
    }

    public Object findValue(Expression expression, Map<String, Object> context) {
        if (!expression.hasTokens()) {
            return null;
        }

        if (expression.isText()) {
            return expression.getExpressionText();
        }

        if (context.containsKey(expression.getExpressionText())) {
            return context.get(expression.getExpressionText());
        }

        Object value = null;
        Iterator<String> it = expression.iterator();
        String token = it.next();

        // 首层表达式处理. 如果直接在首层能找到就直接匹配
        if (context.containsKey(token)) {
            value = context.get(token);
            if (!it.hasNext()) {
                return value;
            }
            token = it.next();
        } else {
            value = context;
        }

        do {
            StringTokenizer st = new StringTokenizer(token, "[]");
            while (st.hasMoreTokens()) {
                Object tmp = accessorChain.getValue(st.nextToken(), value);
                if (tmp == null && (st.hasMoreTokens() || it.hasNext())) {
                    return "$unknow value$";
                }
                value = tmp;
            }
            if (!it.hasNext()) {
                break;
            }
            token = it.next();
        } while (true);

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
