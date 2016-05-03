/*
 * Copyright 2012-2016 the original author or authors.
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

import com.harmony.umbrella.el.Expression;


/**
 * @author wuxii@foxmail.com
 */
public class TemplateExpression extends Expression {

    private boolean isText;
    private int index;

    public TemplateExpression(String expressionText, String delimiter, boolean isText, int index) {
        super(expressionText, delimiter);
        this.isText = isText;
        this.index = index;
    }

    public boolean isText() {
        return isText;
    }

    public int getIndex() {
        return index;
    }

}
