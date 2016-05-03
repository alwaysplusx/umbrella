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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import com.harmony.umbrella.el.ObjectFormat;
import com.harmony.umbrella.util.StringUtils;

/**
 * @author wuxii@foxmail.com
 */
public class Template implements Iterable<TemplateExpression> {

    public static final char DELIM_START = '{';

    public static final char DELIM_END = '}';

    public static final String EXPRESSION_DELIMITER = ".";

    protected final String templateText;
    private char delimiterStart;
    private char delimiterEnd;

    public Template(String templateText) {
        this(templateText, DELIM_START, DELIM_END);
    }

    public Template(String templateText, char delimiterStart, char delimiterEnd) {
        this.templateText = templateText;
        this.delimiterStart = delimiterStart;
        this.delimiterEnd = delimiterEnd;
    }

    @Override
    public Iterator<TemplateExpression> iterator() {
        return iterator(delimiterStart, delimiterEnd, EXPRESSION_DELIMITER);
    }

    public Iterator<TemplateExpression> iterator(String expressionDelimiter) {
        return iterator(delimiterStart, delimiterEnd, expressionDelimiter);
    }

    public Iterator<TemplateExpression> iterator(char startDelimiter, char endDelimiter, String expressionDelimiter) {
        List<TemplateExpression> expressions = new ArrayList<TemplateExpression>();
        StringTokenizer st = new StringTokenizer(templateText);

        int index = 0;
        boolean isText = true;
        String currentDelimiter = startDelimiter + "";

        while (st.hasMoreTokens()) {
            String token = st.nextToken(currentDelimiter);
            if (index > 0 && StringUtils.isNotBlank(token)) {
                token = token.substring(1);
            }
            expressions.add(new TemplateExpression(token, expressionDelimiter, isText, index));
            currentDelimiter = ((isText = !isText) ? startDelimiter : endDelimiter) + "";
            index++;
        }

        return expressions.iterator();
    }

    public String format(Object... args) {
        return format(args, null);
    }

    public String format(Object[] args, ObjectFormat format) {
        StringBuilder out = new StringBuilder();
        Iterator<TemplateExpression> it = iterator();
        int index = 0;
        while (it.hasNext()) {
            TemplateExpression templateExpression = it.next();
            if (templateExpression.isText()) {
                out.append(templateExpression.getExpressionText());
            } else if (index >= args.length) {
                out.append(delimiterStart).append(templateExpression.getExpressionText()).append(delimiterEnd);
            } else {
                out.append(format == null ? String.valueOf(args[index++]) : format.format(args[index++]));
            }
        }
        return out.toString();
    }

    public String getTemplateText() {
        return templateText;
    }

    public char getDelimiterStart() {
        return delimiterStart;
    }

    public char getDelimiterEnd() {
        return delimiterEnd;
    }

}
