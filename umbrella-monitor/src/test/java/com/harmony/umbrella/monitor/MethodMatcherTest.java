/*
 * Copyright 2002-2014 t
he original author or authors.
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
package com.harmony.umbrella.monitor;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.aspectj.weaver.tools.ContextBasedMatcher;
import org.aspectj.weaver.tools.PointcutDesignatorHandler;
import org.aspectj.weaver.tools.PointcutExpression;
import org.aspectj.weaver.tools.PointcutParameter;
import org.aspectj.weaver.tools.PointcutParser;
import org.aspectj.weaver.tools.PointcutPrimitive;
import org.junit.Test;

/**
 * @author wuxii@foxmail.com
 */
public class MethodMatcherTest {

    // PatternParser parser = new
    // PatternParser(MethodMonitor.DEFAULT_METHOD_PATTERN);
    // Pointcut pointcut = parser.parsePointcut();
    // PointcutExpressionImpl expressionImpl = new
    // PointcutExpressionImpl(pointcut, null, null, null);
    // System.out.println(expressionImpl);
    // boolean inType =
    // expressionImpl.couldMatchJoinPointsInType(MethodMatcherTest.class);
    // System.out.println(inType);
    public static void main(String[] args) {
    }

    @Test
    public void test() {
        PointcutExpression expression = create();
        assertNotNull(expression);
        assertTrue(expression.couldMatchJoinPointsInType(getClass()));
        expression.matchesAdviceExecution(null).maybeMatches();
    }

    public PointcutExpression create() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        PointcutParser parser = initializePointcutParser(classLoader);
        PointcutParameter[] pointcutParameters = new PointcutParameter[0];
        return parser.parsePointcutExpression(MethodMonitor.DEFAULT_METHOD_PATTERN, null, pointcutParameters);
    }

    /**
     * Initialize the underlying AspectJ pointcut parser.
     */
    private PointcutParser initializePointcutParser(ClassLoader cl) {
        PointcutParser parser = PointcutParser
                .getPointcutParserSupportingSpecifiedPrimitivesAndUsingSpecifiedClassLoaderForResolution(SUPPORTED_PRIMITIVES, cl);
        parser.registerPointcutDesignatorHandler(new BeanNamePointcutDesignatorHandler());
        return parser;
    }

    private static final Set<PointcutPrimitive> SUPPORTED_PRIMITIVES = new HashSet<PointcutPrimitive>();

    static {
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.EXECUTION);
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.ARGS);
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.REFERENCE);
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.THIS);
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.TARGET);
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.WITHIN);
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.AT_ANNOTATION);
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.AT_WITHIN);
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.AT_ARGS);
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.AT_TARGET);
    }

    /**
     * Handler for the Spring-specific {@code bean()} pointcut designator
     * extension to AspectJ. <p>This handler must be added to each pointcut
     * object that needs to handle the {@code bean()} PCD. Matching context is
     * obtained automatically by examining a thread local variable and therefore
     * a matching context need not be set on the pointcut.
     */
    private class BeanNamePointcutDesignatorHandler implements PointcutDesignatorHandler {

        private static final String BEAN_DESIGNATOR_NAME = "bean";

        @Override
        public String getDesignatorName() {
            return BEAN_DESIGNATOR_NAME;
        }

        @Override
        public ContextBasedMatcher parse(String expression) {
            return null;
        }
    }

    /*
    *//**
     * Matcher class for the BeanNamePointcutDesignatorHandler. <p>Dynamic
     * match tests for this matcher always return true, since the matching
     * decision is made at the proxy creation time. For static match tests, this
     * matcher abstains to allow the overall pointcut to match even when
     * negation is used with the bean() pointcut.
     */
    /*
     * private class BeanNameContextMatcher implements ContextBasedMatcher {
     * 
     * private final NamePattern expressionPattern;
     * 
     * public BeanNameContextMatcher(String expression) { this.expressionPattern
     * = new NamePattern(expression); }
     * 
     * @Override
     * 
     * @SuppressWarnings("rawtypes")
     * 
     * @Deprecated public boolean couldMatchJoinPointsInType(Class someClass) {
     * return (contextMatch(someClass) == FuzzyBoolean.YES); }
     * 
     * @Override
     * 
     * @SuppressWarnings("rawtypes")
     * 
     * @Deprecated public boolean couldMatchJoinPointsInType(Class someClass,
     * MatchingContext context) { return (contextMatch(someClass) ==
     * FuzzyBoolean.YES); }
     * 
     * @Override public boolean matchesDynamically(MatchingContext context) {
     * return true; }
     * 
     * @Override public FuzzyBoolean matchesStatically(MatchingContext context)
     * { return contextMatch(null); }
     * 
     * @Override public boolean mayNeedDynamicTest() { return false; }
     * 
     * private FuzzyBoolean contextMatch(Class<?> targetType) { String
     * advisedBeanName = getCurrentProxiedBeanName(); if (advisedBeanName ==
     * null) { // no proxy creation in progress // abstain; can't return YES,
     * since that will make pointcut with // negation fail return
     * FuzzyBoolean.MAYBE; } if
     * (BeanFactoryUtils.isGeneratedBeanName(advisedBeanName)) { return
     * FuzzyBoolean.NO; } if (targetType != null) { boolean isFactory =
     * FactoryBean.class.isAssignableFrom(targetType); return
     * FuzzyBoolean.fromBoolean(matchesBeanName(isFactory ?
     * BeanFactory.FACTORY_BEAN_PREFIX + advisedBeanName : advisedBeanName)); }
     * else { return FuzzyBoolean.fromBoolean(matchesBeanName(advisedBeanName)
     * || matchesBeanName(BeanFactory.FACTORY_BEAN_PREFIX + advisedBeanName)); }
     * }
     * 
     * private boolean matchesBeanName(String advisedBeanName) { if
     * (this.expressionPattern.matches(advisedBeanName)) { return true; } if
     * (beanFactory != null) { String[] aliases =
     * beanFactory.getAliases(advisedBeanName); for (String alias : aliases) {
     * if (this.expressionPattern.matches(alias)) { return true; } } } return
     * false; } }
     */
}
