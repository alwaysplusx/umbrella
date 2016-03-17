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
package com.harmony.umbrella.log.util;

import java.util.regex.Pattern;

/**
 * @author wuxii@foxmail.com
 */
public class ExpressionUtils {

    public static final String ARRAY_EXPRESSION_PATTERN = "[a-zA-Z]+\\[[0-9]+\\]";
    public static final String MAP_EXPRESSION_PATTERN = "[a-zA-Z]+\\[[a-zA-Z]+\\]";
    public static final String METHOD_EXPRESSION_PATTERN = "#[a-zA-Z]+";
    public static final String NAMED_EXPRESSION_PATTERN = "[a-zA-Z]+";
    public static final String NUMBER_EXPRESSION_PATTERN = "[0-9]+";

    protected static final Pattern arrayExpressionPattern = Pattern.compile(ARRAY_EXPRESSION_PATTERN);
    protected static final Pattern mapExpressionPattern = Pattern.compile(MAP_EXPRESSION_PATTERN);
    protected static final Pattern methodExpressionPattern = Pattern.compile(METHOD_EXPRESSION_PATTERN);
    protected static final Pattern namedExpressionPattern = Pattern.compile(NAMED_EXPRESSION_PATTERN);
    protected static final Pattern numberExpressionPattern = Pattern.compile(NUMBER_EXPRESSION_PATTERN);

    public static String cleanExpression(String expression) {
        if (expression == null) {
            return null;
        }
        if (expression.startsWith("{") && expression.endsWith("}")) {
            return expression.substring(1, expression.length() - 1).trim();
        }
        return expression.trim();
    }

    public static boolean isArrayExpression(String expression) {
        return arrayExpressionPattern.matcher(expression).matches();
    }

    public static boolean isMapExpression(String expression) {
        return mapExpressionPattern.matcher(expression).matches();
    }

    public static boolean isMethodExpression(String expression) {
        return methodExpressionPattern.matcher(expression).matches();
    }

    public static boolean isNamedExpression(String expression) {
        return namedExpressionPattern.matcher(expression).matches();
    }

    public static boolean isNumberExpression(String expression) {
        return numberExpressionPattern.matcher(expression).matches();
    }

    public static String getMapExpressionKey(String expression) {
        if (isMapExpression(expression)) {
            return expression.substring(expression.indexOf("[") + 1, expression.length() - 1);
        }
        return null;
    }

    public static int getArrayExpressionIndex(String expression) {
        if (isArrayExpression(expression)) {
            return Integer.parseInt(expression.substring(expression.indexOf("[") + 1, expression.length() - 1));
        }
        return -1;
    }

    public static String getNamedPart(String expression) {
        StringBuilder sb = new StringBuilder();
        sb.append(expression.charAt(0));
        for (int i = 1, max = expression.length(); i < max; i++) {
            if (!Character.isAlphabetic(expression.charAt(i))) {
                break;
            }
            sb.append(expression.charAt(i));
        }
        return sb.toString();
    }

}
