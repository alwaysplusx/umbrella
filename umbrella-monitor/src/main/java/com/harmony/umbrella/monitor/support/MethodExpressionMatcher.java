/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.harmony.umbrella.monitor.support;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.aspectj.weaver.tools.PointcutExpression;
import org.aspectj.weaver.tools.PointcutParser;
import org.aspectj.weaver.tools.PointcutPrimitive;

import com.harmony.umbrella.monitor.ResourceMatcher;
import com.harmony.umbrella.util.ClassUtils;

/**
 * 方法表达式匹配验证
 * <p>
 * 基于Aspectj来验证表达式与方法匹配的模式
 * <a href="http://www.cnblogs.com/yudy/archive/2012/03/22/2411175.html">http://www.cnblogs.com/yudy/archive/2012/03/22/2411175.html</a>
 * 
 * @author wuxii@foxmail.com
 */
public class MethodExpressionMatcher implements ResourceMatcher<Method> {

    protected static final PointcutExpressionFactory factoryInstance = new PointcutExpressionFactory();
    
    private final PointcutExpression pointcutExpression;

    public MethodExpressionMatcher(String expression) {
        this.pointcutExpression = factoryInstance.create(expression);
    }

    @Override
    public boolean matches(Method method) {
        return pointcutExpression.matchesMethodExecution(method).maybeMatches();
    }

    public boolean matchInType(Class<?> clazz) {
        return pointcutExpression.couldMatchJoinPointsInType(clazz);
    }

    @Override
    public String getExpression() {
        return pointcutExpression.getPointcutExpression();
    }

    @Override
    public String toString() {
        return "method matcher of pattern:" + getExpression();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getExpression() == null) ? 0 : getExpression().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MethodExpressionMatcher other = (MethodExpressionMatcher) obj;
        if (getExpression() == null) {
            if (other.getExpression() != null)
                return false;
        } else if (!getExpression().equals(other.getExpression()))
            return false;
        return true;
    }

    private static final Set<PointcutPrimitive> SUPPORTED_PRIMITIVES = new HashSet<PointcutPrimitive>();

    static {
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.EXECUTION);
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
