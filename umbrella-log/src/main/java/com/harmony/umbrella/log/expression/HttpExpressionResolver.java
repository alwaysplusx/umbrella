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
package com.harmony.umbrella.log.expression;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.harmony.umbrella.log.IllegalExpressionException;

/**
 * http表达式支持，负责解析http相关的表达式
 * <p>
 * http信息主要与value有关，当值是HttpSession等等类型时候用特定的解析方式来提取值
 * </p>
 *
 * @author wuxii@foxmail.com
 */
public class HttpExpressionResolver extends ComparableExpressionResolver {

    protected static final HttpExpressionResolver INSTANCE = new HttpExpressionResolver();

    public HttpExpressionResolver() {
        super(HTTP);
    }

    @Override
    public boolean support(String expression, Object value) {
        return isHttpValue(value);
    }

    @Override
    public Object resolve(String expression, Object value) throws IllegalExpressionException {
        if (support(expression, value)) {
            if (value instanceof HttpServletRequest) {
                return getRequestValue(expression, (HttpServletRequest) value);
            } else if (value instanceof HttpSession) {
                return getSessionValue(expression, (HttpSession) value);
            } else if (value instanceof ServletContext) {
                return getApplicationValue(expression, (ServletContext) value);
            }
            throw new IllegalExpressionException("illegal http value " + value);
        }
        throw new IllegalExpressionException("illegal http expression " + expression);
    }

    private Object getRequestValue(String name, HttpServletRequest request) {
        if (name.startsWith("#")) {
            return accessMethod(name, request);
        } else if (name.startsWith("$")) {
            return request.getParameter(name.substring(1));
        }
        return request.getAttribute(name);
    }

    private Object getSessionValue(String name, HttpSession session) {
        if (name.startsWith("#")) {
            return accessMethod(name, session);
        }
        return session.getAttribute(name);
    }

    private Object getApplicationValue(String name, ServletContext application) {
        if (name.startsWith("#")) {
            return accessMethod(name, application);
        }
        return application.getAttribute(name);
    }

    private Object accessMethod(String name, Object value) {
        try {
            return MethodExpressionResolver.INSTANCE.resolve(name, value);
        } catch (Exception e) {
            throw new IllegalExpressionException("session expression start with '#' but not get method of " + name, e);
        }
    }

    public boolean isHttpValue(Object value) {
        return value instanceof HttpServletRequest //
                || value instanceof HttpSession //
                || value instanceof ServletContext;
    }
}
