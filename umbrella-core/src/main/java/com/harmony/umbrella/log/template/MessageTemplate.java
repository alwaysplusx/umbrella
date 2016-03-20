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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.harmony.umbrella.log.IllegalExpressionException;
import com.harmony.umbrella.log.Message;
import com.harmony.umbrella.log.Template;
import com.harmony.umbrella.log.annotation.Log;
import com.harmony.umbrella.log.expression.ExpressionResolverChain;
import com.harmony.umbrella.log.message.FormattedMessage;
import com.harmony.umbrella.log.message.SimpleMessage;
import com.harmony.umbrella.log.util.ExpressionUtils;

/**
 * @author wuxii@foxmail.com
 */
public class MessageTemplate implements Template {

    protected final Log ann;
    protected final Method method;
    protected final String template;
    protected final String idExpression;

    // use get method replace
    private List<String> _templateExpressions;
    private String _placeholdTemplate;

    public MessageTemplate(Method method) {
        this.ann = method.getAnnotation(Log.class);
        if (ann == null) {
            throw new IllegalArgumentException("method not have @Log annotation");
        }
        if ("".equals(ann.message())) {
            throw new IllegalArgumentException("method not a template method, check @Log#message() property");
        }
        this.template = ann.message();
        this.idExpression = ann.id();
        this.method = method;
    }

    @Override
    public Message newMessage(Object target, Object result, Object[] arguments) {
        String[] expressions = getTemplateExpression();
        if (expressions.length == 0) {
            return new SimpleMessage(template);
        }
        List<Object> args = new ArrayList<Object>();
        for (String expression : expressions) {
            args.add(getExpressionValue(expression, target, result, arguments));
        }
        return new FormattedMessage(getPlaceholderTemplate(), args.toArray(new Object[args.size()]));
    }

    @Override
    public String getId(Object target, Object result, Object[] arguments) {
        if (hasIdExpression()) {
            Object value = getExpressionValue(idExpression, target, result, arguments);
            return String.valueOf(value);
        }
        return null;
    }

    /**
     * 根据表达式关键字获取对应的值
     */
    protected Object getExpressionValue(final String expression, Object target, Object result, Object[] arguments) {
        if (expression == null || "".equals(expression)) {
            throw new IllegalExpressionException("expression is null or empty");
        }
        String specExp = ExpressionUtils.cleanExpression(expression);
        return resolveValue(specExp, wrapObject(target, result, arguments));
    }

    protected Object wrapObject(Object target, Object result, Object[] arguments) {
        return new Holder(target, result, arguments);
    }

    /**
     * 一串表达式根据<code>.</code>分割为一个个单独的表达式，再根据各个表达式求值
     */
    protected Object resolveValue(String expression, Object value) {
        return newChain().doChain(expression, value);
    }

    private ExpressionResolverChain newChain() {
        try {
            return ExpressionResolverChain.createChain(ann);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 截取表达式，截取完后的表达式包含了表达式的前后字符
     * <p>
     * <code>"数据{0.name}保存{result}" 对应的结果为 [{0.name}, {result}] </code>
     */
    public String[] getTemplateExpression() {
        if (_templateExpressions == null) {
            // get template from @Log
            List<String> result = new ArrayList<String>();
            int delim = template.indexOf(DELIM_START);
            if (delim > 0) {
                for (int i = delim + 1; i < template.length(); i++) {
                    char curChar = template.charAt(i);
                    if (curChar == DELIM_END) {
                        result.add(template.substring(delim, i + 1));
                    } else if (curChar == DELIM_START) {
                        delim = i;
                    }
                }
            }
            _templateExpressions = result;
        }
        return _templateExpressions.toArray(new String[_templateExpressions.size()]);
    }

    /**
     * 将原版的表达式中的表达式部分使用占位符号替换
     */
    public String getPlaceholderTemplate() {
        if (_placeholdTemplate == null) {
            String templateCopy = template;
            String[] templateExpression = getTemplateExpression();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < templateExpression.length; i++) {
                String curExp = templateExpression[i];
                int templateIndex = templateCopy.indexOf(curExp);
                sb.append(templateCopy.substring(0, templateIndex)).append(DELIM_START).append(i).append(DELIM_END);
                templateCopy = templateCopy.substring(templateIndex + curExp.length());
            }
            sb.append(templateCopy);
            _placeholdTemplate = sb.toString();
        }
        return _placeholdTemplate;
    }

    public boolean hasIdExpression() {
        return idExpression != null && !"".equals(idExpression);
    }

    protected static class Holder extends ArrayList<Object> {

        private static final long serialVersionUID = 1L;
        final Object target;
        final Object result;
        final Object[] arguments;

        public Holder(Object target, Object result, Object[] arguments) {
            super(Arrays.asList(arguments));
            this.target = target;
            this.result = result;
            this.arguments = arguments;
        }

        public Object getTarget() {
            return target;
        }

        public Object getResult() {
            return result;
        }

        public Object[] getArguments() {
            return arguments;
        }

        public Object[] getArgs() {
            return arguments;
        }

        public Object[] getArg() {
            return arguments;
        }

    }

}
