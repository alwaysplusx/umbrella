package com.harmony.umbrella.log.expression;

import java.lang.reflect.Method;

import com.harmony.umbrella.log.IllegalExpressionException;
import com.harmony.umbrella.log.util.ExpressionUtils;

/**
 * 表达式指定为一个get方法，通过value对应的get方法取值。
 * <p>
 * 表达式默认由#号开头, 全字母连接
 *
 * @author wuxii@foxmail.com
 */
public class MethodExpressionResolver extends ComparableExpressionResolver {

    protected static final MethodExpressionResolver INSTANCE = new MethodExpressionResolver();

    public MethodExpressionResolver() {
        super(METHOD);
    }

    @Override
    public boolean support(String expression, Object value) {
        return ExpressionUtils.isMethodExpression(expression);
    }

    @Override
    public Object resolve(String expression, Object value) throws IllegalExpressionException {
        if (support(expression, value)) {
            Class<?> targetClass = value.getClass();
            try {
                Method method = targetClass.getDeclaredMethod(readMethodName(expression.substring(1)));
                return method.invoke(value);
            } catch (Exception e) {
                throw new IllegalExpressionException("unsupported method expression " + expression, e);
            }
        }
        throw new IllegalExpressionException("unsupported method expression " + expression);
    }

    protected static final String readMethodName(String name) {
        return "get" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }
}
