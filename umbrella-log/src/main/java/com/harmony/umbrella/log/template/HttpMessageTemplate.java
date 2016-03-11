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
package com.harmony.umbrella.log.template;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;

import com.harmony.umbrella.log.HttpTemplate;
import com.harmony.umbrella.log.IllegalExpressionException;

/**
 * @author wuxii@foxmail.com
 */
public class HttpMessageTemplate extends MessageTemplate implements HttpTemplate {

    private final HttpServletRequest request;

    public HttpMessageTemplate(Method method, HttpServletRequest request) {
        super(method);
        this.request = request;
    }

    @Override
    protected Object getUnrecognizedExpressionValue(final String expression, Object target, Object result, Object[] params) {
        String exp = clearExpression(expression);

        String firstExp = getExpressionByIndex(exp, 0);
        String secondExp = getExpressionByIndex(exp, 1);

        Object value = null;

        if (firstExp != null && secondExp != null) {
            String name = getNamePart(secondExp);

            if (firstExp.equals("request")) {
                value = getRequestValue(name);

            } else if (firstExp.equals("session")) {
                value = getSessionValue(name);

            } else if (firstExp.equals("parameter")) {
                return request.getParameter(name);

            } else if (firstExp.equals("application")) {
                value = getApplicationValue(name);

            }

            if (isComplexExpression(secondExp)) {
                value = getComplexExpressionValue(secondExp, value);
            }

            String aheadExpression = firstExp + "." + secondExp + ".";
            if (exp.length() > aheadExpression.length()) {
                String subExpression = exp.substring(aheadExpression.length());
                return getValue(subExpression, value);
            }

        }

        return value;
    }

    private String getExpressionByIndex(String expression, int index) {
        try {
            return expression.split("\\.")[index];
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    private String getNamePart(String expression) {
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

    private Object getRequestValue(String name) {
        if (name.startsWith("#")) {
            try {
                return accessValue(name.substring(1), request);
            } catch (Exception e) {
                throw new IllegalExpressionException("request expression start with '#' but not get method of " + name, e);
            }
        }
        return request.getAttribute(name);
    }

    private Object getSessionValue(String name) {
        if (name.startsWith("#")) {
            try {
                return accessValue(name.substring(1), request.getSession());
            } catch (Exception e) {
                throw new IllegalExpressionException("session expression start with '#' but not get method of " + name, e);
            }
        }
        return request.getSession().getAttribute(name);
    }

    private Object getApplicationValue(String name) {
        if (name.startsWith("#")) {
            try {
                return accessValue(name.substring(1), request.getServletContext());
            } catch (Exception e) {
                throw new IllegalExpressionException("session expression start with '#' but not get method of " + name, e);
            }
        }
        return request.getServletContext().getAttribute(name);
    }

    @Override
    public HttpServletRequest getHttpRequest() {
        return request;
    }

}
