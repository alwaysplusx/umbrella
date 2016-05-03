package com.harmony.umbrella.monitor.matcher;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.aspectj.weaver.tools.PointcutExpression;
import org.aspectj.weaver.tools.PointcutParser;
import org.aspectj.weaver.tools.PointcutPrimitive;

import com.harmony.umbrella.monitor.ResourceMatcher;
import com.harmony.umbrella.util.ClassUtils;

/**
 * 方法表达式匹配验证
 * <p>
 * 基于Aspectj来验证表达式与方法匹配的模式 <a
 * href="http://www.cnblogs.com/yudy/archive/2012/03/22/2411175.html"
 * >http://www.cnblogs.com/yudy/archive/2012/03/22/2411175.html</a>
 * 
 * @author wuxii@foxmail.com
 */
public class MethodExpressionMatcher implements ResourceMatcher<Method> {

    protected static final PointcutExpressionFactory factoryInstance = new PointcutExpressionFactory();

    private static final Set<PointcutPrimitive> SUPPORTED_PRIMITIVES = new HashSet<PointcutPrimitive>();

    private Map<String, MethodMatcher> matchers = new HashMap<String, MethodMatcher>();

    static {
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.EXECUTION);
    }

    @Override
    public boolean match(String pattern, Method resource) {
        if (!matchers.containsKey(pattern)) {
            matchers.put(pattern, new MethodMatcher(pattern));
        }
        return matchers.get(pattern).match(resource);
    }

    private class MethodMatcher {

        private final PointcutExpression pointcutExpression;

        public MethodMatcher(String expression) {
            this.pointcutExpression = factoryInstance.create(expression);
        }

        public boolean match(Method method) {
            return pointcutExpression.matchesMethodExecution(method).maybeMatches();
        }

        @SuppressWarnings("unused")
        public boolean matchInType(final Class<?> clazz) {
            return pointcutExpression.couldMatchJoinPointsInType(clazz);
        }

        public String getExpression() {
            return pointcutExpression.getPointcutExpression();
        }

        @Override
        public String toString() {
            return "method matcher of pattern:" + getExpression();
        }

    }

    private static class PointcutExpressionFactory {

        public PointcutExpression create(String expression) {
            PointcutParser parser = getDefaultPointcutParser();
            return parser.parsePointcutExpression(expression);
        }

        public PointcutParser getDefaultPointcutParser() {
            ClassLoader classLoader = ClassUtils.getDefaultClassLoader();
            PointcutParser parser = PointcutParser.getPointcutParserSupportingSpecifiedPrimitivesAndUsingSpecifiedClassLoaderForResolution(
                    SUPPORTED_PRIMITIVES, classLoader);
            return parser;
        }
    }

}
