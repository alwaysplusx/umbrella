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

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.harmony.umbrella.log.Logs;
import com.harmony.umbrella.log.Message;
import com.harmony.umbrella.log.Template;
import com.harmony.umbrella.log.annotation.Log;
import com.harmony.umbrella.log.message.FormattedMessage;
import com.harmony.umbrella.log.message.SimpleMessage;

/**
 * @author wuxii@foxmail.com
 */
public class MessageTemplate implements Template {

    private static final com.harmony.umbrella.log.Log log = Logs.getLog(MessageTemplate.class);

    private Log ann;
    private Method method;
    private String template;

    private String placeholdTemplate;
    private String idExpression;
    private List<String> templateExpressions;

    public MessageTemplate(Method method) {
        this.ann = method.getAnnotation(Log.class);
        if (ann == null) {
            throw new IllegalArgumentException("method not have @Log annotation is not template message");
        }
        if ("".equals(ann.message())) {
            throw new IllegalArgumentException("method not a template method, check @Log#message() property");
        }
        this.method = method;
        this.template = ann.message();
        this.idExpression = ann.id();
    }

    @Override
    public Message newMessage(Object target, Object result, Object[] params) {
        String[] expressions = getTemplateExpression();

        if (expressions.length == 0) {
            return new SimpleMessage(template);
        }

        List<Object> args = new ArrayList<Object>();

        for (String expression : expressions) {
            args.add(getExpressionValue(expression, target, result, params));
        }

        return new FormattedMessage(getPlaceholdTemplate(), args.toArray(new Object[args.size()]));
    }

    /**
     * 通过解析@Log上的id注解获取id
     */
    @SuppressWarnings("unchecked")
    @Override
    public Serializable getId(Object target, Object result, Object[] params) {
        if (hasIdExpression()) {
            Object value = getExpressionValue(idExpression, target, result, params);
            if (value instanceof Serializable) {
                return (Serializable) value;
            }
            return String.valueOf(value);
        }
        return null;
    }

    private Object getExpressionValue(String expression, Object target, Object result, Object[] params) {
        // 去除花括号以及多余的空格 {expression} -> expression
        expression = expression.substring(1, expression.length() - 1).trim();

        if ("target".equals(expression)) {
            return target;

        } else if ("result".equals(expression)) {
            return result;

        } else if ("id".equals(expression)) {
            return getId(target, result, params);

        } else if (expression.startsWith("target.")) {
            return getValue(expression.substring("target.".length()), target);

        } else if (expression.startsWith("result.")) {
            return getValue(expression.substring("result.".length()), result);

        } else if (expression.startsWith("id.")) {
            Object id = getId(target, result, params);
            return getValue(expression.substring("id.".length()), id);

        } else {
            int dotIndex = expression.indexOf(".");
            int paramIndex = -1;

            try {

                if (dotIndex == -1) {
                    paramIndex = Integer.parseInt(expression);
                } else {
                    paramIndex = Integer.parseInt(expression.substring(0, dotIndex));
                }

                if (paramIndex >= 0 && paramIndex < params.length) {
                    String subExpression = expression.substring(dotIndex + 1);
                    if (dotIndex == -1 || "".equals(subExpression)) {
                        return params[paramIndex];
                    }
                    return getValue(expression.substring(dotIndex + 1), params[paramIndex]);
                } else {
                    log.warn("expression {}, argument index more than argument array length", expression);
                    return null;
                }

            } catch (NumberFormatException e) {
                log.error("expression {}, not start with a number and not match any keywords");
            }
        }
        return null;
    }

    protected Object getValue(String expression, Object obj) {
        Object target = obj;
        Class<?> targetClass = target.getClass();

        StringTokenizer st = new StringTokenizer(expression, ".");
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            try {
                Method readMethod = targetClass.getDeclaredMethod(readMethodName(token));
                target = readMethod.invoke(target);
            } catch (Exception e) {
                try {
                    Field field = targetClass.getDeclaredField(token);
                    field.setAccessible(true);
                    target = field.get(target);
                } catch (Exception e1) {
                }
                log.error("target class {} not have field of name {}", targetClass.getName(), token);
                return null;
            }
            targetClass = target.getClass();
        }
        return target;
    }

    private String readMethodName(String name) {
        return "get" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }

    public boolean hasIdExpression() {
        return idExpression != null && !"".equals(idExpression);
    }

    public String getIdExpression() {
        return idExpression;
    }

    /**
     * 截取表达式，截取完后的表达式包含了表达式的前后字符
     * <p>
     * <code>"数据{0.name}保存{result}" 对应的结果为 [{0.name}, {result}] </code>
     * 
     * @return
     */
    public String[] getTemplateExpression() {
        if (templateExpressions == null) {

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

            templateExpressions = result;
        }

        return templateExpressions.toArray(new String[templateExpressions.size()]);
    }

    /**
     * 将原版的表达式中的表达式部分使用占位符号替换
     */
    public String getPlaceholdTemplate() {
        if (placeholdTemplate == null) {

            String template = getTemplate();
            String[] templateExpression = getTemplateExpression();

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < templateExpression.length; i++) {
                String curExp = templateExpression[i];
                int templateIndex = template.indexOf(curExp);
                sb.append(template.substring(0, templateIndex)).append(DELIM_START).append(i).append(DELIM_END);
                template = template.substring(templateIndex + curExp.length());
            }
            sb.append(template);
            placeholdTemplate = sb.toString();
        }

        return placeholdTemplate;
    }

    public Method getMethod() {
        return method;
    }

    /**
     * 重注解中获取配置的message模版
     * 
     * @see {@linkplain Log#message()}
     */
    public String getTemplate() {
        return template;
    }

}